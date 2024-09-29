package app.kotlin.qrquicker.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//Representing the state of the image loading area
enum class LoadingImageAreaState {
    NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED, // No permission granted to access media
    INACTIVE, // The area is not active (no image loaded)
    ACTIVE // The area is active with an image loaded
}

/**
 * Represents the UI state for the QR code scanning feature.
 *
 * @property loadingImageAreaState The current state of the loading image area.
 * @property pickedImage The image loaded from Media store by user
 * @property qrCodeResult The result of the last successful QR code scan.
 * @property autoCopyOptionEnable Whether the auto-copy option is enabled.
 * @property autoOpenWeblinkOptionEnable Whether the auto-open weblink option is enabled.
 */
data class DecodeImageUiState(
    val loadingImageAreaState: LoadingImageAreaState = LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED,
    val pickedImage: Uri? = null,
    val qrCodeResult: String = "",
    val autoCopyOptionEnable: Boolean = false,
    val autoOpenWeblinkOptionEnable: Boolean = false
)

//ViewModel to manage the state of the image decoding screen and the interactions
class DecodeImageViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<DecodeImageUiState> =
        MutableStateFlow(value = DecodeImageUiState())
    val uiState: StateFlow<DecodeImageUiState> = _uiState.asStateFlow()

    /**
     * Update the state of the image loading area (e.g., ACTIVE, INACTIVE)'
     *
     * @param newState The new state is set for the loading image area
     */
    fun updateLoadingImageAreaState(newState: LoadingImageAreaState) {
        _uiState.update { currentState ->
            currentState.copy(loadingImageAreaState = newState)
        }
    }

    /**
     * The function catches the load image event and update state
     *
     * @param newImage represent the new uri of the image when it loaded
     */
    fun loadImage(newImage: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(
                loadingImageAreaState = if (newImage != null) {
                    LoadingImageAreaState.ACTIVE // Set state to ACTIVE if an image is loaded
                } else {
                    currentState.loadingImageAreaState // Otherwise, keep the previous state
                },
                pickedImage = newImage // Store the URI of the new image
            )
        }
    }

//     Unload the current image, reset the image state and QR code result
    fun unloadImage() {
        _uiState.update { currentState ->
            currentState.copy(
                loadingImageAreaState = LoadingImageAreaState.INACTIVE,
                pickedImage = null,
                qrCodeResult = ""
            )
        }
    }

    fun updateQrCodeResult(newResult: String) {
        _uiState.update { currentState ->
            currentState.copy(qrCodeResult = newResult)
        }
    }

    val toggleAutoCopyOption: () -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(autoCopyOptionEnable = !currentState.autoCopyOptionEnable)
        }
    }

    val toggleAutoOpenWeblinkOption: () -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(autoOpenWeblinkOptionEnable = !currentState.autoOpenWeblinkOptionEnable)
        }
    }
}
