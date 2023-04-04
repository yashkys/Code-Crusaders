package com.example.stayfit.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.example.stayfit.StepResetReceiver;
import com.example.stayfit.User;
import com.example.stayfit.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class HomeFragment extends Fragment  implements SensorEventListener {

    private FragmentHomeBinding binding;
    TextView stepProgressText;
    TextView calorieBurned;
    TextView distanceMoved;
    Context context;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference userRef;

    SensorManager sensorManager;
    boolean running  = false;
    double magnitudePreviousStep;
    float totalSteps = 0f;
    float previousTotalSteps = 0f;
    int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;
    String username ;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("users");
        calorieBurned = binding.tvCaloriesBurned;
        distanceMoved = binding.tvDistance;
        userRef.child(mUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.getValue(String.class);
                    setGreetings();
                    loadUserData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        context = container.getContext();
        stepProgressText = binding.stepProgressText;

        if (isPermissionGranted()) {
            requestPermission ();
        }

        loadData();
        resetData();

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        return view;
    }

    private void loadUserData() {
        userRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user data
                String name = dataSnapshot.child("name").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                try {
                    Picasso.get()
                            .load(imageUrl)
                            .into(binding.circleImageView);
                }catch(NullPointerException e){

                }finally {
                    binding.tvUserName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });

//        userRef.child("username").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    user = snapshot1.getValue(User.class);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        binding.tvUserName.setText(user.getName());
    }

    private void setGreetings() {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        int len = timeStamp.length() ;
        timeStamp = timeStamp.substring(len-6,len -4);
        int hours = Integer.parseInt(timeStamp);
        TextView greeting = binding.tvGreetings;
        if(hours<12){
            greeting.setText("Good morning,");
        }else if(hours<16){
            greeting.setText("Good afternoon,");
        }else{
            greeting.setText("Good evening,");
        }

    }

    private void resetData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, StepResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void loadData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("step", Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getFloat("currentstep", 0f);
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED;
        }
        return false;
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
            calorieBurned.setText("" + getCalorieCount(steps));
            distanceMoved.setText("" + getDistance(steps));


        }else {
            if (running) {
                totalSteps = event.values[0];
                int currentStep = (int) (totalSteps - previousTotalSteps);
                stepProgressText.setText("" + currentStep);
                calorieBurned.setText("" + getCalorieCount(currentStep));
                distanceMoved.setText("" + getDistance(currentStep));
            }
        }

    }

    private int getDistance(int steps) {
        return steps/1300;
    }

    private int getCalorieCount(int steps) {
        return steps/13;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}