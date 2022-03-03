package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPostDetailBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.FieldValue;

public class PostDetailFragment extends AppFragment {

    //    private static final String POST_ID = "param";
//    private String mParam;
    private FragmentPostDetailBinding binding;

    public PostDetailFragment() {
    }

//    public PostDetailFragment(String mParam) {
//        this.mParam = mParam;
//    }

    // -------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostDetailBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db.collection("posts").
                document(PostDetailFragmentArgs.fromBundle(getArguments()).
                        getPostId()).addSnapshotListener((collectionSnapshot, e) -> {
            if (collectionSnapshot != null) {
                Post post = collectionSnapshot.toObject(Post.class);
                binding.autor.setText(post.authorName);
                binding.contenido.setText(post.content);

                if (getActivity() == null) {
                    return;
                } else {
                    Glide.with(getActivity()).load(post.imageUrl).centerCrop().into(binding.imagen);
                    Glide.with(getActivity()).load(post.imageUser).centerCrop().into(binding.autorFoto);
                }

                binding.favorito.setChecked(post.likes.containsKey(auth.getUid()));
                System.out.println("PostId: "
                        + post.postid);


                post.postid = db.collection("posts").
                        document(PostDetailFragmentArgs.fromBundle(getArguments()).getPostId())
                        .getId();

                binding.favorito.setOnClickListener(v ->
                        db.collection("posts").document(post.postid)
                        .update("likes." + auth.getUid(),
                                !post.likes.containsKey(auth.getUid()) ? true : FieldValue.delete()));


            }
        });


    }
}