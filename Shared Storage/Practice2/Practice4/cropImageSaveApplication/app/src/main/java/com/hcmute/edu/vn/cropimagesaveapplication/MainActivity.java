package com.hcmute.edu.vn.cropimagesaveapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    // Declaring the UI elements from the layout file
    private Button buttonCrop, buttonSave;
    private ImageView imageView;

    // Declaring the Bitmap
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the UI elements
        imageView = findViewById(R.id.iv);
        buttonCrop = findViewById(R.id.btnCrop);
        buttonSave = findViewById(R.id.btnSave);

        // Declaring resource address ( type integer)
        int bitmapResourceID = R.drawable.image;

        // Setting the ImageView with the Image
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), bitmapResourceID));
        bitmap = BitmapFactory.decodeResource(getResources(), bitmapResourceID);

        // When Crop button is clicked
        buttonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // runs a custom function on the original image
                bitmap = getCircularBitmap(bitmap);

                // Sets the ImageView with the editted/cropped Image
                imageView.setImageBitmap(bitmap);
            }
        });

        // When Save button is clicked
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save whatever the bitmap is (edited/uneditted) into the device.
                saveMediaToStorage(bitmap);
            }
        });

    }

    private void saveMediaToStorage(Bitmap bitmap) {
        String filename = System.currentTimeMillis() + ".jpg";
        OutputStream fos = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver contentResolver = this.getContentResolver();
            if (contentResolver != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    fos = imageUri != null ?
                            this.getContentResolver().openOutputStream(imageUri,"w") : null;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = new File(imagesDir, filename);
            try {
                fos = new FileOutputStream(image);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (fos != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getCircularBitmap(Bitmap srcBitmap) {
        // Select whichever of width or height is minimum
        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());

        // Generate a bitmap with the above value as dimensions
        Bitmap dstBitmap = Bitmap.createBitmap(squareBitmapWidth, squareBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initializing a Canvas with the above generated bitmap
        Canvas canvas = new Canvas(dstBitmap);

        // initializing Paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Generate a square (rectangle with all sides same)
        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);

        // Operations to draw a circle
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        float left = ((float)(squareBitmapWidth - srcBitmap.getWidth())) / 2.0F;
        float top = ((float)(squareBitmapWidth - srcBitmap.getHeight())) / 2.0F;
        canvas.drawBitmap(srcBitmap, left, top, paint);
        srcBitmap.recycle();

        // Return the bitmap
        return dstBitmap;
    }
}