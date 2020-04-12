package com.ming.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ming.journalapp.model.User;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference userRef;
    private Button btnSign, btnGotoLogin;
    private EditText etUserName, etEmail, etPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        userRef = firestore.collection("users");
        btnSign = findViewById(R.id.btn_signin);
        btnGotoLogin = findViewById(R.id.btn_goto_login);
        etUserName = findViewById(R.id.et_username_signin);
        etEmail = findViewById(R.id.et_email_signin);
        etPassword = findViewById(R.id.et_password_sign);
        progressBar = findViewById(R.id.pb_signin);


        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = etEmail.getText().toString();
                String usernameStr = etUserName.getText().toString();
                String passwordStr = etPassword.getText().toString();

                createAccount(usernameStr, emailStr, passwordStr);
            }
        });
        btnGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SigninActivity.this, LoginActivity.class));
            }
        });
    }

    private void createAccount(final String username, final String email, final String password) {
        if (vaildUserInput(username, email, password)) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                Map<String, Object> user = new User(username, email, currentUser.getUid()).toMap();
                                userRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(SigninActivity.this, JournalActivity.class);
                                                    intent.putExtra(User.KEY_USER, Objects.requireNonNull(task.getResult()).toObject(User.class));
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SigninActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SigninActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean vaildUserInput(String username, String email, String password) {

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("email can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
            return false;

        }
        if (TextUtils.isEmpty(username)) {
            etUserName.setError("username can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("password can't be empty");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;

        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
//        currentUser = firebaseAuth.getCurrentUser();
//        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
