package com.rtsproject.sepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ModifyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Date c = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private TextView TitleTV, ContentTV;
    private int Type, imageSelectIndicator;
    public int RESULT_LOAD_IMG = 1;
    private String Title, Content, UserName, UserEmail,Date,UserID, ImageURL;
    private ImageView ThumbnailIV;
    private Button PublishB, DeleteB, DiscardB, ChoosePicButton;
    private RadioButton newsOptionRB, universityOptionRB, registrationOptionRB;
    private RadioGroup radiobuttonOption;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        newsOptionRB= findViewById(R.id.news_radio_button);
        universityOptionRB = findViewById(R.id.university_radio_button);
        registrationOptionRB = findViewById(R.id.registration_radio_button);
        TitleTV = findViewById(R.id.post_title_edit);
        ContentTV = findViewById(R.id.post_content_edit);
        ThumbnailIV = findViewById(R.id.thumbnail_image_view);
        PublishB = findViewById(R.id.publish_button);
        DeleteB = findViewById(R.id.delete_button);
        DiscardB=findViewById(R.id.discard_button);
        ChoosePicButton = findViewById(R.id.choose_picture_btn);
        radiobuttonOption = findViewById(R.id.radio_button_group);
        TextView deleteNote = findViewById(R.id.delete_note);
        imageSelectIndicator = 0;



//        if(getIntent()!=null){
//            int arrived = getIntent().getIntExtra("From",1);
//        }

        int arrived = getIntent().getIntExtra("From",1);

        if(arrived ==1){
            DeleteB.setVisibility(View.GONE);
            deleteNote.setVisibility(View.GONE);
        }else{
            DeleteB.setVisibility(View.VISIBLE);
            deleteNote.setVisibility(View.VISIBLE);
        }
        if(getIntent().getStringExtra("DocId") != null){
            imageSelectIndicator++;
            DocumentReference postDocRef = db.collection("posts").document(""+getIntent().getStringExtra("DocId"));
            postDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    TitleTV.setText(documentSnapshot.getString("Title"));
                    ContentTV.setText(documentSnapshot.getString("Contents"));
                    ImageURL = documentSnapshot.getString("Picture");
                    new DownLoadImageTask(ThumbnailIV).execute(ImageURL);
                    int type = Objects.requireNonNull(documentSnapshot.getLong("Type")).intValue();
                    switch (type){
                        case 1:
                            newsOptionRB.toggle();
                            break;
                        case 2:
                            universityOptionRB.toggle();
                            break;
                        case 3:
                            registrationOptionRB.toggle();
                            break;
                    }
                    PublishB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Title = TitleTV.getText().toString();
                            Content = ContentTV.getText().toString();
                            Date = df.format(c);
                            UserEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            DocumentReference userDocRef = db.collection("users").document(UserID );
                            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserName = documentSnapshot.getString("Name");
                                }
                            });

                            if(Title.equals("")){
                                Toast.makeText(ModifyActivity.this,
                                        "Please enter the Title post", Toast.LENGTH_SHORT).show();
                            }else if(Content.equals("")){
                                Toast.makeText(ModifyActivity.this,
                                        "Please write the content", Toast.LENGTH_SHORT).show();
                            }else if(Title.length()>70){
                                Toast.makeText(ModifyActivity.this,
                                        "Title is too long", Toast.LENGTH_SHORT).show();
                            }else if(radiobuttonOption.getCheckedRadioButtonId() == -1){
                                Toast.makeText(ModifyActivity.this,
                                        "Please choose the type of the post", Toast.LENGTH_SHORT).show();
                            }else{
                                new AlertDialog.Builder(ModifyActivity.this)
                                        .setTitle("Save Changes Confirmation")
                                        .setMessage("Are you sure going to make changes on this post?")
                                        .setIcon(R.drawable.ic_uil_file_upload_alt)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Toast.makeText(ModifyActivity.this, "Please Wait ... it may took a while", Toast.LENGTH_SHORT).show();
                                                Type = TypeDetermination(newsOptionRB, universityOptionRB, registrationOptionRB);
                                                final DocumentReference docRef = db.collection("posts").document(""+getIntent().getStringExtra("DocId"));
                                                Map<String, Object> post = new HashMap<>();
                                                post.put("Title", Title);
                                                post.put("Picture",ImageURL);
                                                post.put("Contents",Content);
                                                post.put("Type",Type);
                                                post.put("Date",Date);
                                                post.put("Email",UserEmail);
                                                post.put("Writer",UserName);
                                                docRef.set(post)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                uploadImage(docRef,getIntent().getStringExtra("DocId"));
                                                                Toast.makeText(ModifyActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                                                Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                                                                startActivity(changeScreen);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ModifyActivity.this, "Fail to Save", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
                            }
                        }
                    });
                    DeleteB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(ImageURL);
                            new AlertDialog.Builder(ModifyActivity.this)
                                    .setTitle("Delete Permanently")
                                    .setMessage("Any deleted post can not be retrieved")
                                    .setIcon(R.drawable.ic_baseline_delete_forever)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // File deleted successfully
                                                    db.collection("posts").document(""+getIntent().getStringExtra("DocId"))
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(ModifyActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ModifyActivity.this, "Error Deleting Post", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                                                    startActivity(changeScreen);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Uh-oh, an error occurred!
                                                }
                                            });
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    });
                }
            });

        }else{
            PublishB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Title = TitleTV.getText().toString();
                    Content = ContentTV.getText().toString();
                    Date = df.format(c);
                    UserEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DocumentReference userDocRef = db.collection("users").document(UserID );
                    userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserName = documentSnapshot.getString("Name");
                        }
                    });

                    if(Title.equals("")){
                        Toast.makeText(ModifyActivity.this,
                                "Please enter the Title post", Toast.LENGTH_SHORT).show();
                    }else if(Title.length()>70){
                        Toast.makeText(ModifyActivity.this,
                                "Title is too long", Toast.LENGTH_SHORT).show();
                    }else if(imageSelectIndicator == 0){
                        Toast.makeText(ModifyActivity.this,
                                "please choose a picture", Toast.LENGTH_SHORT).show();
                    }else if(Title.length()<5){
                        Toast.makeText(ModifyActivity.this,
                                "Title is too short", Toast.LENGTH_SHORT).show();
                    }else if(Content.equals("")){
                        Toast.makeText(ModifyActivity.this,
                                "Please write the content", Toast.LENGTH_SHORT).show();
                    }else if(radiobuttonOption.getCheckedRadioButtonId() == -1){
                        Toast.makeText(ModifyActivity.this,
                                "Please choose the type of the post", Toast.LENGTH_SHORT).show();
                    }else{
                        new AlertDialog.Builder(ModifyActivity.this)
                                .setTitle("Publishing Confirmation")
                                .setMessage("Are you sure going to publish this post? You can edit it later in modify option")
                                .setIcon(R.drawable.ic_uil_file_upload_alt)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(ModifyActivity.this, "Please Wait ... it may took a while", Toast.LENGTH_SHORT).show();
                                        Type = TypeDetermination(newsOptionRB, universityOptionRB, registrationOptionRB);
                                        final DocumentReference docRef = db.collection("posts").document(""+Title.substring(0,2)+c+UserID.substring(0,3));
                                        Map<String, Object> post = new HashMap<>();
                                        post.put("Title", Title);
                                        post.put("Picture","");
                                        post.put("Contents",Content);
                                        post.put("Type",Type);
                                        post.put("Date",Date);
                                        post.put("Email",UserEmail);
                                        post.put("Writer",UserName);
                                        docRef.set(post)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        uploadImage(docRef,""+Title.substring(0,2)+c+UserID.substring(0,3));
                                                        Toast.makeText(ModifyActivity.this, "Published", Toast.LENGTH_SHORT).show();
                                                        Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                                                        startActivity(changeScreen);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ModifyActivity.this, "Fail to Publish", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }})
                                .setNegativeButton(android.R.string.no, null).show();

                    }
                }
            });

        }

        ChoosePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        DiscardB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title = TitleTV.getText().toString();
                Content = ContentTV.getText().toString();
                if(!Title.equals("") || !Content.equals("")){
                    new AlertDialog.Builder(ModifyActivity.this)
                            .setTitle("Are you sure?")
                            .setMessage("All of the changes will be discarded")
                            .setIcon(R.drawable.ic_mdi_file_cancel_outline)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(ModifyActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                                    Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                                    startActivity(changeScreen);
                                    finish();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                    startActivity(changeScreen);
                    finish();
                }

            }
        });



    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                int imageHeight = selectedImage.getHeight();
                int imageWidth = selectedImage.getWidth();
                trace("IMAGE_SIZE","H: "+imageHeight+"  W: "+imageWidth);
                if (imageHeight>1280 && imageWidth>1280){
                    Toast.makeText(ModifyActivity.this, "Please pick smaller size image, Max size is (H: 1280 X W: 1280), max recommended (H: 720 X W: 1280)",Toast.LENGTH_LONG).show();
                }else{
                    ThumbnailIV.setImageBitmap(selectedImage);
                    imageSelectIndicator++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ModifyActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ModifyActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    public void trace(String tag, String message){
        Log.d(tag , message);
    }
    private void uploadImage(final DocumentReference docref,String docID) {
        if (imageUri != null) {
            // Defining the child of storageReference
            final StorageReference ref = mStorageRef.child("posts/thumbnail/" + docID);
            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_url = uri.toString();
                                    Map<String, Object> thumbnail = new HashMap<>();
                                    thumbnail.put("Picture", download_url);
                                    docref.update(thumbnail)
                                            .addOnSuccessListener(new OnSuccessListener <Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ModifyActivity.this, "Image Uploaded!!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ModifyActivity.this, "Failed to Upload Image, Change it By modifying it",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ModifyActivity.this, "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


    public int TypeDetermination(RadioButton OptionA, RadioButton OptionB, RadioButton OptionC){
        int a = 1 ;
        if(!OptionA.isChecked()){
            if(OptionB.isChecked()){
                a=2;
                return a;
            }else if(OptionC.isChecked()){
                a=3;
                return a;
            }
        }
        return a;

    }
    public void onBackPressed() {
        Title = TitleTV.getText().toString();
        Content = ContentTV.getText().toString();
        if(!Title.equals("") || !Content.equals("")){
            new AlertDialog.Builder(ModifyActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("All of the changes will be discarded")
                    .setIcon(R.drawable.ic_mdi_file_cancel_outline)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(ModifyActivity.this, "Discarded", Toast.LENGTH_SHORT).show();
                            Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
                            startActivity(changeScreen);
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }else{
            Intent changeScreen =new Intent(ModifyActivity.this, AdministratorActivity.class);
            startActivity(changeScreen);
            finish();
        }
    }



}
