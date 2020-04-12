package com.ming.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ming.journalapp.model.Journal;
import com.ming.journalapp.model.User;
import com.ming.journalapp.ui.JournalRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class JournalListActvity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference journalRef=firestore.collection("journals");
    private FirebaseUser currentUser;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private List<Journal> journalList= new ArrayList<>();
    private JournalRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list_actvity);
        firebaseAuth= FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        if (journalList.isEmpty()){
            noJournal=findViewById(R.id.tv_no_journal);
        }
        recyclerView=findViewById(R.id.jounral_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));





    }


    @Override
    protected void onStart() {
        super.onStart();

        journalRef.whereEqualTo("userId",currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot doc:queryDocumentSnapshots){
                        Journal journal=doc.toObject(Journal.class);
                        assert journal != null;
                        journal.setJournalId(doc.getId());
                        journalList.add(journal);
                    }
                    adapter=new JournalRecyclerAdapter(JournalListActvity.this,journalList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JournalListActvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_add_jounral:
                if (currentUser!=null) startActivity(new Intent(JournalListActvity.this,JournalActivity.class));
                break;
            case R.id.menu_sign_out:
                if (currentUser!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActvity.this,MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
