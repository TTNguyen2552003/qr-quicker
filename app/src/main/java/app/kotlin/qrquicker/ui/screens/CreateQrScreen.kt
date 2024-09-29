package app.kotlin.qrquicker.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.helpers.gotoAppSetting
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodyLarge
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.gap100
import app.kotlin.qrquicker.ui.styles.gap300
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.stateLayerHyperlinkColor
import app.kotlin.qrquicker.ui.styles.surfaceColor
import app.kotlin.qrquicker.ui.viewmodels.CreateQrUiState
import app.kotlin.qrquicker.ui.viewmodels.CreateQrViewModel


@Composable
fun CreateQrScreen(
    createQrViewModel: CreateQrViewModel = viewModel(factory = CreateQrViewModel.factory)
) {
//    Collect the UI state from the ViewModel
    val createQrUiState: CreateQrUiState by createQrViewModel.uiState.collectAsState()

    val context: Context = LocalContext.current

//    Lifecycle observer to handle permission checks when the app resumes or start
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
//                Check notification permission state
                val isNotificationPermissionAllowed: Boolean = ContextCompat.checkSelfPermission(
                    context,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

//                Update the state in ViewModel
                createQrViewModel.updateNotificationPermissionState(
                    newState = isNotificationPermissionAllowed
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

//    If the permission is not allow, run a launcher to request
    if (!createQrUiState.notificationEnable) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            createQrViewModel.updateNotificationPermissionState(newState = isGranted)
        }

        LaunchedEffect(key1 = Unit) {
            permissionLauncher.launch(POST_NOTIFICATIONS)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(all = gap600)
    ) {
//        Text field contains the text from user
        TextField(
            placeHolder = R.string.create_qr_screen_text_field_place_holder,
            value = createQrUiState.textInput,
            onValueChange = { newValue: String -> createQrViewModel.updateTextInput(newValue = newValue) }
        )

//        The place holder when the text field is empty and the QR result when the input text is not empty
        if (createQrUiState.textInput.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(size = 256.dp)
                    .align(alignment = Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.place_holder_outline),
                    contentDescription = "",
                    modifier = Modifier.size(size = 256.dp),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(color = onSurfaceColor)
                )

                Text(
                    text = stringResource(id = R.string.create_qr_screen_result_place_holder_text),
                    style = bodyLarge.noScale(),
                    color = onSurfaceColor
                )
            }
        } else {
            Column(
                modifier = Modifier.align(alignment = Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(space = gap300),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                createQrUiState.qrCodeResult?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "",
                        modifier = Modifier.size(size = 256.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Button(
                    label = R.string.create_qr_screen_save_button_label,
                    onPressEvent = createQrViewModel.saveQrCode
                )
            }
        }

//        Rationale and hyperlink when the notification permission is not allowed
        if (!createQrUiState.notificationEnable) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .offset(y = -gap600),
                verticalArrangement = Arrangement.spacedBy(space = gap100),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.create_qr_screen_permission_request_rationale),
                    style = bodySmall.noScale(),
                    color = onSurfaceColor
                )

                Text(
                    text = stringResource(id = R.string.create_qr_screen_text_button_label),
                    style = bodySmall.noScale(),
                    color = stateLayerHyperlinkColor,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                gotoAppSetting(context = context)
                            }
                        )
                    }
                )
            }
        }
    }
}