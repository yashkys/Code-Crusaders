package com.example.stayfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.stayfit.AddDetailsActivity;
import com.example.stayfit.LoginActivity;
import com.example.stayfit.R;
import com.example.stayfit.databinding.FragmentHomeBinding;
import com.example.stayfit.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Context context;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference userRef;
    String username;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = container.getContext();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
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


        binding.logout.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        binding.layoutChangePassword.setOnClickListener(view1 -> changePasswordClicked());

        return view;
    }

    private void changePasswordClicked() {
        Toast.makeText(context, "Feature to be updated soon", Toast.LENGTH_SHORT).show();
        Log.d("Change Password click", "Feature Not Added");
    }

    private void loadUserData() {
        userRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user data
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
//                int weight = Integer.parseInt(dataSnapshot.child("email").getValue(String.class););
//                int height = Integer.parseInt(binding.editTextHeight.getText().toString());
                // int dob = Integer.parseInt(binding.dob.getText().toString());
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                try {
                    Picasso.get()
                            .load(imageUrl)
                            .into(binding.userProfileImage);
                }catch(NullPointerException e){

                }finally {
                    binding.userName.setText(name);
                    binding.userEmail.setText(email);
                    binding.userUsername.setText("@" + username);
                    binding.editTextWeight.setText("" + dataSnapshot.child("weight").getValue(Integer.class));
                    binding.editTextHeight.setText("" +dataSnapshot.child("height").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });



    }
}