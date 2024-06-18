package com.mydroid.notesapp.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mydroid.notesapp.R;

public class LoginFragment extends Fragment {

    static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        Button btnGoogleSignIn = view.findViewById(R.id.Google);

        // SharedPreferences is used to check if the user is already signed in
        SharedPreferences SP = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
        Boolean isNewUser = SP.getBoolean("isNewUser", true);

        if (!isNewUser) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NotesFragment())
                    .commit();
        }

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Get the GoogleSignInAccount object
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.e("TAG", "Google sign in failed", e);
                Toast.makeText(getActivity(), "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Authenticate with Firebase using the Google ID token
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");

                            Toast.makeText(getActivity(), "Sign in Successfully", Toast.LENGTH_SHORT).show();

                            // SharedPreferences is used to set app opened 1st time
                            SharedPreferences SP = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor = SP.edit();
                            editor.putBoolean("isNewUser", true);
                            editor.apply();

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new NotesFragment())
                                    .commit();

                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
