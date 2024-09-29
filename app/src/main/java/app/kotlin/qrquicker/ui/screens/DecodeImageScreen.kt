package app.kotlin.qrquicker.ui.screens

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_BODY
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_ID
import app.kotlin.qrquicker.DETECT_QR_FAILED_NOTIFICATION_TITLE
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.helpers.analyzeImage
import app.kotlin.qrquicker.helpers.copyToClipBoard
import app.kotlin.qrquicker.helpers.gotoAppSetting
import app.kotlin.qrquicker.helpers.makeNotification
import app.kotlin.qrquicker.helpers.openWeblink
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.OptionMenu
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.gap700
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.surfaceColor
import app.kotlin.qrquicker.ui.viewmodels.DecodeImageUiState
import app.kotlin.qrquicker.ui.viewmodels.DecodeImageViewModel
import app.kotlin.qrquicker.ui.viewmodels.LoadingImageAreaState
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun DecodeImageScreen(decodeImageViewModel: DecodeImageViewModel = viewModel()) {
//    Collect the UI state from the ViewModel
    val decodeImageUiState: DecodeImageUiState by decodeImageViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(space = gap700),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context: Context = LocalContext.current

//        Lifecycle observer to handle permission checks when the app resumes or start
        val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
//                    Check read media images permission state
                    val isPhotosAndMediaPermissionAllowed: Boolean = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                            context.checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                                    context.checkSelfPermission(READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            context.checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                        }

                        else -> {
                            context.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        }
                    }

//                     Update the state in ViewModel
                    val newLoadingImageAreaState: LoadingImageAreaState =
                        if (isPhotosAndMediaPermissionAllowed) {
                            LoadingImageAreaState.INACTIVE
                        } else {
                            LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED
                        }
                    decodeImageViewModel.updateLoadingImageAreaState(newState = newLoadingImageAreaState)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

//        Request read media images permission if not granted
        if (decodeImageUiState.loadingImageAreaState == LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED) {
            val multiplePermissionsLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allGranted = permissions.values.all { it }
                val newLoadingImageAreaState = if (allGranted) {
                    LoadingImageAreaState.INACTIVE
                } else {
                    LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED
                }
                decodeImageViewModel.updateLoadingImageAreaState(newState = newLoadingImageAreaState)
            }

            LaunchedEffect(decodeImageUiState.loadingImageAreaState) {
                val permissionsToRequest = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        arrayOf(READ_MEDIA_IMAGES)
                    }

                    else -> {
                        arrayOf(READ_EXTERNAL_STORAGE)
                    }
                }
                multiplePermissionsLauncher.launch(permissionsToRequest)
            }
        }

//       Handle QR code scanning results
        LaunchedEffect(key1 = decodeImageUiState.qrCodeResult) {
            val currentQrCodeResult = decodeImageUiState.qrCodeResult
            if (currentQrCodeResult.isNotEmpty()) {
                launch {
                    if (decodeImageUiState.autoCopyOptionEnable) {
                        copyToClipBoard(context = context, textCopy = currentQrCodeResult)
                    }
                }

                launch {
                    if (decodeImageUiState.autoOpenWeblinkOptionEnable) {
                        openWeblink(context = context, weblink = currentQrCodeResult)
                    }
                }
            }
        }

//        Create and handle the load image event in app
        val loadImageLauncher: ManagedActivityResultLauncher<String, Uri?> =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { imageUri: Uri? ->
                    analyzeImage(
                        context = context,
                        imageUri = imageUri,
                        onQrCodeDetected = { newResult ->
                            decodeImageViewModel.loadImage(newImage = imageUri)
                            decodeImageViewModel.updateQrCodeResult(newResult = newResult)
                        },
                        onDetectFailed = {
                            decodeImageViewModel.loadImage(newImage = imageUri)
                            decodeImageViewModel.updateQrCodeResult(newResult = "")
                            makeNotification(
                                context = context,
                                title = DETECT_QR_FAILED_NOTIFICATION_TITLE,
                                body = DETECT_QR_FAILED_NOTIFICATION_BODY,
                                id = DETECT_QR_FAILED_NOTIFICATION_ID
                            )
                        }
                    )
                }
            )

        Column(
            verticalArrangement = Arrangement.spacedBy(space = gap400),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (decodeImageUiState.loadingImageAreaState) {
//                Loading image area when no read media images permission granted
                LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED -> {
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
                            text = stringResource(id = R.string.decode_image_screen_loading_image_area_place_holder_text_without_permission),
                            style = bodySmall.noScale(),
                            textAlign = TextAlign.Center,
                            color = onSurfaceColor
                        )
                    }

                    Button(
                        label = R.string.decode_image_screen_request_permission_button_label,
                        onPressEvent = { gotoAppSetting(context = context) }
                    )
                }

//                Loading image area when there is no image loaded
                LoadingImageAreaState.INACTIVE -> {
                    Image(
                        painter = painterResource(R.drawable.image_view_place_holder),
                        contentDescription = "image view place holder",
                        modifier = Modifier.size(size = 200.dp),
                        colorFilter = ColorFilter.tint(color = onSurfaceColor)
                    )

                    Button(
                        label = R.string.decode_image_screen_start_loading_button_label,
                        onPressEvent = { loadImageLauncher.launch(input = "image/*") }
                    )
                }

//                Loading image area when image loaded
                LoadingImageAreaState.ACTIVE -> {
                    Image(
                        painter = rememberAsyncImagePainter(model = decodeImageUiState.pickedImage),
                        contentDescription = "",
                        modifier = Modifier
                            .size(size = 200.dp)
                            .clip(shape = RectangleShape),
                        contentScale = ContentScale.Fit
                    )

                    Button(
                        label = R.string.decode_image_screen_stop_loading_button_label,
                        onPressEvent = { decodeImageViewModel.unloadImage() }
                    )
                }
            }
        }

//        Text field displays the QR code result
        TextField(
            placeHolder = R.string.decode_image_screen_text_field_place_holder,
            value = decodeImageUiState.qrCodeResult,
            isReadOnly = true
        )

//         Options menu for auto-copy and auto-open weblink
        OptionMenu(
            option1Description = R.string.decode_image_screen_option_1_description,
            option1State = decodeImageUiState.autoCopyOptionEnable,
            onOption1StateChange = decodeImageViewModel.toggleAutoCopyOption,
            option2Description = R.string.decode_image_screen_option_2_description,
            option2State = decodeImageUiState.autoOpenWeblinkOptionEnable,
            onOption2StateChange = decodeImageViewModel.toggleAutoOpenWeblinkOption
        )
    }
}