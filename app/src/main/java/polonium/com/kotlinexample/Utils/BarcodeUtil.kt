package polonium.com.kotlinexample.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.android.gms.vision.barcode.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.data.BarcodeRealm
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun BarcodeRealm.createBitmap(width: Int, height: Int): Bitmap {
    val bitMatrix = MultiFormatWriter().encode(this.rawValue, this.barcodeFormatZXING()
            , width
            , height)
    val bmp = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
    for (x in 0 until bitMatrix.width) {
        for (y in 0 until bitMatrix.height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }

    val byteArrayOutputStream = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.JPEG, 99, byteArrayOutputStream)
    return BitmapFactory.decodeStream(ByteArrayInputStream(byteArrayOutputStream.toByteArray()))
}

fun BarcodeRealm.barcodeFormatZXING(): BarcodeFormat {
    return when (this.format) {
        Barcode.CODABAR -> BarcodeFormat.CODABAR
        Barcode.PDF417 -> BarcodeFormat.PDF_417
        Barcode.CODE_128 -> BarcodeFormat.CODE_128
        Barcode.CODE_39 -> BarcodeFormat.CODE_39
        Barcode.CODE_93 -> BarcodeFormat.CODE_93
        Barcode.AZTEC -> BarcodeFormat.AZTEC
        Barcode.EAN_13 -> BarcodeFormat.EAN_13
        Barcode.EAN_8 -> BarcodeFormat.EAN_8
        Barcode.UPC_A -> BarcodeFormat.UPC_A
        Barcode.UPC_E -> BarcodeFormat.UPC_E
        Barcode.DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        Barcode.ITF -> BarcodeFormat.ITF
        else -> BarcodeFormat.QR_CODE
    }
    }

    val BarcodeRealm.valueType : BarcodeValueType
        get() = BarcodeValueType.values()[this.valueFormat!!]

    enum class BarcodeValueType(val strId: Int) {
        ERR(R.string.err),
        CONTACT_INFO(R.string.contact),
        EMAIL(R.string.email),
        ISBN(R.string.ISBN),
        PHONE(R.string.phone),
        PRODUCT(R.string.product),
        SMS(R.string.sms),
        TEXT(R.string.text),
        URL(R.string.url),
        WIFI(R.string.wifi),
        GEO(R.string.geo),
        CALENDAR_EVENT(R.string.calendarEvent),
        DRIVER_LICENSE(R.string.driverLicense)
    }



