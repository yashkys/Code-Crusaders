package com.example.stayfit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StepResetReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        DatabaseReference myRef = database.getReference("users").child(mUser.getUid());

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

// Retrieve data using the keys
        int step = sharedPreferences.getInt("previousStepCount", 12); // Provide a default value in case key1 does not exist
        myRef.child("lastStepCount").setValue(step);
    }
}