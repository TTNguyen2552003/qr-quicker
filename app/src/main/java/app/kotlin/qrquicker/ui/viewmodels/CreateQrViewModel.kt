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

data class CreateQrUiState(
    val notificationEnable: Boolean = false,
    val textInput: String = "",
    val qrCodeResult: Bitmap? = null
)

class CreateQrViewModel(private val qrCreationRepository: QrCreationRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateQrUiState> =
        MutableStateFlow(value = CreateQrUiState())
    val uiState: StateFlow<CreateQrUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null
    private val debounceDelay: Long = 150

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

        debounceJob?.cancel()

        debounceJob = viewModelScope.launch {
            delay(debounceDelay)
            updateQRCodeResult()
        }
    }

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

    fun updateNotificationPermissionState(newState: Boolean) {
        _uiState.update { currentState->
            currentState.copy(notificationEnable = newState)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as QrQuickerApplication)
                val appContainer = application.appContainer
                CreateQrViewModel(qrCreationRepository = appContainer.qrCreationRepository)
            }
        }
    }
}