package com.example.firebasetemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.ActivityMainBinding;
import com.example.firebasetemplate.databinding.NavHeaderMainBinding;
import com.example.firebasetemplate.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavHeaderMainBinding navHeaderMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());
        navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        FirebaseFirestore.getInstance().setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        setSupportActionBar(binding.toolbar);

        navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.postsHomeFragment, R.id.postsLikeFragment, R.id.postsMyFragment)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.signInFragment ||
                    destination.getId() == R.id.registerFragment) {
                binding.toolbar.setVisibility(View.GONE);
                binding.bottomNavView.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.newPostFragment ||
                    destination.getId() == R.id.postDetailFragment ||
                    destination.getId() == R.id.publicProfileFragment ||
                    destination.getId() == R.id.profileFragment ||
                    destination.getId() == R.id.editProfileFragment ||
                    destination.getId() == R.id.imageFragment) {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.bottomNavView.setVisibility(View.GONE);
            } else {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.bottomNavView.setVisibility(View.VISIBLE);
            }
        });

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.getCurrentUser().getEmail())
                        .addSnapshotListener((collectionSnapshot, e) -> {
                            if (collectionSnapshot != null) {
                                System.out.println(collectionSnapshot);
                                UserClass user = collectionSnapshot.toObject(UserClass.class);
                                if (user != null) {

                                    Glide.with(getApplicationContext()).load(user.imageIcon).circleCrop().into(navHeaderMainBinding.photo);

                                    navHeaderMainBinding.username.setText(user.userName);
                                    navHeaderMainBinding.name.setText(user.name);
                                    navHeaderMainBinding.email.setText(user.email);
                                    Log.e("sdfdfs", "USER:" + firebaseAuth.getCurrentUser().getEmail());
                                }
                            }
                        });

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}