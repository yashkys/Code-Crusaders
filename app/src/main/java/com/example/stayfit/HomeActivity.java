package com.example.stayfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    boolean running  = false;

    double magnitudePreviousStep;
    float totalSteps = 0f;
    float previousTotalSteps = 0f;
    int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (isPermissionGranted()) {
            requestPermission ();
        }

        loadData();
        resetData();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private void resetData() {
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("step", Context.MODE_PRIVATE);
        float savedNo = sharedPreferences.getFloat("currentstep", 0f);
        previousTotalSteps = savedNo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //most recent mobiles and major android mobile have this sensor
        Sensor countSensor = sensorManager.getDefaultSensor (Sensor.TYPE_STEP_COUNTER);
        //this also in very few
        Sensor detectorSensor = sensorManager.getDefaultSensor (Sensor.TYPE_STEP_DETECTOR);
        //below sensor for particularly samsung and moto
        Sensor accelerometer = sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);

        //now check which one is in your mobile
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else if (detectorSensor != null) {
            sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
        } else if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Your device is not compatible", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.ACTIVITY_RECOGNITION},
                    ACTIVITY_RECOGNITION_REQUEST_CODE);
        }
    }
    private boolean isPermissionGranted() {
        //check the user have permission enabled
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }        // Add more cases if needed
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView stepTaken = binding.stepCountTextView;

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //we need to detect the magnitude
            float xaccel = event.values[0];
            float yaccel = event.values[1];
            float zaccel = event.values[2];
            double magnitude = Math.sqrt((xaccel * xaccel + yaccel * yaccel + zaccel * zaccel));

            double magnitudeDelta = magnitude - magnitudePreviousStep;
            magnitudePreviousStep = magnitude;

            if(magnitudeDelta > 6){
                totalSteps++;
            }
            int steps = (int)totalSteps;
            stepTaken.setText("" + totalSteps);

        }else {
            if (running) {
                totalSteps = event.values[0];
                int currentStep = (int) (totalSteps - previousTotalSteps);
                stepTaken.setText("" + currentStep);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}