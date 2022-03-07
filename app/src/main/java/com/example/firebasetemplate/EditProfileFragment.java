package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentEditProfileBinding;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileFragment extends AppFragment {

    private FragmentEditProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentEditProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db.collection("users").
                document(EditProfileFragmentArgs.fromBundle(getArguments()).
                        getUserId()).addSnapshotListener((collectionSnapshot, e) -> {
            if (collectionSnapshot != null) {
                UserClass user = collectionSnapshot.toObject(UserClass.class);
                String split = user.userName.substring(1);

                binding.nameEdit.setText(user.name);
                binding.emailEdit.setText(user.email);
                binding.usernameEdit.setText(split);
                Glide.with(getActivity()).load(user.imageIcon).centerCrop().into(binding.imgPerfil);
                Glide.with(getActivity()).load(user.imageIcon).centerCrop().into(binding.imgPerfil2);
            }
        });

        binding.btnSave.setOnClickListener(view1 -> {
            // TODO: cambiar name
            FirebaseUser currentUser = auth.getCurrentUser();

            // TODO: NO se cambia email, como cambiar img, como contasenya
            db.collection("users").
                    document(EditProfileFragmentArgs.fromBundle(getArguments()).
                            getUserId()).addSnapshotListener((collectionSnapshot, e) -> {
                if (collectionSnapshot != null) {
                    UserClass usser = collectionSnapshot.toObject(UserClass.class);
                    usser.name = binding.nameEdit.getText().toString();
                    usser.userName = "@" + binding.usernameEdit.getText().toString();
//                    usser.imageIcon = binding.imgPerfil.toString();

                    db.collection("users").document(usser.email).set(usser);

                    // TODO navigate al reves
//                    navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
                }
            });


//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(binding.nameEditText.getText().toString())
//                    .setPhotoUri(downloadUriImg)
//                    .build();

        });

    }
}