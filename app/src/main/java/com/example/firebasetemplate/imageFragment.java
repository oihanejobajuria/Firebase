package com.example.firebasetemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentImageBinding;
import com.example.firebasetemplate.model.Post;

public class imageFragment extends AppFragment {
    private FragmentImageBinding binding;

    public imageFragment() {
    }

    // -------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentImageBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db.collection("posts").
                document(imageFragmentArgs.fromBundle(getArguments()).
                        getPostId()).addSnapshotListener((collectionSnapshot, e) -> {
            if (collectionSnapshot != null) {
                Post post = collectionSnapshot.toObject(Post.class);

                if (getActivity() == null) {
                    return;
                } else {
                    Glide.with(getActivity()).load(post.imageUrl).centerCrop().into(binding.image);
                }
            }
        });

    }
}