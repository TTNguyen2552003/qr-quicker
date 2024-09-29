package app.kotlin.qrquicker.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


//Represents the possible states of the QR code scanning area.
enum class ScanningAreaState {
    NO_CAMERA_PERMISSION,   // No permission granted from user
    INACTIVE,   // Permission granted but the scanner is turned off
    ACTIVE,     // The scanner is turned on
}

/**
 * Represents the UI state for the QR code scanning feature.
 *
 * @property scanningAreaState The current state of the scanning area.
 * @property isFlashLightOn Whether the flashlight is currently on.
 * @property qrCodeResult The result of the last successful QR code scan.
 * @property autoCopyOptionEnable Whether the auto-copy option is enabled.
 * @property autoOpenWeblinkOptionEnable Whether the auto-open weblink option is enabled.
 */
data class ScanQrUiState(
    val scanningAreaState: ScanningAreaState = ScanningAreaState.NO_CAMERA_PERMISSION,
    val isFlashLightOn: Boolean = false,
    val qrCodeResult: String = "",
    val autoCopyOptionEnable: Boolean = false,
    val autoOpenWeblinkOptionEnable: Boolean = false
)


//ViewModel for managing the QR code scanning UI state and user interactions.
class ScanQrViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ScanQrUiState> =
        MutableStateFlow(value = ScanQrUiState())
    val uiState: StateFlow<ScanQrUiState> = _uiState.asStateFlow()

    /**
     * Updates the scanning area state and related UI elements.
     *
     * @param newState The new state to set for the scanning area.
     */
    fun updateScanningAreaState(newState: ScanningAreaState) {
        _uiState.update { currentState ->
            currentState.copy(
                scanningAreaState = newState,
                qrCodeResult = if (newState == ScanningAreaState.INACTIVE) {
                    ""  //  Set the result to empty if the scanning area state is INACTIVE
                } else {
                    currentState.qrCodeResult   //  Otherwise, keep the result
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

    /**
     * Updates the QR code scan result.
     *
     * @param newResult The new QR code scan result.
     */
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