package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class HomeFragmentActivity extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference postRef= db.collection("posts");
    private FirestorePostsAdapter newsAdapter, universityAdapter, registrationAdapter;
    private ArrayList<InformationObject> InfoList = new ArrayList<>();
    private ArrayList<ExploreObject> ExploreList = new ArrayList<>();
    private ArrayList<RegistrationObject> RegistrationList = new ArrayList<>();
    private TextView greetingText;
    private ImageView indicatorDatabase;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.home_fragment, container, false);
        greetingText = content.findViewById(R.id.greeting_text);
        indicatorDatabase = content.findViewById(R.id.database_connection_home);
        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DocumentReference userDocRef = db.collection("users").document(userID);
            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    greetingText.setText("Welcome "+ documentSnapshot.getString("Name")+" !");
                    indicatorDatabase.setColorFilter(getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_IN);
                }
            });
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
            setUpNewsRecyclerView(content);
            setUpUniversityRecyclerView(content);
            setUpRegistrationRecyclerView(content);
            newsAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String Id = documentSnapshot.getId();
                    String path = documentSnapshot.getReference().getPath();
                    Toast.makeText(getActivity(),
                            "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                    intent.putExtra("DocId", Id);
                    intent.putExtra("Path", path);
                    startActivity(intent);
                }
            });
            universityAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String Id = documentSnapshot.getId();
                    String path = documentSnapshot.getReference().getPath();
                    Toast.makeText(getActivity(),
                            "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                    intent.putExtra("DocId", Id);
                    intent.putExtra("Path", path);
                    startActivity(intent);
                }
            });
            registrationAdapter.setOnItemClickListener(new FirestorePostsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String Id = documentSnapshot.getId();
                    String path = documentSnapshot.getReference().getPath();
                    Toast.makeText(getActivity(),
                            "Item " + Id + " with path "+ path +" ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                    intent.putExtra("DocId", Id);
                    intent.putExtra("Path", path);
                    startActivity(intent);
                }
            });
        }else{
            indicatorDatabase.setVisibility(View.GONE);
            SpanInfoRecyclerView(content);
            SpanExploreRecyclerView(content);
            SpanRegistrationRecyclerView(content);
        }
        return content;
    }

    public void trace(String tag, String message){
        Log.d(tag , message);
    }
    private LinearLayoutManager layoutManagercode(){
        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), getActivity().getResources().getInteger(R.integer.number_of_grid_items));
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return layoutManager;
    }
    public void recyclerViewManagerCode(View view, int XmlRvId, androidx.recyclerview.widget.RecyclerView.Adapter adapter){
        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(XmlRvId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagercode());
        recyclerView.setAdapter(adapter);
    }

    public void SpanInfoRecyclerView(View content){
        InfoList.add(new InformationObject(R.drawable.covid19,"COVID Prevention","Regularly and thoroughly clean your hands with an alcohol-based hand rub or wash them with soap and water. Why? Washing your hands with soap and water or using alcohol-based hand rub kills viruses that may be on your hands.\n" +
                "Maintain at least 1 metre (3 feet) distance between yourself and others. Why? When someone coughs, sneezes, or speaks they spray small liquid droplets from their nose or mouth which may contain virus. If you are too close, you can breathe in the droplets, including the COVID-19 virus if the person has the disease.\n" +
                "Avoid going to crowded places. Why? Where people come together in crowds, you are more likely to come into close contact with someone that has COIVD-19 and it is more difficult to maintain physical distance of 1 metre (3 feet).\n" +
                "Avoid touching eyes, nose and mouth. Why? Hands touch many surfaces and can pick up viruses. Once contaminated, hands can transfer the virus to your eyes, nose or mouth. From there, the virus can enter your body and infect you.\n" +
                "Make sure you, and the people around you, follow good respiratory hygiene. This means covering your mouth and nose with your bent elbow or tissue when you cough or sneeze. Then dispose of the used tissue immediately and wash your hands. Why? Droplets spread virus. By following good respiratory hygiene, you protect the people around you from viruses such as cold, flu and COVID-19.\n" +
                "Stay home and self-isolate even with minor symptoms such as cough, headache, mild fever, until you recover. Have someone bring you supplies. If you need to leave your house, wear a mask to avoid infecting others. Why? Avoiding contact with others will protect them from possible COVID-19 and other viruses."));
        InfoList.add(new InformationObject(R.drawable.online_teaching,"Remote Teaching for International Students","At 2:00 p.m. on March 2nd, 50 international students at SWPU across different time zones reunited in the online classroom. The students were from 7 countries, including Russia, Thailand and Uzbekistan. The course they took was Comprehensive Chinese.\n" +
                "\n" +
                "\"Considering the time difference of students in various countries, we teach the remote courses from 2 to 9 p.m. Beijing time so that all students can attend the courses in the day\", said Dai Lei, director of the Office of International Cooperation and Exchange.\n" +
                "\n" +
                "Students were attracted by this new method of teaching, because it's a brand new experience. They believe they can quickly adapt to this new teaching mode and follow the progress, and are looking forward to the next lecture. After the first day's class, students expressed how they felt. \"Although classmates and lecturers are in different countries, seeing the familiar faces thousand miles away through webcams and being able to learn Chinese with friends again are thrilling.\" (Contributed by the Office of International Cooperation and Exchange)"));
        InfoList.add(new InformationObject(R.drawable.mask,"Importance of Mask","Masks can help prevent the spread of the virus from the person wearing the mask to others. Masks alone do not protect against COVID-19, and should be combined with physical distancing and hand hygiene. Follow the advice provided by your local health authority."));

        InformationAdapter infoAdapter = new InformationAdapter(InfoList);
        recyclerViewManagerCode(content, R.id.RecyclerView_LatestInfo , infoAdapter);
        infoAdapter.setOnItemClickListener(new InformationAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "Item clicked: " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                intent.putExtra("Title",InfoList.get(position).getInfoTitle());
                intent.putExtra("Content", InfoList.get(position).getInfoContent());
                intent.putExtra("Image", InfoList.get(position).getPreview());
                startActivity(intent);
            }
        });

    }
    public void SpanExploreRecyclerView(View content){/*R.string.article_prev*/
        ExploreList.add(new ExploreObject(R.drawable.swpu_schools,"Academy Buddings","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.swpu_monument,"University Canteen","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.swpu_monument,"University Shops","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.uni_map,"Useful Apps","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.uni_map,"University Map","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.teacher,"Teacher's Info","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));
        ExploreList.add(new ExploreObject(R.drawable.study,"Study Plans","Massa, vitae lacus, lorem eu. Nibh rutrum tincidunt etiam nibh mauris congue eu proin. Vitae quis ut nunc vestibulum, lacus in enim velit. Mauris in ipsum enim nibh erat congue vulputate etiam. Tristique nec, ac pretium tincidunt. Commodo leo gravida bibendum justo rhoncus donec augue ornare sapien. Etiam venenatis blandit pretium, egestas mattis. Purus odio elit sed ornare ut risus, nibh. In diam quis viverra etiam nunc a nunc quis augue. Condimentum tempus sit lectus in bibendum felis eu. Etiam molestie dui luctus turpis tempor, sed.\n" +
                "Purus nec urna feugiat elementum in. Nibh fermentum scelerisque diam facilisis gravida tellus. Vitae ultrices sed vitae turpis magna lacus ultrices. Ornare arcu feugiat a, tristique phasellus aenean tellus amet, laoreet. Dictum et nunc ipsum sapien non. Metus et accumsan, quis placerat. Leo accumsan mi orci, amet, arcu at. Velit, quis risus erat amet. Bibendum cursus nisi, ut at pretium. Sed quisque adipiscing lectus donec turpis. Facilisi commodo nullam adipiscing vitae. Amet sollicitudin in porta libero sit.\n" +
                "Ultrices scelerisque ipsum eget cras est. Hendrerit diam sem lectus amet a. Amet, fusce in nisi sit sollicitudin urna, justo. Enim pulvinar tincidunt massa velit feugiat scelerisque. Bibendum fames dapibus faucibus ullamcorper. Elit, orci, cras lectus est, purus netus. Ullamcorper arcu urna tellus ut maecenas nam nibh tempus porttitor. Sollicitudin consectetur nisl ac id eget euismod in dis consequat.\n" +
                "Morbi platea auctor tincidunt enim egestas dui placerat vitae enim. Odio cras id consequat sit arcu amet vel in. Erat consectetur scelerisque eget et, porta porta tincidunt neque. Ornare ac amet libero dolor odio lectus nullam scelerisque massa. Duis neque elit ipsum aliquam, facilisis morbi consectetur iaculis. Sed libero tristique mauris, tempor scelerisque egestas quam justo. Amet aenean aliquet cursus nunc, sagittis scelerisque vitae. Adipiscing fermentum id lacus nisi dictum quam ornare. Bibendum gravida fringilla ultrices magna at purus, lobortis. Turpis ante lacus nunc ornare ullamcorper in. Justo, lobortis in sed eros sem sollicitudin. Condimentum natoque senectus urna aenean.\n" +
                "Fames integer dui, vulputate varius aenean a. Ultrices duis lectus ut quam sed nec ante fermentum nisi. Lorem cursus nulla pellentesque consequat turpis adipiscing lacus, elementum, duis. Phasellus sit potenti nec dictum lorem interdum. Tristique sit habitasse elementum in lacus, metus bibendum eu. Ipsum volutpat, lobortis lorem senectus diam ut. Purus feugiat arcu laoreet eleifend adipiscing quis nunc, quisque. Facilisis adipiscing imperdiet nisl mus fusce. Et varius faucibus sem volutpat.\n" +
                "Risus commodo in duis felis nunc amet. Tristique non lorem pellentesque quam dolor ipsum ante erat. Lacinia sed duis tristique elementum velit. Sodales vulputate cras eget consequat sed cras. Fermentum nisl netus duis massa leo lacus, urna, tortor. Gravida quam nisl, id cras urna, nunc vestibulum. Nulla porta risus praesent amet nibh vulputate. Quis et urna feugiat pharetra, pharetra, in egestas in tellus."));

        ExploreAdapter exploreAdapter = new ExploreAdapter(ExploreList);
        recyclerViewManagerCode(content, R.id.RecyclerView_Explore, exploreAdapter);
        exploreAdapter.setOnItemClickListener(new ExploreAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "Item clicked: " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                intent.putExtra("Title",ExploreList.get(position).getExploreTitle());
                intent.putExtra("Content", ExploreList.get(position).getExploreContent());
                intent.putExtra("Image", ExploreList.get(position).getPreview());
                startActivity(intent);
            }
        });
    }

    public void SpanRegistrationRecyclerView(View content){
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_bg,"STEP 1: HOW COULD I GO TO THE UNIVERSITY FROM AIRPORT.","Metro is one of the most popular and affordable transportation in China . So it is one of the easiest way to reach university. First you need to get out of the airport.\n" +
                "Then you have to find out which side of the airport the metro line is actually on. Don't be disappointed if you can't find the Metro line.\n" +
                "Don't be discouraged , you will find the metro line very quickly with the exact location by using map you can find it . which already given on our web site description. \n" +
                "Taxi is very comfortable and time saving transportation in China. So there is no doubt that taxi will reach the university very soon. \n" +
                "You can take advantage of this service by using some application. So here is the name of one global which are most use in china: DiDi Greater China. \n" +
                "By using this app you can call a taxi and set the exact location on the app map ( just such southwest petroleum university on the app such bar ) and confirm your ride."));
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_monument,"STEP 2: REGISTRATION TO THE OFFICE OF INTERNATIONAL AFFAIR.","Location of Office of International Affair (OIA)\n" +
                "If you need more information or any other kinds of help then you can contact to OIA\n" +
                "Don't Forget to bring important Documents (Visa, Passport, JW, Photo, Admission Notice, etc)\n"+
                "No. 8 Xindu Avenue (Chengdu Campus), Xindu District, Chengdu Postcode: 610500\n" +
                "Southwest petroleum university,library building ,6th floor"));
        RegistrationList.add(new RegistrationObject(R.drawable.chinese_sim,"STEP 3: HOW TO BUY A SIM CARD.","Let's know about the mobile network company\n" +
                "In the university campus you will find 2 SIM operators shop and registration center. \n" +
                "China unicom & China telecom. But there are 3 company's mobile network you will get in the university and another one is China Mobile. \n" +
                "You can take any of then all of them have the 4G network connectivity and recently they are offering the newest technology the 5G network in the university campus.\n" +
                "Documents you will need to buy a SIM card in China\n" +
                "1. passport copy of first page and visa page\n" +
                "2. Passport\n" +
                "3. Admission Notice\n" +
                "4. JW202 Form\n" +
                "5. 2 copy photo\n" +
                "6. Cash RMB or WECHAT PAY / ALIPAY"));
        RegistrationList.add(new RegistrationObject(R.drawable.chinese_bank,"STEP 4: HOW TO OPEN A BANK ACCOUNT.", "Let's know about the Banks in Campus\n" +
                "In Xindu you will find almost every banks branches ,even you don't need to go outside the campus to open a bank account or cash out the money from your bank card, \n" +
                "in university campus you will find 2 bank Bank of China and the ICBC bank and a few ATM booth in the campus in different location in the campus , \n" +
                "so donn't need to worry about the banking system in the campus , everything is very easy and there will be some volunteer to help you."));
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_boxue,"STEP 5: THE DEPARTMENT REGISTRATION.", "All the departments name and building name\n" +
                "1. School of Oil & Natural Gas Engineering\n" +
                "2. School of Mechanical Engineering\n" +
                "3. School of Chemistry & Chemical Engineering\n" +
                "4. School of Material Science & Engineering\n" +
                "5. School of Computer Science\n" +
                "6. School of Electronics and Information Engineering\n" +
                "7. School of Civil Engineering & Architecture\n" +
                "8. School of Economics and Management\n" +
                "9. School of Foreign Languages\n" +
                "\n" +
                "Documents you will need to registered in departments\n" +
                "1. Passport\n" +
                "2. Registration List"));
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_dorm,"STEP 6: THE DORMITORY CHECK-IN.", "The university is offering on campus housing for every student. The male and female dormitory are different and the are located in different places in the campus & the room system is also different for the male and female dormitory.\n" +
                "For the male dormitory, a room is sheared by 3 students and for the female dormitory there is a single room for a student and also 2 sheared room are there. \n" +
                "And the university are offering each and every thing for living both for the male and female dormitory.\n" +
                "Documents you will need to registered in Dormitory\n" +
                "1. Passport\n" +
                "2. Registration List"));
        RegistrationList.add(new RegistrationObject(R.drawable.fees,"STEP 7: HOW TO PAY THE FEE.", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));
        RegistrationList.add(new RegistrationObject(R.drawable.police_china,"STEP 8: ACCOMMODATION REGISTRATION IN CHENGDONG POLICE STATION.", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));
        RegistrationList.add(new RegistrationObject(R.drawable.pexam,"STEP 9: THE PHYSICAL EXAMINATION.", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_monument,"STEP 10: SUBMIT THE PHYSICAL REPORT TO OFFICE OF INTERNATIONAL AFFAIR(OIA).", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));
        RegistrationList.add(new RegistrationObject(R.drawable.chengdu_psb,"STEP 11: APPLY FOR RESIDENT PERMIT ON SICHUAN ENTRY EXIT OFFICE.", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));
        RegistrationList.add(new RegistrationObject(R.drawable.swpu_monument,"STEP 12: SECOND ACCOMMODATION REGISTRATION.", "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "Tristique elementum feugiat ultricies pellentesque diam. Scelerisque risus tristique diam mauris adipiscing sem eget amet auctor. Facilisis lectus amet lorem facilisi consectetur lacus amet porttitor. Arcu id tellus pulvinar imperdiet viverra congue. Et turpis in maecenas iaculis facilisi sed volutpat at. Sit sapien et porta morbi neque.\n" +
                "d volutpat at. Sit sapien et porta morbi neque.\n"));

        RegistrationAdapter registrationAdapter = new RegistrationAdapter(RegistrationList);
        recyclerViewManagerCode(content, R.id.RecyclerView_Registration, registrationAdapter);
        registrationAdapter.setOnItemClickListener(new RegistrationAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "Item clicked: " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ContentViewerActivityA.class);
                intent.putExtra("Title", RegistrationList.get(position).getRegistrationTitle());
                intent.putExtra("Content", RegistrationList.get(position).getRegistrationContent());
                intent.putExtra("Image", RegistrationList.get(position).getPreview());
                startActivity(intent);
            }
        });
    }
    private void setUpNewsRecyclerView(View content){
        Query query = postRef.whereEqualTo("Type", 1).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        newsAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = content.findViewById(R.id.RecyclerView_LatestInfo);
        spanRecyclerView(recyclerView, newsAdapter);
    }
    private void setUpUniversityRecyclerView(View content){
        Query query = postRef.whereEqualTo("Type", 2).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        universityAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = content.findViewById(R.id.RecyclerView_Explore);
        spanRecyclerView(recyclerView, universityAdapter);
    }
    private void setUpRegistrationRecyclerView(View content){
        Query query = postRef.whereEqualTo("Type", 3).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirestorePostsObject> options = new FirestoreRecyclerOptions.Builder<FirestorePostsObject>()
                .setQuery(query,FirestorePostsObject.class)
                .build();
        registrationAdapter = new FirestorePostsAdapter(options);
        RecyclerView recyclerView = content.findViewById(R.id.RecyclerView_Registration);
        spanRecyclerView(recyclerView, registrationAdapter);
    }
    public void spanRecyclerView(RecyclerView recyclerView, FirestorePostsAdapter chosenAdapter){
        final LinearLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chosenAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            if (newsAdapter != null && universityAdapter != null &&  registrationAdapter != null) {
                newsAdapter.startListening();
                universityAdapter.startListening();
                registrationAdapter.startListening();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
            if (newsAdapter != null && universityAdapter != null &&  registrationAdapter != null) {
                newsAdapter.stopListening();
                universityAdapter.stopListening();
                registrationAdapter.stopListening();
            }
        }
    }

}
