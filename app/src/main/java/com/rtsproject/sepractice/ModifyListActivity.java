package com.rtsproject.sepractice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;

public class ModifyListActivity extends AppCompatActivity {
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference postRef= db.collection("posts");
    private FirestorePostsAdapter newsAdapter;
    private FirestorePostsAdapter universityAdapter;
    private FirestorePostsAdapter registrationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_list);
        CollectionReference colPostsRef = db.collection("posts");
        colPostsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                assert snapshots != null;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Message.class));
                            newsAdapter.notifyDataSetChanged();
                            universityAdapter.notifyDataSetChanged();
                            registrationAdapter.notifyDataSetChanged();


                            break;
                        case MODIFIED:
                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(Message.class));
                            newsAdapter.notifyDataSetChanged();
                            universityAdapter.notifyDataSetChanged();
                            registrationAdapter.notifyDataSetChanged();

                            break;
                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Message.class));
                            newsAdapter.notifyDataSetChanged();
                            universityAdapter.notifyDataSetChanged();
                            registrationAdapter.notifyDataSetChanged();

                            break;
                    }
                }

            }
        });
        setUpNewsRecyclerView();
        setUpUniversityRecyclerView();
        setUpRegistrationRecyclerView();
        SearchView searchPost = findViewById(R.id.mod_search_view);
        setUpSearchModifyRecyclerView("00000000000000000000000000");
        searchPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setUpSearchModifyRecyclerView(searchChecker(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setUpSearchModifyRecyclerView(searchChecker(newText));
                return false;
            }
        });

        newsAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String Id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(ModifyListActivity.this,
                        "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifyListActivity.this, ModifyActivity.class);
                intent.putExtra("DocId", Id);
                intent.putExtra("Path", path);
                intent.putExtra("From", 0);
                startActivity(intent);
            }
        });
        universityAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String Id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(ModifyListActivity.this,
                        "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifyListActivity.this, ModifyActivity.class);
                intent.putExtra("DocId", Id);
                intent.putExtra("Path", path);
                intent.putExtra("From", 0);
                startActivity(intent);
            }
        });
        registrationAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String Id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(ModifyListActivity.this,
                        "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifyListActivity.this, ModifyActivity.class);
                intent.putExtra("DocId", Id);
                intent.putExtra("Path", path);
                intent.putExtra("From", 0);
                startActivity(intent);
            }
        });
    }
    private String searchChecker(String txt){
        String give= "-";
        if(txt.equals("")){
            String distract = "null-----";
            give= distract +"1";
        }else{
            return txt;
        }
        return give;
    }
    private void setUpNewsRecyclerView(){
        Query query = postRef.whereEqualTo("Type", 1).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        newsAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_lateinfo);
        spanRecyclerView(recyclerView, newsAdapter);
    }
    private void setUpUniversityRecyclerView(){
        Query query = postRef.whereEqualTo("Type", 2).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        universityAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_university);
        spanRecyclerView(recyclerView, universityAdapter);
    }
    private void setUpRegistrationRecyclerView(){
        Query query = postRef.whereEqualTo("Type", 3).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        registrationAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_registration);
        spanRecyclerView(recyclerView, registrationAdapter);
    }
    private void setUpSearchModifyRecyclerView(String searchText){
        Query query = postRef.orderBy("Title").startAt(searchText).endAt(searchText+"\uf8ff");;
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        FirestorePostsAdapter searchAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.search_mod_recycler_view2);
        spanRecyclerView(recyclerView, searchAdapter);
        searchAdapter.startListening();
        searchAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String Id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(ModifyListActivity.this,
                        "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifyListActivity.this, ModifyActivity.class);
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
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chosenAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        newsAdapter.startListening();
        universityAdapter.startListening();
        registrationAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        newsAdapter.stopListening();
        universityAdapter.stopListening();
        registrationAdapter.stopListening();
    }

}
