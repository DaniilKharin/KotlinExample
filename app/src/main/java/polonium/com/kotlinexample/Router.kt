package polonium.com.kotlinexample

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.barcode.Barcode
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.app_bar_main.*
import polonium.com.kotlinexample.Utils.RxPermissionsXKtx.RxPermissionsXKtx
import polonium.com.kotlinexample.codeOverview.CodeOverviewFragment


class Router constructor(private val fragmentManager: FragmentManager, val activity: MainActivity) : IRouter {
    override fun openBitmap(uri: Uri) {
        compositeDisposable.add(RxPermissionsXKtx(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.view_dialog_title)))
                })
    }

    override fun shareBitmap(uri: Uri) {
        compositeDisposable.add(RxPermissionsXKtx(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/png"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_dialog_title)))
                })
    }

    override fun saveBitmap(bitmap: Bitmap): ObservableField<Uri?> {
        var bitmapPathObservableField: ObservableField<Uri?> = ObservableField<Uri?>()
        androidx.appcompat.app.AlertDialog.Builder(activity)
                .setMessage(R.string.open_after_save_question)
                .setPositiveButton("Да") { dialog, int -> bitmapPathObservableField = openBitmap(bitmap) }
                .setNegativeButton("Нет") { dialog, int ->
                    compositeDisposable.add(RxPermissionsXKtx(activity)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                bitmapPathObservableField.set(Uri.parse(MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "barcode", null)))
                                Toast.makeText(activity, R.string.saved, Toast.LENGTH_SHORT).show()
                            })
                }
                .create().show()
        return bitmapPathObservableField
    }

    override fun openBitmap(bitmap: Bitmap): ObservableField<Uri?> {
        val bitmapPathObservableField: ObservableField<Uri?> = ObservableField<Uri?>()
        compositeDisposable.add(RxPermissionsXKtx(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    bitmapPathObservableField.set(Uri.parse(MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "barcode", null)))
                    val intent = Intent(Intent.ACTION_VIEW, bitmapPathObservableField.get())
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.view_dialog_title)))
                })
        return bitmapPathObservableField
    }

    private val compositeDisposable = CompositeDisposable()

    override fun shareBitmap(bitmap: Bitmap): ObservableField<Uri?> {
        val bitmapPathObservableField: ObservableField<Uri?> = ObservableField<Uri?>()
        compositeDisposable.add(RxPermissionsXKtx(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/png"
                    bitmapPathObservableField.set(Uri.parse(MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "title", null)))
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapPathObservableField)
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_dialog_title)))
                })
        return bitmapPathObservableField
    }

    override fun callTelNumber(tel: String) {
        val callTelIntent = Intent(android.content.Intent.ACTION_DIAL)
        callTelIntent.type = "vnd.android-dir/mms-sms"
        callTelIntent.data = Uri.parse("tel:$tel")
        activity.startActivity(callTelIntent)
    }

    companion object {
        const val SCAN_FRAGMENT_NAME = "scan"
        const val OVERVIEW_FRAGMENT_NAME = "overview"
    }

    override fun sendSMS(sms: Barcode.Sms) {
        val smsIntent = Intent(android.content.Intent.ACTION_VIEW)
        smsIntent.type = "vnd.android-dir/mms-sms"
        smsIntent.putExtra("address", sms.phoneNumber)
        smsIntent.putExtra("sms_body", sms.message)
        activity.startActivity(smsIntent)
    }

    override fun openOverview(barcode: Barcode) {
        val args = Bundle()
        args.putParcelable(CodeOverviewFragment.ARG_BARCODE, barcode)
        activity.nav_host_fragment.findNavController().navigate(R.id.action_scanFragment_to_codeOverviewFragment, args)
    }

    override fun showMessage(message: String) {
        Toast.makeText(fragmentManager.findFragmentById(R.id.content)?.context, message, Toast.LENGTH_SHORT).show()
    }


    override fun toScanFragment() {
        activity.nav_host_fragment.findNavController().navigate(R.id.scanFragment)
    }

    override fun toHistoryFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openSettings() {
        activity.nav_host_fragment.findNavController().navigate(R.id.settingsDialogFragment)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }

}