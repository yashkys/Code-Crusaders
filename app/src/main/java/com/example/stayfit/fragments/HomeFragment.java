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

import com.example.stayfit.StepCountProvider;
import com.example.stayfit.StepResetReceiver;
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


public class HomeFragment extends Fragment {//} implements SensorEventListener {//

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
    String username ;

    int stepCount = 0;
    //updated data last
    private Sensor stepSensor;
    private SensorEventListener stepSensorEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = container.getContext();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("users");
        calorieBurned = binding.tvCaloriesBurned;
        distanceMoved = binding.tvDistance;
        stepProgressText = binding.stepProgressText;

        loadUserData();
//        StepCountProvider sp = new StepCountProvider(context, requireActivity());

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //lastMidnight = getTodayMidnightTimeInMillis();
        resetData();
        stepSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                //update stepcount from firebase
                stepCount = (int) sensorEvent.values[0];
                final int[] lastStepCount = {0};
                userRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Retrieve user step  data
                        lastStepCount[0] =  dataSnapshot.child("lastStepCount").getValue(Integer.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "An Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
                //here
                stepCount = stepCount-lastStepCount[0];

                stepProgressText.setText("" + stepCount);
                calorieBurned.setText("" + getCalorieCount(stepCount));
                distanceMoved.setText("" + getDistance(stepCount));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(stepSensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(stepSensorEventListener);
    }

    // Returns the midnight time in milliseconds of today's date
    private long getTodayMidnightTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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
    private int getDistance(int steps) {
        return steps/1300;
    }

    private int getCalorieCount(int steps) {
        return steps/40;
    }

    private void loadUserData() {

        userRef.child(mUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.getValue(String.class);
                    setGreetings();

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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

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

    @Override
    public void onStop() {
        super.onStop();
                SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                // Create an editor to modify the SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("previousStepCount", stepCount);
                editor.apply();
    }
}