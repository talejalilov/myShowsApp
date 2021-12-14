package com.example.myshowsapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myshowsapp.adapters.ViewPagerAdapter;
import com.example.myshowsapp.databinding.ActivityInformationBinding;
import com.example.myshowsapp.fragments.FirstFragment;
import com.example.myshowsapp.fragments.SecondFragment;

public class InformationActivity extends AppCompatActivity {

    ActivityInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tabLayout.setupWithViewPager(binding.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFragment(new FirstFragment(), "About The App");
        viewPagerAdapter.addFragment(new SecondFragment(), "Developed By");

        binding.viewPager.setAdapter(viewPagerAdapter);


    }
}