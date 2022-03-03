package com.example.firebasetemplate;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentRegisterBinding;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.UUID;


public class RegisterFragment extends AppFragment {
    private FragmentRegisterBinding binding;
    private Uri uriImg, downloadUriImg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentRegisterBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgPerfiil.setOnClickListener(v -> galeria.launch("image/*"));
        appViewModel.uriImagenPerfilSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).into(binding.imgPerfiil);
            uriImg = uri;
        });

        binding.createAccountButton.setOnClickListener(v -> {
            if (binding.nameEditText.getText().toString().isEmpty()) {
                binding.nameEditText.setError("Required Name");
                return;
            }
            if (binding.emailEditText.getText().toString().isEmpty()) {
                binding.emailEditText.setError("Required Email");
                return;
            }
            if (binding.usernameEditText.getText().toString().isEmpty()) {
                binding.usernameEditText.setError("Required Username");
                return;
            }
            if (binding.passwordEditText.getText().toString().isEmpty()) {
                binding.passwordEditText.setError("Required Password");
                return;
            }

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                            binding.emailEditText.getText().toString(),
                            binding.passwordEditText.getText().toString()
                    ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseStorage.getInstance()
                            .getReference("/images/" + UUID.randomUUID() + ".jpg")
                            .putFile(uriImg)
                            .continueWithTask(task1 -> task1.getResult().getStorage().getDownloadUrl())
                            .addOnSuccessListener(urlDescarga -> {
                                downloadUriImg = urlDescarga;

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(binding.nameEditText.getText().toString())
                                        .setDisplayName(binding.usernameEditText.getText().toString())
                                        .setPhotoUri(downloadUriImg)
                                        .build();

                                user.updateProfile(profile)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                UserClass usser = new UserClass();
                                                usser.email = binding.emailEditText.getText().toString();
                                                usser.name = binding.nameEditText.getText().toString();
                                                usser.userName = binding.usernameEditText.getText().toString();
                                                usser.imageIcon = downloadUriImg.toString();
                                                db.collection("users").document(usser.email).set(usser);
                                                navController.navigate(R.id.action_registerFragment_to_postsHomeFragment);
                                                Log.d("asd", "User profile updated");
                                            }
                                        });
                            });

                } else {
                    Log.d("FAIL", "Create user with email : failure", task.getException());
                    Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> appViewModel.setUriImagenPerfilSeleccionada(uri));
}