package app.kotlin.qrquicker.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ScanningAreaState {
    NO_CAMERA_PERMISSION,
    INACTIVE,
    ACTIVE
}

data class ScanQrUiState(
    val scanningAreaState: ScanningAreaState = ScanningAreaState.NO_CAMERA_PERMISSION,
    val isFlashLightOn: Boolean = false,
    val qrCodeResult: String = "",
    val autoCopyOptionEnable: Boolean = false,
    val autoOpenWeblinkOptionEnable: Boolean = false
)

class ScanQrViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ScanQrUiState> =
        MutableStateFlow(value = ScanQrUiState())
    val uiState: StateFlow<ScanQrUiState> = _uiState.asStateFlow()

    fun updateScanningAreaState(newState: ScanningAreaState) {
        _uiState.update { currentState ->
            currentState.copy(
                scanningAreaState = newState,
//                When the scanning area state change to INACTIVE that mean there
                qrCodeResult = if (newState == ScanningAreaState.INACTIVE) {
                    ""
                } else {
                    currentState.qrCodeResult
                },
                isFlashLightOn = if (newState == ScanningAreaState.INACTIVE) {
                    false
                } else {
                    currentState.isFlashLightOn
                }
            )
        }
    }

    val startScanning: () -> Unit = {
        updateScanningAreaState(newState = ScanningAreaState.ACTIVE)
    }

    val stopScanning: () -> Unit = {
        updateScanningAreaState(newState = ScanningAreaState.INACTIVE)
    }

    fun toggleFlashLight() {
        _uiState.update { currentState ->
            currentState.copy(isFlashLightOn = !currentState.isFlashLightOn)
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