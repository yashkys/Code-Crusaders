package com.example.stayfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityHomeBinding;
import com.example.stayfit.fragments.BrowseFragment;
import com.example.stayfit.fragments.HomeFragment;
import com.example.stayfit.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity { // implements SensorEventListener

//    SensorManager sensorManager;
//    boolean running  = false;
//
//    double magnitudePreviousStep;
//    float totalSteps = 0f;
//    float previousTotalSteps = 0f;
//    int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    ActivityHomeBinding binding;
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        if (isPermissionGranted()) {
//            requestPermission ();
//        }
//
//        loadData();
//        resetData();
//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home :
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_browse :
                    selectedFragment = new BrowseFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment  = new ProfileFragment();
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, (selectedFragment != null) ? selectedFragment : new HomeFragment())
                    .commit();

            return true;
        });
    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_home :
//                selectedFragment = new HomeFragment();
//                break;
//            case R.id.nav_browse :
//                selectedFragment = new BrowseFragment();
//                break;
//            case R.id.nav_profile:
//                selectedFragment  = new ProfileFragment();
//                break;
//        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.frame_layout, (selectedFragment != null) ? selectedFragment : new HomeFragment())
//                .commit();
//
//        return true;
//    }
//

}