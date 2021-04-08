package com.rtsproject.sepractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference postRef= db.collection("posts");
    private FirestorePostsAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchPost = findViewById(R.id.user_search_view);
        final TextView  greetingText = findViewById(R.id.greeting_text_searchtxt);
        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DocumentReference userDocRef = db.collection("users").document(userID);
            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    greetingText.setText("Welcome "+ documentSnapshot.getString("Name")+" !");
                }
            });
        }
        setUpSearchModifyRecyclerView("00000000000000000000000000");
        searchPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setUpSearchModifyRecyclerView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setUpSearchModifyRecyclerView(newText);
                return false;
            }
        });

    }
    private void setUpSearchModifyRecyclerView(String searchText){
        Query query = postRef.orderBy("Title").startAt(searchText).endAt(searchText+"\uf8ff");;
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        searchAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.result_recycler_view);
        spanRecyclerView(recyclerView, searchAdapter);
        searchAdapter.startListening();
        searchAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String Id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(SearchActivity.this,
                        "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, ContentViewerActivityA.class);
                intent.putExtra("DocId", Id);
                intent.putExtra("Path", path);
                intent.putExtra("From", 0);
                startActivity(intent);
            }
        });
    }
    public void spanRecyclerView(RecyclerView recyclerView, FirestorePostsAdapter chosenAdapter){
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chosenAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        searchAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchAdapter.stopListening();
    }
}
