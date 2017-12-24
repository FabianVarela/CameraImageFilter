package com.developer.fabian.cameraimagefilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.fabian.cameraimagefilter.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private static final int CAPTURE_PHOTO_IMAGE = 1;
    private static final String FOLDER_NAME = "CameraImageFilter";

    private ImageView imgPhoto;
    private ImageButton imbSave;
    private ImageButton imbGray;
    private ImageButton imbNegative;
    private ImageButton imbNormal;

    private Utilities utilities;
    private Bitmap bmpOriginal;
    private Bitmap bmpNegative;
    private Bitmap bmpGrayScale;
    private boolean openCamera = false;

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

        if (!this.openCamera) {
            this.imgPhoto.setVisibility(View.INVISIBLE);
            this.imbSave.setEnabled(false);
            this.imbGray.setEnabled(false);
            this.imbNegative.setEnabled(false);
            this.imbNormal.setEnabled(false);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_PHOTO_IMAGE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmapImage = (Bitmap) extras.get("data");

            int imageWidth = bitmapImage.getWidth();
            int imageHeight = bitmapImage.getHeight();

            this.bmpOriginal = bitmapImage;
            this.bmpGrayScale = this.utilities.matToBit(this.utilities.grayScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight);
            this.bmpNegative = this.utilities.matToBit(this.utilities.negativeScale(Utilities.filterTest(bitmapImage), imageWidth, imageHeight), imageWidth, imageHeight);

            this.imgPhoto.setImageBitmap(this.bmpOriginal);
            this.imgPhoto.setVisibility(View.VISIBLE);
        }
    }

    public void onOpenCamera(View view) {
        this.openCamera = true;
        Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intentPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentPhoto, CAPTURE_PHOTO_IMAGE);
            imbSave.setEnabled(true);
            imbGray.setEnabled(true);
            imbNegative.setEnabled(true);
            imbNormal.setEnabled(true);
        }
    }

    public void onSaveImage(View view) {
        File media = getAccessDirectory();

        try {
            media.createNewFile();

            Bitmap bitmap = ((BitmapDrawable) this.imgPhoto.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bitmapData = byteArrayOutputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream(media);
            fileOutputStream.write(bitmapData);
            fileOutputStream.flush();
            fileOutputStream.close();

            Toast.makeText(this, R.string.save_image_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.save_image_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void onNegative(View view) {
        Toast.makeText(this, R.string.negative_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpNegative);
    }

    public void onGrayScale(View view) {
        Toast.makeText(this, R.string.scale_gray_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpGrayScale);
    }

    public void onNormal(View view) {
        Toast.makeText(this, R.string.normal_filter, Toast.LENGTH_SHORT).show();
        this.imgPhoto.setImageBitmap(this.bmpOriginal);
    }

    @Nullable
    private File getAccessDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dirMedia = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FOLDER_NAME);

            if (!dirMedia.exists()) {
                if (!dirMedia.mkdirs()) {
                    Toast.makeText(this, R.string.error_save_sdcard, Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    Toast.makeText(this, R.string.success_folder_created, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.message_exists_folder, Toast.LENGTH_SHORT).show();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            return new File(dirMedia.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }

        Toast.makeText(this, R.string.message_sdcard_noexists, Toast.LENGTH_SHORT).show();

        return null;
    }
}
