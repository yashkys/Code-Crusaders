package com.example.stayfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tvSignup = binding.textViewSignup;
        etEmail = binding.editTextEmail;
        etPassword = binding.editTextPassword;
        btnLogin = binding.buttonLogin;

        tvSignup.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view1 -> {
            PerformLogin();
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    ACTIVITY_RECOGNITION_REQUEST_CODE);
        } else {
            // Permission is already granted
            Toast.makeText(this, "ACTIVITY_RECOGNITION permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(this, "ACTIVITY_RECOGNITION permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied
                Toast.makeText(this, "ACTIVITY_RECOGNITION permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToNextActivity();
        }
    }

    private void PerformLogin() {
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=. *[A-Z])(?=. *[ !@#&( )-[{}] : ; ', ?/ * ~$^+=<>]). {8,20}$";
        if(!email.matches(EMAIL_PATTERN)){
            etEmail.setError("Enter correct email");
        }
        else if(pass.length()<8 && !pass.matches(PASSWORD_PATTERN)){
            etPassword.setError("Password must contain at least : \n1. 8 characters \n2. One Uppercase letter \n3. One Lowercase letter \n4. A number \n5. One special character(@,$,_,etc)");
        }else{
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();

                    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    StepCountProvider sp = new StepCountProvider(LoginActivity.this, sensorManager);
                    int step = sp.getStepCount();

                    //        // Get the SharedPreferences object
                    //        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                    //        // Create an editor to modify the SharedPreferences
                    //        SharedPreferences.Editor editor = sharedPreferences.edit();
                    //        editor.putInt("previousStepCount", step);
                    //        editor.apply();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert mUser != null;
                    DatabaseReference myRef = database.getReference("users").child(mUser.getUid());
                    myRef.child("lastStepCount").setValue(step);
                    sendToNextActivity();
                } else {
                    Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendToNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}