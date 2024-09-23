package app.kotlin.qrquicker.ui.screens

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.OptionMenu
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.gap700
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.surfaceColor

enum class LoadingImageAreaState {
    NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED,
    INACTIVE,
    ACTIVE
}

@Composable
fun LoadImageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .statusBarsPadding()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(space = gap700),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context: Context = LocalContext.current

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

        var loadingImageAreaState: LoadingImageAreaState by remember {
            mutableStateOf(
                value = if (isPhotosAndMediaPermissionAllowed)
                    LoadingImageAreaState.INACTIVE
                else
                    LoadingImageAreaState.NO_PHOTOS_AND_MEDIA_PERMISSION_ALLOWED
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(space = gap400),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loadingImageAreaState) {
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

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    Button(
                        label = R.string.decode_image_screen_request_permission_button_label,
                        onPressEvent = { context.startActivity(intent) }
                    )
                }

                LoadingImageAreaState.INACTIVE -> {
                    Image(
                        painter = painterResource(R.drawable.image_view_place_holder),
                        contentDescription = "image view place holder",
                        modifier = Modifier.size(size = 200.dp),
                        colorFilter = ColorFilter.tint(color = onSurfaceColor)
                    )

                    Button(
                        label = R.string.decode_image_screen_start_loading_button_label,
                        onPressEvent = { loadingImageAreaState = LoadingImageAreaState.ACTIVE }
                    )
                }

                LoadingImageAreaState.ACTIVE -> {
                    Image(
                        painter = painterResource(id = R.drawable.sample_qr_code_image),
                        contentDescription = "",
                        modifier = Modifier
                            .size(size = 200.dp)
                            .clip(shape = RectangleShape)
                        ,
                        contentScale = ContentScale.Fit
                    )

                    Button(
                        label = R.string.decode_image_screen_stop_loading_button_label,
                        onPressEvent = { loadingImageAreaState = LoadingImageAreaState.INACTIVE }
                    )
                }
            }
        }

        TextField(placeHolder = R.string.decode_image_screen_text_field_place_holder)

        OptionMenu(
            option1Description = R.string.decode_image_screen_option_1_description,
            option2Description = R.string.decode_image_screen_option_2_description,
        )
    }
}