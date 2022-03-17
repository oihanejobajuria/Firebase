package com.example.firebasetemplate;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPublicProfileBinding;
import com.example.firebasetemplate.databinding.ViewholderPostBinding;
import com.example.firebasetemplate.model.Post;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PublicProfileFragment extends AppFragment {

    private FragmentPublicProfileBinding binding;
    private PostPublicAdapter adapter = new PostPublicAdapter();
    private List<Post> postsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPublicProfileBinding.inflate(inflater, container, false)).getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerPProfile.setAdapter(adapter);
        binding.recyclerPProfile.setLayoutManager(new LinearLayoutManager(getActivity()));
       // System.out.println(" item count pre"  +adapter.getItemCount());
        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
                    postsList.clear();
                    for (DocumentSnapshot documentSnapshot : collectionSnapshot) {
                        System.out.println("aaaaaaaaa");
                        Post post = documentSnapshot.toObject(Post.class);
                        post.postid = documentSnapshot.getId();
                        postsList.add(post);
                        System.out.println(postsList);
                    }
                    System.out.println("ddddddddddddddddddd");
                    adapter.notifyDataSetChanged();
                    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvv");
            System.out.println(" item count post"  +postsList.size());

                });

        db.collection("users")
                .whereEqualTo("userName", PublicProfileFragmentArgs.fromBundle(getArguments()).getUsername())
                .addSnapshotListener((collectionSnapshot, e) -> {
                    ArrayList<UserClass> user = (ArrayList<UserClass>) collectionSnapshot.toObjects(UserClass.class);

                    if (user.size() == 0) {
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
    Query setQuery() {
        return   db.collection("posts")
                .whereEqualTo("authorUsername", PublicProfileFragmentArgs.fromBundle(getArguments()).getUsername());
    }

    class PostPublicAdapter extends RecyclerView.Adapter<PostPublicAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            System.out.println("vcccccccccccccccccc");
            return new ViewHolder(ViewholderPostBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            System.out.println("bbbbbbbbbbbbbbbbbb");
            Post post = postsList.get(position);
            holder.binding.contenido.setText(post.content);
            holder.binding.autor.setText(post.authorName);
            Glide.with(requireContext()).load(post.imageUrl).into(holder.binding.imagen);
            Glide.with(requireContext()).load(post.imageUser).into(holder.binding.autorFoto);
            holder.binding.favorito.setOnClickListener(v -> db.collection("posts").document(post.postid)
                    .update("likes." + auth.getUid(),
                            !post.likes.containsKey(auth.getUid()) ? true : FieldValue.delete()));

            if (getActivity() == null) {
                return;
            }
            holder.binding.favorito.setChecked(post.likes.containsKey(auth.getUid()));
        }

        @Override
        public int getItemCount() {
            System.out.println("item count");

            return postsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewholderPostBinding binding;

            public ViewHolder(ViewholderPostBinding binding) {

                super(binding.getRoot());
                System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrr");
                this.binding = binding;
            }
        }
    }
}