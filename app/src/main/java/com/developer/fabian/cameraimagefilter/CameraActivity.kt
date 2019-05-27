package com.developer.fabian.cameraimagefilter

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

import com.developer.fabian.cameraimagefilter.utils.Utilities

import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import pub.devrel.easypermissions.EasyPermissions

class CameraActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {

        private const val CAPTURE_PHOTO_IMAGE = 1
        private const val REQUEST_CAMERA = 2

        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private lateinit var imgPhoto: ImageView
    private lateinit var imbSave: ImageButton
    private lateinit var imbGray: ImageButton
    private lateinit var imbNegative: ImageButton
    private lateinit var imbNormal: ImageButton

    private var utilities: Utilities? = null

    private var bmpOriginal: Bitmap? = null
    private var bmpNegative: Bitmap? = null
    private var bmpGrayScale: Bitmap? = null

    private var isOpenCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        imgPhoto = findViewById(R.id.imgPhoto)
        imbSave = findViewById(R.id.imbSave)
        imbGray = findViewById(R.id.imbGrayScale)
        imbNegative = findViewById(R.id.imbNegative)
        imbNormal = findViewById(R.id.imbNormal)

        utilities = Utilities()

        if (!isOpenCamera) {
            showImage(false)
            enableElements(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_PHOTO_IMAGE && resultCode == RESULT_OK) {
            val bitmapImage = data.extras!!.get("data") as Bitmap

            val imageWidth = bitmapImage.width
            val imageHeight = bitmapImage.height

            bmpOriginal = bitmapImage
            bmpGrayScale = utilities!!.matToBit(utilities!!.grayScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight)
            bmpNegative = utilities!!.matToBit(utilities!!.negativeScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight)

            imgPhoto.setImageBitmap(bmpOriginal)
            showImage(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, R.string.storage_permissions, Toast.LENGTH_SHORT).show()
    }

    /*  Action buttons  */

    fun openCamera(view: View) {
        if (hasPermissions()) {
            this.isOpenCamera = true
            val intentPhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intentPhoto.resolveActivity(packageManager) != null) {
                startActivityForResult(intentPhoto, CAPTURE_PHOTO_IMAGE)
                enableElements(true)
            }
        } else {
            requestPermissions()
        }
    }

    fun convertImageToNegative(view: View) {
        Toast.makeText(this, R.string.negative_filter, Toast.LENGTH_SHORT).show()
        imgPhoto.setImageBitmap(this.bmpNegative)
    }

    fun convertImageToGrayScale(view: View) {
        Toast.makeText(this, R.string.scale_gray_filter, Toast.LENGTH_SHORT).show()
        imgPhoto.setImageBitmap(this.bmpGrayScale)
    }

    fun convertImageToNormal(view: View) {
        Toast.makeText(this, R.string.normal_filter, Toast.LENGTH_SHORT).show()
        imgPhoto.setImageBitmap(this.bmpOriginal)
    }

    fun saveImage(view: View) {
        if (imgPhoto.visibility == View.VISIBLE) {
            val bitmap = (imgPhoto.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, fileName, null)

            if (path.equals("", ignoreCase = true))
                Toast.makeText(this, R.string.save_image_error, Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, R.string.save_image_success, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.no_image, Toast.LENGTH_SHORT).show()
        }
    }

    /*  End action buttons  */

    private fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *PERMISSIONS)
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.camera_permissions),
                REQUEST_CAMERA,
                *PERMISSIONS)
    }

    private fun enableElements(isEnabled: Boolean) {
        this.imbSave.isEnabled = isEnabled
        this.imbGray.isEnabled = isEnabled
        this.imbNegative.isEnabled = isEnabled
        this.imbNormal.isEnabled = isEnabled
    }

    private fun showImage(isShowing: Boolean) {
        if (isShowing)
            this.imgPhoto.visibility = View.VISIBLE
        else
            this.imgPhoto.visibility = View.INVISIBLE
    }
}
