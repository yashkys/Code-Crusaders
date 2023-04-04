package com.example.stayfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stayfit.AddDetailsActivity;
import com.example.stayfit.LoginActivity;
import com.example.stayfit.R;
import com.example.stayfit.databinding.FragmentHomeBinding;
import com.example.stayfit.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = container.getContext();
        binding.logout.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
        return view;
    }
}