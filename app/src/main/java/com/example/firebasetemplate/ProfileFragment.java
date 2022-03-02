package com.example.firebasetemplate;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends AppFragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                Glide.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).centerCrop().into(binding.imgPerfil);
                Glide.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(binding.imgPerfil2);
                binding.nameText.setText(firebaseAuth.getCurrentUser().getDisplayName());
                binding.emailText.setText(firebaseAuth.getCurrentUser().getEmail());
                Log.e("sdfdfs","USER:" + firebaseAuth.getCurrentUser().getEmail());
            }
        });

    }
}