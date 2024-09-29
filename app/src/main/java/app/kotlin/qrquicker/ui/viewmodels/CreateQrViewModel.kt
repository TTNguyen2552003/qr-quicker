package app.kotlin.qrquicker.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kotlin.qrquicker.MAX_INPUT_TEXT_LENGTH
import app.kotlin.qrquicker.QrQuickerApplication
import app.kotlin.qrquicker.data.QrCreationRepository
import app.kotlin.qrquicker.helpers.generateQRCode
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A UI state data class for the QR code creation screen.
 *
 * @property notificationEnable Whether notifications are enabled for QR code creation.
 *
 * @property textInput The text input used to generate the QR code.
 *
 * @property qrCodeResult The resulting QR code as a Bitmap (null if no QR code is generated).
 *
 */
data class CreateQrUiState(
    val notificationEnable: Boolean = false,
    val textInput: String = "",
    val qrCodeResult: Bitmap? = null
)

/**
 * A ViewModel responsible for handling the QR code creation logic, managing the UI state,
 * and interacting with the repository to generate and save QR codes.
 *
 * @param qrCreationRepository The repository responsible for QR code creation.
 */
class CreateQrViewModel(private val qrCreationRepository: QrCreationRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateQrUiState> =
        MutableStateFlow(value = CreateQrUiState())
    val uiState: StateFlow<CreateQrUiState> = _uiState.asStateFlow()

//     Job used to debounce text input changes to avoid excessive QR code generation.
    private var debounceJob: Job? = null

//    Delay used to debounce QR code generation.
    private val debounceDelay: Long = 150

    /**
     * Updates the `qrCodeResult` in the UI state based on the current text input.
     * If the input is empty, clears the QR code result. Otherwise, generates a new QR code.
     */
    private fun updateQRCodeResult() {
        val currentTextInputValue: String = _uiState.value.textInput
        if (currentTextInputValue.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(qrCodeResult = null)
            }
        } else {
            val newQRCode: Bitmap = generateQRCode(text = currentTextInputValue)
            _uiState.update { currentState ->
                currentState.copy(qrCodeResult = newQRCode)
            }
        }
    }

    /**
     * Updates the `textInput` field in the UI state. If the new input exceeds the maximum
     * allowed length, it truncates the input to the maximum length. It also debounces the
     * QR code generation to avoid generating QR codes for every keystroke.
     *
     * @param newValue The new text input value to be updated.
     */
    fun updateTextInput(newValue: String) {
        _uiState.update { currentState ->
            if (newValue.length <= MAX_INPUT_TEXT_LENGTH)
                currentState.copy(textInput = newValue)
            else
                currentState.copy(
                    textInput = newValue.substring(
                        startIndex = 0,
                        endIndex = MAX_INPUT_TEXT_LENGTH
                    )
                )
        }

//         Cancels the previous debounce job if it exists.
        debounceJob?.cancel()

//         Launches a new debounce job to delay QR code generation.
        debounceJob = viewModelScope.launch {
            delay(debounceDelay)
            updateQRCodeResult()
        }
    }

    /**
     * Saves the QR code using the current text input if it is not empty.
     * It triggers a coroutine to save the QR code in the background using the repository.
     */
    val saveQrCode: () -> Unit = {
        val currentTextInput = _uiState.value.textInput
        if (currentTextInput.isNotEmpty()) {
            viewModelScope.launch {
                withContext(IO) {
                    qrCreationRepository.createQrCode(text = _uiState.value.textInput)
                }
            }
        }
    }

    /**
     * Updates the `notificationEnable` field in the UI state to reflect the current notification permission state.
     *
     * @param newState The new notification permission state (enabled or disabled).
     */
    fun updateNotificationPermissionState(newState: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(notificationEnable = newState)
        }
    }

    companion object {
        /**
         * Factory to create an instance of `CreateQrViewModel`, initializing it with the
         * appropriate repository from the application's container.
         */
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as QrQuickerApplication)
                val appContainer = application.appContainer
                CreateQrViewModel(qrCreationRepository = appContainer.qrCreationRepository)
            }
        }
    }
}