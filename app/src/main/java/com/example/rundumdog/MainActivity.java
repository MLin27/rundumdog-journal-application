package com.example.rundumdog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;

import org.osmdroid.config.Configuration;

import com.example.rundumdog.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements NavigationHost{
    FirebaseAuth mAuth;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    public static ArrayList<Entry> entries;

    EntryLoop.OnItemClickListener mListener;

    ActivityMainBinding binding;


    //creates main activity layout with Home Fragment as root view and a navigation menu
    //if user in not logged in, redirects to Login Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment(new HomeFragment());

            binding.navbar.setOnItemSelectedListener(
                    item -> {
                        if (item.getItemId() == R.id.navigation_home) {
                            replaceFragment(new HomeFragment());
                        } else if (item.getItemId() == R.id.navigation_map) {
                            replaceFragment(new MapFragment());
                        }

                        return true;
                    });

            Context ctx = getApplicationContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        }
        else {
            Intent intent
                    = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(intent);
            setContentView(R.layout.activity_login);
        }

    }

    //
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    //navigates to selected fragment and adds it to the stack if selected
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    //sets permissions for the map
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        permissionsToRequest.addAll(Arrays.asList(permissions).subList(0, grantResults.length));
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }



}