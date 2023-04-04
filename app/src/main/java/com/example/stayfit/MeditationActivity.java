package com.example.stayfit;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.stayfit.databinding.ActivityMeditationBinding;

public class MeditationActivity extends AppCompatActivity {

    ActivityMeditationBinding binding;

    String[] music = {
            "Flute music", "Chill music", "Relax music"
    };
    private MediaPlayer mediaPlayer;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis ;
    int pos = 0;
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeditationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        cnt = 0;


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);

        Spinner spinner = binding.musicSpinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                pos = 0;
            }
        });

        // Create the instance of ArrayAdapter
        // having the list of music
        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, music);

        // set simple layout resource file
        // for each item of spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spinner.setAdapter(adapter);

        mButtonStartPause = binding.buttonStartPause;
        mButtonReset = binding.buttonReset;

        mButtonStartPause.setOnClickListener(v -> {
            cnt++;
            int hr = Integer.parseInt(binding.edtHour.getText().toString());
            int min = Integer.parseInt(binding.edtMinutes.getText().toString());
            int sec = Integer.parseInt(binding.edtSeconds.getText().toString());
            mTimeLeftInMillis = (hr * 3600L + min * 60L + sec ) * 1000;
            if ((cnt&1) != 1) {
                mediaPlayer.stop();
                pauseTimer();
            } else {
                playMusic(pos);
                startTimer();
            }
        });

        mButtonReset.setOnClickListener(v -> resetTimer());

        updateCountDownText();
    }

    private void playMusic(int i) {
        mediaPlayer = MediaPlayer.create(this, (i==0) ? R.raw.music1 : (i ==1) ? R.raw.music2 : R.raw.music3);
        mediaPlayer.start();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mediaPlayer.stop();
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("Pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = 0L;
        mediaPlayer.stop();
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        int minutes = (int) ((mTimeLeftInMillis / 1000) / 60) % 60;
        int hours = (int) ((mTimeLeftInMillis / 1000) / 3600) ;
        binding.edtSeconds.setText("" + seconds);
        binding.edtMinutes.setText("" + minutes);
        binding.edtHour.setText("" + hours);
    }
}