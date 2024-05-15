package com.example.transposescan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String imageDirectory = "";

    PreviewView previewView;

    Button bTakePicture;
    private ImageCapture imageCapture;

    Button switchviews;

    private TextView pathview;

    public String transposeTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request camera permissions if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        bTakePicture = findViewById(R.id.bCapture);
        previewView = findViewById(R.id.previewView);
        switchviews = findViewById(R.id.switchButton);
        pathview = findViewById(R.id.imagepath);


        bTakePicture.setOnClickListener(this);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Error initializing camera provider", e);
            }

        }, ContextCompat.getMainExecutor(this));

        switchviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to launch QuickTransposer activity
                Intent intent = new Intent(MainActivity.this, QuickTransposerFragment.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with initialization
                cameraProviderFuture.addListener(() -> {
                    try {
                        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                        startCameraX(cameraProvider);
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e("CameraX", "Error initializing camera provider", e);
                    }
                }, ContextCompat.getMainExecutor(this));
            } else {
                // Camera permission denied, handle accordingly (e.g., show message or disable camera functionality)
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCapture) {
            capturePhoto();
            String path = getDirectory();
            pathview.setText(path);
            showPopup();



        }
        if (view.getId() == R.id.switchButton) {
            Intent intent = new Intent(this, QuickTransposerFragment.class);
            startActivity(intent);
        }
    }

    private void capturePhoto() {
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(MainActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
                        getImageDirectory();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void getImageDirectory() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
        int column_index_data = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();

        //THIS IS WHAT YOU WANT!
        imageDirectory = cursor.getString(column_index_data);


        if (imageDirectory.equals((MediaStore.Images.Media.EXTERNAL_CONTENT_URI) + "/" + MediaStore.MediaColumns.DISPLAY_NAME)) {
            //Log.d("Directory of Image", imageDirectory);
            Toast.makeText(MainActivity.this,"Successfully assigned directory to file: " + imageDirectory,Toast.LENGTH_SHORT).show();
        }

        Log.e("image directory", imageDirectory);
    }

    public String getDirectory() {
        return imageDirectory;
    }


    private void showPopup() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_spinner, null);
        builder.setView(dialogView);

        // Set up the spinner
        Spinner spinner = dialogView.findViewById(R.id.spinner_letters);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.key_instruments, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Create the dialog
        AlertDialog alertDialog = builder.create();

        // Set up the submit button
        Button submitButton = dialogView.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assign the selected value to transposeTo
                transposeTo = spinner.getSelectedItem().toString();

                // Close the dialog
                alertDialog.dismiss();
            }
        });

        // Show the dialog
        alertDialog.show();
    }

    public String getTransposeTo() {
        return transposeTo;
    }

}