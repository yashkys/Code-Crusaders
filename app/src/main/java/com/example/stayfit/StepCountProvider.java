package com.example.stayfit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.FragmentActivity;

public class StepCountProvider {
    private SensorManager sensorManager;
    private Context context;

    private Sensor stepSensor;
    private SensorEventListener stepSensorEventListener;


    private int stepCount = 0;

    public StepCountProvider(Context context, FragmentActivity fragmentActivity){
        this.context = context;
        sensorManager = (SensorManager) fragmentActivity.getSystemService(Context.SENSOR_SERVICE);

        start();
    }
    public StepCountProvider(Context context, SensorManager sensorManager){
        this.context = context;
        this.sensorManager = sensorManager;
        start();

    }

    private void start() {

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        stepSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                //long currentTimeInMillis = System.currentTimeMillis();
                stepCount = (int) sensorEvent.values[0];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

    }
    public int getStepCount(){
        return stepCount;
    }
    public void onPause(){
        sensorManager.unregisterListener(stepSensorEventListener);
    }

}
