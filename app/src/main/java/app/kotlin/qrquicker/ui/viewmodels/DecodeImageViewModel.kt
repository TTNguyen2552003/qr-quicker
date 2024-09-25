package app.kotlin.qrquicker.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class LoadingImageAreaState {
    NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED,
    INACTIVE,
    ACTIVE
}

data class DecodeImageUiState(
    val loadingImageAreaState: LoadingImageAreaState = LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED,
    val pickedImage: Uri? = null,
    val qrCodeResult: String = "",
    val autoCopyOptionEnable: Boolean = false,
    val autoOpenWeblinkOptionEnable: Boolean = false
)

class DecodeImageViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<DecodeImageUiState> =
        MutableStateFlow(value = DecodeImageUiState())
    val uiState: StateFlow<DecodeImageUiState> = _uiState.asStateFlow()

    fun updateLoadingImageAreaState(newState: LoadingImageAreaState) {
        _uiState.update { currentState ->
            currentState.copy(loadingImageAreaState = newState)
        }
    }

    fun loadImage(newImage: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(
                loadingImageAreaState = if (newImage != null) {
                    LoadingImageAreaState.ACTIVE
                } else {
                    currentState.loadingImageAreaState
                },
                pickedImage = newImage
            )
        }
    }

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