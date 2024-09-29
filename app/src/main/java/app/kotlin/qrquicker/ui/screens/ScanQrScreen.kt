package app.kotlin.qrquicker.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_BODY
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_ID
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_TITLE
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.helpers.copyToClipBoard
import app.kotlin.qrquicker.helpers.gotoAppSetting
import app.kotlin.qrquicker.helpers.makeNotification
import app.kotlin.qrquicker.helpers.openWeblink
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.CameraPreviewView
import app.kotlin.qrquicker.ui.components.OptionMenu
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.gap200
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.gap50
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.gap700
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onPrimaryColor
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.primaryColor
import app.kotlin.qrquicker.ui.styles.shapeSmall
import app.kotlin.qrquicker.ui.styles.surfaceColor
import app.kotlin.qrquicker.ui.viewmodels.ScanQrUiState
import app.kotlin.qrquicker.ui.viewmodels.ScanQrViewModel
import app.kotlin.qrquicker.ui.viewmodels.ScanningAreaState
import kotlinx.coroutines.launch

@Composable
fun ScanQrScreen(scanQrViewModel: ScanQrViewModel = viewModel()) {
//    Collect the UI state from the ViewModel
    val scanQrUiState: ScanQrUiState by scanQrViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(all = gap600),
        verticalArrangement = Arrangement.spacedBy(space = gap700),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context: Context = LocalContext.current

//        Lifecycle observer to handle permission checks when the app resumes
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
//                    Check camera permission status
                    val isCameraPermissionAllow = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

//                    Update scanning area state based on permission
                    val newScanningAreaState: ScanningAreaState = if (isCameraPermissionAllow) {
                        ScanningAreaState.INACTIVE
                    } else {
                        ScanningAreaState.NO_CAMERA_PERMISSION
                    }

//                    Update the state in ViewModel
                    scanQrViewModel.updateScanningAreaState(newState = newScanningAreaState)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

//        Request camera permission if not granted
        if (scanQrUiState.scanningAreaState == ScanningAreaState.NO_CAMERA_PERMISSION) {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
//               Update the state in ViewModel
                val newScanningAreaState: ScanningAreaState = if (isGranted) {
                    ScanningAreaState.INACTIVE
                } else {
                    ScanningAreaState.NO_CAMERA_PERMISSION
                }
                scanQrViewModel.updateScanningAreaState(newState = newScanningAreaState)
            }

            LaunchedEffect(key1 = Unit) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

//        Handle QR code scanning results
        LaunchedEffect(key1 = scanQrUiState.qrCodeResult) {
            val currentQrCodeResult = scanQrUiState.qrCodeResult
            if (currentQrCodeResult.isNotEmpty()) {
                launch {
                    if (scanQrUiState.autoCopyOptionEnable) {
                        copyToClipBoard(context = context, textCopy = currentQrCodeResult)
                    }
                }

                launch {
                    if (scanQrUiState.autoOpenWeblinkOptionEnable) {
                        openWeblink(context = context, weblink = currentQrCodeResult)
                    }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(space = gap400),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (scanQrUiState.scanningAreaState) {
//                Scanning area when camera permission is not granted
                ScanningAreaState.NO_CAMERA_PERMISSION -> {
                    Box(
                        modifier = Modifier.size(size = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.place_holder_outline),
                            contentDescription = "place holder outline",
                            modifier = Modifier.size(size = 160.dp),
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(color = onSurfaceColor)
                        )

                        Text(
                            text = stringResource(id = R.string.scan_qr_screen_scanning_area_place_holder_text_without_permission),
                            style = bodySmall.noScale(),
                            textAlign = TextAlign.Center,
                            color = onSurfaceColor
                        )
                    }


                    Button(
                        label = R.string.scan_qr_screen_request_permission_button_label,
                        onPressEvent = { gotoAppSetting(context) }
                    )
                }

//                Scanning area when scanner is inactive
                ScanningAreaState.INACTIVE -> {
                    Image(
                        painter = painterResource(R.drawable.scanning_view_place_holder),
                        contentDescription = "camera view place holder",
                        modifier = Modifier.size(size = 200.dp),
                        colorFilter = ColorFilter.tint(color = onSurfaceColor)
                    )

                    Button(
                        label = R.string.scan_qr_screen_start_scanning_button_label,
                        onPressEvent = scanQrViewModel.startScanning
                    )
                }

                ScanningAreaState.ACTIVE -> {
                    Box(modifier = Modifier.size(size = 200.dp)) {
                        CameraPreviewView(
                            onQrCodeDetected = { result ->
                                scanQrViewModel.updateQrCodeResult(newResult = result)
                            },
                            onScanFailed = {
                                makeNotification(
                                    context = context,
                                    title = DETECT_QR_FAILED_NOTIFICATION_TITLE,
                                    body = DETECT_QR_FAILED_NOTIFICATION_BODY,
                                    id = DETECT_QR_FAILED_NOTIFICATION_ID
                                )
                            },
                            torchEnabled = scanQrUiState.isFlashLightOn
                        )

//                        Flashlight toggle button
                        Box(
                            modifier = Modifier
                                .align(alignment = Alignment.TopEnd)
                                .offset(
                                    x = -gap200,
                                    y = gap200
                                )
                                .drawBehind {
                                    drawRoundRect(
                                        color = primaryColor,
                                        cornerRadius = CornerRadius(shapeSmall.toPx())
                                    )
                                }
                                .padding(all = gap50)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            tryAwaitRelease()
                                            scanQrViewModel.toggleFlashLight()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (scanQrUiState.isFlashLightOn) {
                                        R.drawable.flashlight_on
                                    } else {
                                        R.drawable.flashlight_off
                                    }
                                ),
                                contentDescription = "flashlight icon",
                                modifier = Modifier.size(size = 16.dp),
                                colorFilter = ColorFilter.tint(color = onPrimaryColor)
                            )
                        }
                    }

                    Button(
                        label = R.string.scan_qr_screen_stop_scanning_button_label,
                        onPressEvent = scanQrViewModel.stopScanning
                    )
                }
            }
        }

//        Text field to display scanned QR code result
        TextField(
            placeHolder = R.string.scan_qr_screen_text_field_place_holder,
            value = scanQrUiState.qrCodeResult,
            isReadOnly = true
        )

//         Options menu for auto-copy and auto-open weblink
        OptionMenu(
            option1Description = R.string.scan_qr_screen_option_1_description,
            option1State = scanQrUiState.autoCopyOptionEnable,
            onOption1StateChange = scanQrViewModel.toggleAutoCopyOption,
            option2Description = R.string.scan_qr_screen_option_2_description,
            option2State = scanQrUiState.autoOpenWeblinkOptionEnable,
            onOption2StateChange = scanQrViewModel.toggleAutoOpenWeblinkOption
        )
    }
}
