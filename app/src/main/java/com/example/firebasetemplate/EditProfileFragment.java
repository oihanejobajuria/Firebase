package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentEditProfileBinding;
import com.example.firebasetemplate.model.Post;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProfileFragment extends AppFragment {

    private FragmentEditProfileBinding binding;
    private Uri uriImg;
    ListenerRegistration listy;
//    static String oldName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//    String oldName = EditProfileFragmentArgs.fromBundle(getArguments()).getOldName();
    private List<Post> postsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentEditProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listy = db.collection("users").
                document(EditProfileFragmentArgs.fromBundle(getArguments()).
                        getUserId()).addSnapshotListener((collectionSnapshot, e) -> {
            if (collectionSnapshot != null) {
                UserClass user = collectionSnapshot.toObject(UserClass.class);
                String split = user.userName.substring(1);

                binding.nameEdit.setText(user.name);
                binding.usernameEdit.setText(split);

                if (getActivity() != null) {
                    Glide.with(requireActivity()).load(user.imageIcon).centerCrop().into(binding.imgPerfil);
                    Glide.with(requireActivity()).load(user.imageIcon).centerCrop().into(binding.imgPerfil2);
                }
            }
        });


        binding.imgPerfil.setOnClickListener(v -> galeria.launch("image/*"));
        appViewModel.uriImagenPerfilEdit.observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).into(binding.imgPerfil);
            Glide.with(this).load(uri).into(binding.imgPerfil2);
            uriImg = uri;
        });

        binding.btnSave.setOnClickListener(view1 -> {
            if (uriImg != null) {
                FirebaseStorage.getInstance()
                        .getReference("/images/" + UUID.randomUUID() + ".jpg")
                        .putFile(uriImg)
                        .continueWithTask(task1 -> task1.getResult().getStorage().getDownloadUrl())
                        .addOnSuccessListener(urlDescarga -> {
                            updateProfile(urlDescarga);
                        });
            } else {
                updateProfile(null);
            }


        });

    }


    private void updateProfile(Uri urlDescarga) {

        FirebaseUser currentUser = auth.getCurrentUser();
        UserProfileChangeRequest.Builder profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.nameEdit.getText().toString());


        if (urlDescarga != null) {
            profile.setPhotoUri(urlDescarga);
        }

        currentUser.updateProfile(profile.build())
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        db.collection("users").
                                document(EditProfileFragmentArgs.fromBundle(getArguments()).
                                        getUserId()).addSnapshotListener((collectionSnapshot, e) -> {
                            if (collectionSnapshot != null) {
                                UserClass usser = collectionSnapshot.toObject(UserClass.class);
                                usser.name = currentUser.getDisplayName();
                                usser.userName = "@" + binding.usernameEdit.getText().toString();
                                usser.imageIcon = currentUser.getPhotoUrl().toString();
                                // no se puede cambiar email

                                db.collection("users").document(usser.email).set(usser);

//                                navController.popBackStack();
//                                Toast.makeText(getActivity(), "Profile changed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }).addOnCompleteListener(task3 -> {
            if (task3.isSuccessful()) {
                db.collection("posts")
                        .whereEqualTo("authorName", EditProfileFragmentArgs.fromBundle(getArguments()).getOldName())
                        .addSnapshotListener((collectionSnapshot, e) -> {
                            postsList.clear();
                            for (DocumentSnapshot d : collectionSnapshot) {
                                Post post = d.toObject(Post.class);
                                System.out.println("aaaaaaaaaa " + post.postid);
                                post.postid = d.getId();
                                postsList.add(post);
                            }
                            for (Post p : postsList) {
                                p.authorName = currentUser.getDisplayName();
                                p.authorUsername = "@" + binding.usernameEdit.getText().toString();
                                p.imageUser = currentUser.getPhotoUrl().toString();
                                System.out.println("bbbbbbbb " + p.postid);
                                db.collection("posts").document(p.postid).set(p);
                            }

                        });
            }
        });
    }


//        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
//            postsList.clear();
//            for (DocumentSnapshot documentSnapshot : collectionSnapshot) {
//                Post post = documentSnapshot.toObject(Post.class);
//                post.postid = documentSnapshot.getId();
//                postsList.add(post);
//            }
//            adapter.notifyDataSetChanged();
//        });
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        listy.remove();
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> appViewModel.setUriImagenPerfilEdit(uri));


//    Query setQuery() {
//        return db.collection("posts").whereEqualTo("authorName", oldName);
//    }
}