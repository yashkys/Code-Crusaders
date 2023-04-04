package com.example.stayfit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit.HomeActivity;
import com.example.stayfit.R;
import com.example.stayfit.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment  implements SensorEventListener {

    SensorManager sensorManager;
    boolean running  = false;
    double magnitudePreviousStep;
    float totalSteps = 0f;
    float previousTotalSteps = 0f;
    int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    TextView stepProgressText;
    private FragmentHomeBinding binding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        context = container.getContext();

        stepProgressText = binding.stepProgressText;

        if (isPermissionGranted()) {
            requestPermission ();
        }

        loadData();
        resetData();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        return view;
    }

    private void resetData() {
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("step", Context.MODE_PRIVATE);
        float savedNo = sharedPreferences.getFloat("currentstep", 0f);
        previousTotalSteps = savedNo;
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

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
            Toast.makeText(getContext(), "Your device is not compatible", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] {android.Manifest.permission.ACTIVITY_RECOGNITION},
                    ACTIVITY_RECOGNITION_REQUEST_CODE);
        }
    }
    private boolean isPermissionGranted() {
        //check the user have permission enabled
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED;
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//            } else {
//                // Permission denied
//            }
//        }        // Add more cases if needed
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {


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
            stepProgressText.setText("" + totalSteps);

        }else {
            if (running) {
                totalSteps = event.values[0];
                int currentStep = (int) (totalSteps - previousTotalSteps);
                stepProgressText.setText("" + currentStep);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}