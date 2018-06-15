package com.developer.fabian.cameraimagefilter;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.fabian.cameraimagefilter.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class CameraActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int CAPTURE_PHOTO_IMAGE = 1;
    private static final int REQUEST_CAMERA = 2;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageView imgPhoto;
    private ImageButton imbSave;
    private ImageButton imbGray;
    private ImageButton imbNegative;
    private ImageButton imbNormal;

    private Utilities utilities;

    private Bitmap bmpOriginal;
    private Bitmap bmpNegative;
    private Bitmap bmpGrayScale;

    private boolean isOpenCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        this.imgPhoto = findViewById(R.id.imgPhoto);
        this.imbSave = findViewById(R.id.imbSave);
        this.imbGray = findViewById(R.id.imbGrayScale);
        this.imbNegative = findViewById(R.id.imbNegative);
        this.imbNormal = findViewById(R.id.imbNormal);

        this.utilities = new Utilities();

        if (!this.isOpenCamera) {
            showImage(false);
            enableElements(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_PHOTO_IMAGE && resultCode == RESULT_OK) {
            Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");

            int imageWidth = bitmapImage.getWidth();
            int imageHeight = bitmapImage.getHeight();

            this.bmpOriginal = bitmapImage;
            this.bmpGrayScale = this.utilities.matToBit(this.utilities.grayScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight);
            this.bmpNegative = this.utilities.matToBit(this.utilities.negativeScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight);

            this.imgPhoto.setImageBitmap(this.bmpOriginal);
            showImage(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.storage_permissions, Toast.LENGTH_SHORT).show();
    }

    /*  Action buttons  */

    public void openCamera(View view) {
        if (hasPermissions()) {
            this.isOpenCamera = true;
            Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intentPhoto.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intentPhoto, CAPTURE_PHOTO_IMAGE);
                enableElements(true);
            }
        } else {
            requestPermissions();
        }
    }

    public void convertImageToNegative(View view) {
        Toast.makeText(this, R.string.negative_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpNegative);
    }

    public void convertImageToGrayScale(View view) {
        Toast.makeText(this, R.string.scale_gray_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpGrayScale);
    }

    public void convertImageToNormal(View view) {
        Toast.makeText(this, R.string.normal_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpOriginal);
    }

    public void saveImage(View view) {
        if (imgPhoto.getVisibility() == View.VISIBLE) {
            Bitmap bitmap = ((BitmapDrawable) this.imgPhoto.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, null);

            if (path.equalsIgnoreCase(""))
                Toast.makeText(this, R.string.save_image_error, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.save_image_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.no_image, Toast.LENGTH_SHORT).show();
        }
    }

    /*  End action buttons  */

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, PERMISSIONS);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.camera_permissions),
                REQUEST_CAMERA,
                PERMISSIONS);
    }

    private void enableElements(boolean isEnabled) {
        this.imbSave.setEnabled(isEnabled);
        this.imbGray.setEnabled(isEnabled);
        this.imbNegative.setEnabled(isEnabled);
        this.imbNormal.setEnabled(isEnabled);
    }

    private void showImage(boolean isShowing) {
        if (isShowing)
            this.imgPhoto.setVisibility(View.VISIBLE);
        else
            this.imgPhoto.setVisibility(View.INVISIBLE);
    }
}
