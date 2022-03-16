package com.example.firebasetemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPostsBinding;
import com.example.firebasetemplate.databinding.FragmentPublicProfileBinding;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class PublicProfileFragment extends AppFragment {

    private FragmentPublicProfileBinding binding;
//    private PostsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPublicProfileBinding.inflate(inflater, container, false)).getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db.collection("users")
                .whereEqualTo("userName", PublicProfileFragmentArgs.fromBundle(getArguments()).getUsername())
                .addSnapshotListener((collectionSnapshot, e) -> {

                    System.out.println( "aaaaaaaaaaaaaaa" + PublicProfileFragmentArgs.fromBundle(getArguments()).getUsername());
                    ArrayList<UserClass> user = (ArrayList<UserClass>) collectionSnapshot.toObjects(UserClass.class);

                    if (user.size()==0) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.namePProfile.setText(user.get(0).name);
                        binding.usernamePProfile.setText(user.get(0).userName);

                        if (getActivity() != null) {
                            Glide.with(this).load(user.get(0).imageIcon).centerCrop().into(binding.imgPProfile);
                        }

                    }

                });
    }
}