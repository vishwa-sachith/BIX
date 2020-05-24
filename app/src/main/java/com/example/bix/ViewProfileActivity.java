package com.example.bix;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bix.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    boolean checkwebgood;
    boolean checkfbgood;

    CircleImageView image_profile;
    TextView username1;
    TextView facebook1;
    TextView mnumber1;
    TextView webURL1;
    TextView location1;
    TextView email1;

    static String currentLocation = "";

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        image_profile = findViewById(R.id.profile_image);
        username1 = findViewById(R.id.username);
        facebook1 = findViewById(R.id.fb_un);
        mnumber1 = findViewById(R.id.number);
        webURL1 = findViewById(R.id.weburl);
        location1 = findViewById(R.id.locationtxtview);
        email1 = findViewById(R.id.email);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(getIntent().getStringExtra("user"));

        if (!currentLocation.equals("")) {
            location1.setText(currentLocation);
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username1.setText(user.getUsername());
                email1.setText(user.getEmail());

                if (!user.getFacebook().equals("not provided")) {
                    facebook1.setText(user.getFacebook());
                }
                if (!user.getMnumber().equals("not provided")) {
                    mnumber1.setText(user.getMnumber());
                }
                if (!user.getWebURL().equals("not provided")) {
                    webURL1.setText(user.getWebURL());
                }
                if (!user.getLocation().equals("not provided")) {
                    location1.setText(user.getLocation());
                }
                if (user.getImageURL().equals("default")) {
                    image_profile.setImageResource(R.drawable.user);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });


    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public void phone(View view) {
        if (!TextUtils.isEmpty(mnumber1.getText())) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: +94"+mnumber1.getText().toString()));
            startActivity(intent);
        }
    }

    public void message(View view) {
        if (!TextUtils.isEmpty(mnumber1.getText())) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", mnumber1.getText().toString());
            startActivity(smsIntent);
        }
    }

    public void visitweb(View view) {
        if (!TextUtils.isEmpty(webURL1.getText())) {
            String url = webURL1.getText().toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    public void visitfb(View view) {
        if (!TextUtils.isEmpty(facebook1.getText())) {
            String uri = "fb://facewebmodal/f?href=https://www.facebook.com/" + facebook1.getText();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }

    public void viewlocation(View view) {
        if (!TextUtils.isEmpty(location1.getText())) {
            Uri gmmIntentUri = Uri.parse("geo:"+location1.getText());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

}
