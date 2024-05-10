package com.example.transposescan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.transposescan.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class contains code to take launch the app on an emulator or other connected device, ask for permissions, take a photo and obtain its directory,
 */
public class MainActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Executor executor = Executors.newSingleThreadExecutor();

    private ImageCapture imageCapture;
    private Preview preview;
    private Camera camera;

    private ActivityMainBinding binding;

    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    /**
     * Called when the activity is first created. This method sets up the layout,
     *     requests camera permissions, and initializes camera functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for this activity

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Check if all required permissions are granted
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            // Request required permissions from the user
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Set OnClickListener for  switch button to navigate to another activity
        View switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to QuickTransposeFragment when button is clicked
                Intent intent = new Intent(MainActivity.this, QuickTransposerFragment.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method checks if all required permissions are granted.
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method initializes camera functionality using CameraX library.
     */
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * This method binds camera preview to the layout and sets up image capture.
     * @param cameraProvider ProcessCameraProvider used to bind camera
     *                       to preview.
     */
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        // Set OnClickListener for the capture button to trigger image capture
        binding.bCapture.setOnClickListener(v -> captureImage());
    }

    /**
     * This method captures an image and saves it to a designated file.
     */
    private void captureImage() {
        File photoFile = new File(getBatchDirectoryName(), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() ->
                        // Display a toast message when the image is saved successfully
                        Toast.makeText(MainActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() ->
                        // Display a toast message if there's an error saving the image
                        Toast.makeText(MainActivity.this, "Error saving image: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * This method obtains the directory path where images will be saved.
     * @return the path of the directory where all the images will be saved.
     */
    private String getBatchDirectoryName() {
        String app_folder_path = getFilesDir().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("Capture Image", "Cannot create directory to save image");
        }
        return app_folder_path;
    }

    /**
     * This method handles permission request results.
     * @param requestCode The request code passed into requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
