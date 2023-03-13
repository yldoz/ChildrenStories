package com.example.alamaudioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.alamaudioplayer.databinding.ActivityConformAgeBinding;

public class ConformAgeActivity extends AppCompatActivity {
    private ActivityConformAgeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConformAgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}