package app.kotlin.qrquicker

const val TRANSITION_DURATION: Int = 500

const val MAX_INPUT_TEXT_LENGTH: Int = 100

const val QR_CODE_RESULT_IMAGE_SIZE: Int = 256

const val KEY_TEXT_INPUT_VALUE: String = "TEXT_INPUT_VALUE"
const val KEY_QR_CODE_OUTPUT_VALUE: String = "QR_CODE_OUTPUT_VALUE"
const val OUTPUT_PATH: String = "qr_quicker_output"

val CHANNEL_NAME: CharSequence = "Non-verbose QR Quicker notification"
const val CHANNEL_DESCRIPTION = "Show notification when user want to save or decode the QR code"
const val CHANNEL_ID: String = "NON_VERBOSE_NOTIFICATION"

const val SAVING_QR_NOTIFICATION_TITLE: String = "Saving your QR code"
const val SAVING_QR_NOTIFICATION_BODY: String = "This takes a few second"
const val SAVING_QR_NOTIFICATION_ID: Int = 0

const val SAVE_QR_SUCCESSFUL_NOTIFICATION_TITLE: String = "Save QR code successfully"
const val SAVE_QR_SUCCESSFUL_NOTIFICATION_BODY: String = "Tap to view in storage"
const val SAVE_QR_SUCCESSFUL_NOTIFICATION_ID: Int = 1

const val SAVE_QR_FAILED_NOTIFICATION_TITLE: String = "Failed to save your QR code"
const val SAVE_QR_FAILED_NOTIFICATION_BODY: String = ""
const val SAVE_QR_FAILED_NOTIFICATION_ID: Int = 2

const val DETECT_QR_FAILED_NOTIFICATION_TITLE: String = "Failed to detect the QR code"
const val DETECT_QR_FAILED_NOTIFICATION_BODY: String = ""
const val DETECT_QR_FAILED_NOTIFICATION_ID: Int = 3

