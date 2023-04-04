package com.example.stayfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stayfit.BMICalculatorActivity;
import com.example.stayfit.BookAppointmentActivity;
import com.example.stayfit.ExerciseActivity;
import com.example.stayfit.MeditationActivity;
import com.example.stayfit.RecipeActivity;
import com.example.stayfit.databinding.FragmentBrowseBinding;

public class BrowseFragment extends Fragment {

    FragmentBrowseBinding binding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBrowseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        context = container.getContext();

        binding.meditationButton.setOnClickListener(view1 -> {
            startActivity( new Intent(context, MeditationActivity.class));
        });

        binding.bmiCalculator.setOnClickListener(view1 -> {
            startActivity( new Intent(context, BMICalculatorActivity.class));
        });

        binding.exerciseRoutine.setOnClickListener(view1 -> {
            startActivity( new Intent(context, ExerciseActivity.class));
        });

        binding.receipeButton.setOnClickListener(view1 -> {
            startActivity( new Intent(context, RecipeActivity.class));
        });

        binding.bookAppointmentButton.setOnClickListener(view1 -> {
            startActivity( new Intent(context, BookAppointmentActivity.class));
        });
        return view;
    }

}