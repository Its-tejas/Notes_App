package com.mydroid.notesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.mydroid.notesapp.Model.Users;

public class LogInActivity extends AppCompatActivity {

    static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Configure Google Sign In
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_SIGN_IN);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button btnGoogleSignIn = findViewById(R.id.btnGoogle);

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to NoteListActivity
//            IntentPasser(new NotesListActivity());
            Intent intent = new Intent(LogInActivity.this, NotesListActivity.class);
            startActivity(intent);
        }

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//     Handle Google sign-in button click
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    public void IntentPasser(Context context)
    {
        Intent intent = new Intent(LogInActivity.this, context.getClass());
        startActivity(intent);
    }

    // Handle the result of the sign-in intent

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
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }

        }
    }


    // Authenticate with Firebase using the Google ID token
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Users users = new Users();

//                            users.setUid(user.getUid());
                            users.setEmail(user.getEmail());
                            users.setName(user.getDisplayName());

                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Toast.makeText(LogInActivity.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LogInActivity.this, NotesListActivity.class);
                            startActivity(intent);

                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



}