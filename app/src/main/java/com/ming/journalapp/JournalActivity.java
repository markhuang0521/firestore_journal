package com.ming.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ming.journalapp.model.Journal;
import com.ming.journalapp.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class JournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private TextView username, postDate;
    private ImageView btnAddPhoto, journalBg;
    private EditText journalTitle, journalDesc;
    private Button btnSave;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private StorageReference imagePath = storageReference.child("journal_images");
    private CollectionReference collectionref = firestore.collection("journals");
    private Uri imageUri;
    private Uri defaultImageUri = Uri.parse("res:///" + R.drawable.cannon_bg);
    private SimpleDateFormat df = new SimpleDateFormat("MMMM-dd-yyyy", Locale.US);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                } else {
                }
            }
        };

        username = findViewById(R.id.tv_journal_username);
        postDate = findViewById(R.id.tv_journal_date);
        journalTitle = findViewById(R.id.et_journal_title);
        journalDesc = findViewById(R.id.et_journal_desc);
        progressBar = findViewById(R.id.progressBar);
        btnSave = findViewById(R.id.btn_journal_save);
        btnAddPhoto = findViewById(R.id.iv_journal_add_image);
        journalBg = findViewById(R.id.iv_journal_bg);
        journalBg.setImageResource(R.drawable.cannon_bg);
        progressBar.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);

        User userObj = getIntent().getParcelableExtra(User.KEY_USER);
        if (userObj != null) {
            username.setText(userObj.getUsername());
        }
        postDate.setText(df.format(Calendar.getInstance().getTime()));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_journal_save:
                saveJournal();

                break;
            case R.id.iv_journal_add_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);

                break;
        }
    }

    private void saveJournal() {
        final String title = journalTitle.getText().toString().trim();
        final String desc = journalDesc.getText().toString().trim();
        final String username = !TextUtils.isEmpty(currentUser.getEmail()) ? currentUser.getEmail() : "mark";
        imageUri = imageUri != null ? imageUri : defaultImageUri;
        if (TextUtils.isEmpty(title)) {
            journalTitle.setError("title cant be empty");
        } else if (TextUtils.isEmpty(desc)) {
            journalDesc.setError("description cant be empty");
        } else {
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference filePath = imagePath.child(currentUser.getUid()).child(title + "_" + Timestamp.now().getSeconds());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> journal = new Journal(title, desc, currentUser.getUid(), uri.toString(), postDate.getText().toString(), username).toMap();
                            collectionref.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(JournalActivity.this, "note Successfuly added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(JournalActivity.this, JournalListActvity.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(JournalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                    Toast.makeText(JournalActivity.this, "journal Successfuly uploaded", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(JournalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                journalBg.setImageURI(imageUri);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}
