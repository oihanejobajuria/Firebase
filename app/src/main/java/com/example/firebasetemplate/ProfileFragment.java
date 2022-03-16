package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentProfileBinding;
import com.example.firebasetemplate.model.UserClass;

public class ProfileFragment extends AppFragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db.collection("users").document(auth.getCurrentUser().getEmail())
                .addSnapshotListener((collectionSnapshot, e) -> {
                    if (collectionSnapshot != null) {
                        UserClass user = collectionSnapshot.toObject(UserClass.class);
                        binding.nameText.setText(user.name);
                        binding.username.setText(user.userName);
                        binding.emailText.setText(user.email);

                        if (getActivity() != null) {
                            Glide.with(this).load(user.imageIcon).centerCrop().into(binding.imgPerfil);
                            Glide.with(this).load(user.imageIcon).into(binding.imgPerfil2);
                        }
                    }
                });

        binding.imgBtnEdit.setOnClickListener(v -> {
            ProfileFragmentDirections.ActionProfileFragmentToEditProfileFragment action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment();
            action.setUserId(auth.getCurrentUser().getEmail()).setOldName(auth.getCurrentUser().getDisplayName());
            navController.navigate(action);
        });

    }
}