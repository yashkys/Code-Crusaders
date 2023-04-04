package com.example.stayfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.stayfit.databinding.ActivityBmicalculatorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class BMICalculatorActivity extends AppCompatActivity {

    ActivityBmicalculatorBinding binding;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference userRef;
    private static final DecimalFormat REAL_FORMATTER = new DecimalFormat("0.##");

    double weight, height;
    String username ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBmicalculatorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("users");
        userRef.child(mUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.getValue(String.class);
                    loadUserData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        binding.buttonCalculate.setOnClickListener(view1 -> {
            double res = calculateBMI();
            binding.textViewResult.setText( "BMI : " + REAL_FORMATTER.format(res));
            if (res < 18.5) {
                binding.textViewResult.setTextColor(getResources().getColor(R.color.yellow, getTheme()));
            } else if (res < 25) {
                binding.textViewResult.setTextColor(getResources().getColor(R.color.green, getTheme()));
            } else if (res < 30) {
                binding.textViewResult.setTextColor(getResources().getColor(R.color.mustard, getTheme()));
            } else {
                binding.textViewResult.setTextColor(getResources().getColor(R.color.DarkRed, getTheme()));
            }
        });
    }

    private void loadUserData() {
        userRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user data
                binding.editTextWeight.setText("" + dataSnapshot.child("weight").getValue(Integer.class));
                binding.editTextHeight.setText("" +dataSnapshot.child("height").getValue(Integer.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BMICalculatorActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public double calculateBMI() {
        weight = Double.parseDouble(binding.editTextWeight.getText().toString());
        height = Double.parseDouble(binding.editTextHeight.getText().toString())/100;
        return weight / (height * height) ;
    }
}