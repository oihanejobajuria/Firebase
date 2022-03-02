package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebasetemplate.databinding.FragmentPostDetailBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class PostDetailFragment extends AppFragment {

    private static final String POST_ID = "param";
    private String mParam;
    private FragmentPostDetailBinding binding;

    public PostDetailFragment() {
    }

    public PostDetailFragment(String mParam) {
        this.mParam = mParam;
    }

    // -------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostDetailBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
            Post post = new Post();
            for (DocumentSnapshot documentSnapshot: collectionSnapshot) {
                post = documentSnapshot.toObject(Post.class);
                post.postid = documentSnapshot.getId();
            }

            binding.autor.setText(post.authorName);
        });

    }

    Query setQuery() {
        return db.collection("posts").whereEqualTo("postid", mParam);
    }
}