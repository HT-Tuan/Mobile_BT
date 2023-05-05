package com.hcmute.edu.vn.imageclassify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.support.image.TensorImage;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import com.hcmute.edu.vn.imageclassify.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Button selectBtn, predictBtn, capturebtn;
    TextView textView;
    ImageView imageView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermision();

        String[] labels = new String[1001];
        int cnt = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line = bufferedReader.readLine();
            while (line != null){
                labels[cnt] = line;
                cnt = cnt + 1;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        capturebtn = findViewById(R.id.captureBtn);
        textView = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(MainActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                    // Runs model inference and gets result.
                    MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    textView.setText(labels[getMax(outputFeature0.getFloatArray())]+" ");
                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });
    }

    private int getMax(float[] array) {
        int Max = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i] > array[Max])
                Max = i;
        }
        return Max;
    }

    private void getPermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11){
            if (grantResults.length > 0){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    this.getPermision();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10){
            if(data != null)
            {
                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == 12) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}