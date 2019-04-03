package com.apptreesoftware.barcodescan

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*
import com.yourcompany.barcodescan.R


class BarcodeScannerActivity : Activity(), ZXingScannerView.ResultHandler {

    lateinit var scannerView: me.dm7.barcodescanner.zxing.ZXingScannerView

    companion object {
        val REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100
        val TOGGLE_FLASH = 200
        val ALBUM_RESULT_CODE = 300
        val PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 400

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = QrScanView(this)
        scannerView.setAutoFocus(true)
        // this paramter will make your HUAWEI phone works great!
        scannerView.setAspectTolerance(0.5f)
        setContentView(scannerView)
        actionBar.title = "扫描二维码"
        setCustomActionBar()
    }

    private fun setCustomActionBar() {
        val lp = ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        val mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null)
        val actionBar = actionBar
        actionBar!!.setCustomView(mActionBarView, lp)
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
        actionBar.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        if (scannerView.flash) {
//            val item = menu.add(0,
//                    TOGGLE_FLASH, 0, "Flash Off")
//            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//        } else {
//            val item = menu.add(0,
//                    TOGGLE_FLASH, 0, "Flash On")
//            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//        }
        val item = menu.add(0,
                TOGGLE_FLASH, 0, "相册")
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == TOGGLE_FLASH) {
//            scannerView.flash = !scannerView.flash
//            this.invalidateOptionsMenu()
//            return true
//        }
        if (item.itemId == TOGGLE_FLASH) {
            openSysAlbum()
            var checked = ContextCompat.checkSelfPermission(this
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checked == PackageManager.PERMISSION_GRANTED) {
                openSysAlbum()
            } else {
                ActivityCompat.requestPermissions(this
                        , arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
            }
            return true
        }

        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun openSysAlbum() {
        var albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(albumIntent, ALBUM_RESULT_CODE)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        // start camera immediately if permission is already given
        if (!requestCameraAccessIfNecessary()) {
            scannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        scannerView.stopCameraPreview()
        scannerView.stopCamera()
        val intent = Intent()
        intent.putExtra("SCAN_RESULT", result.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun finishWithError(errorCode: String) {
        val intent = Intent()
        intent.putExtra("ERROR_CODE", errorCode)
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun requestCameraAccessIfNecessary(): Boolean {
        val array = arrayOf(Manifest.permission.CAMERA)
        if (ContextCompat
                        .checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, array,
                    REQUEST_TAKE_PHOTO_CAMERA_PERMISSION)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_TAKE_PHOTO_CAMERA_PERMISSION -> {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    scannerView.startCamera()
                } else {
                    finishWithError("PERMISSION_NOT_GRANTED")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ALBUM_RESULT_CODE) {
            handleAlbumPic(data)
        }
    }

    private fun handleAlbumPic(data: Intent?) {
        var uri: Uri? = data?.data
        val result = scanningImage(uri)
        if (result != null) {
            handleResult(result);
        }
    }

    private fun scanningImage(uri: Uri?): Result? {
        if (uri == null) {
            return null
        }
        var hints = Hashtable<DecodeHintType, String>()
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8") //设置二维码内容的编码

        var scanBitmap = BitmapUtil.decodeUri(this, uri, 500, 500);
        var width = scanBitmap.getWidth()
        var height = scanBitmap.getHeight()
        var pixels = IntArray(width * height)
        scanBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        var source = RGBLuminanceSource(width, height, pixels)
        var bitmap1 = BinaryBitmap(HybridBinarizer(source))
        var reader = QRCodeReader()
        try {
            return reader.decode(bitmap1, hints)
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: ChecksumException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
        return null
    }
}

object PermissionUtil {

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value [PackageManager.PERMISSION_GRANTED].

     * @see Activity.onRequestPermissionsResult
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.size < 1) {
            return false
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
