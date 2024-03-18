package com.example.blogapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.example.blogapp.SplashActivity;
import com.example.blogapp.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class Profile extends Fragment {
    FragmentProfileBinding binding;
    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initvar();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initvar() {
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        binding.uName.setText(account.getDisplayName());
        binding.uEmail.setText(account.getEmail());
        Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.profileDp);

        logoutuser();
    }


    private void logoutuser() {

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(getContext(), signInOptions);

        new AlertDialog.Builder(getActivity())
                .setTitle("Logout ?")
                .setMessage("Are you sure you want to logout from the app ?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut(); // logout from firebase

                        signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() { // logout from google_AUTH
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(getActivity().getApplicationContext(), SplashActivity.class));
                                getActivity().finish();
                            }
                        });

                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }


}