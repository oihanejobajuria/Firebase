package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebasetemplate.databinding.FragmentSignInBinding;


public class SignInFragment extends AppFragment {
    private FragmentSignInBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentSignInBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signInProgressBar.setVisibility(View.GONE);

        binding.googleSignIn.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_signInFragment_to_postsHomeFragment);
        });

        binding.emailSignIn.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_signInFragment_to_postsHomeFragment);
        });

        binding.goToRegister.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_signInFragment_to_registerFragment);
        });
    }
}