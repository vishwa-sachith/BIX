package com.example.bix;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bix.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {


    ImageView profilePicView;
    final int Image_request_code = 1;
    Bitmap bitmap;
    String URL = "http://192.168.8.129:8080/web_pocketPM/uploadPfrofilePic";
    ////////////

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

        setContentView(R.layout.activity_edit_profile);

        image_profile = findViewById(R.id.profile_image);
        username1 = findViewById(R.id.username);
        facebook1 = findViewById(R.id.fb_un);
        mnumber1 = findViewById(R.id.number);
        webURL1 = findViewById(R.id.weburl);
        location1 = findViewById(R.id.locationtxtview);
        email1 = findViewById(R.id.email);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

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
                chooseImageFromGallery(v);
            }
        });

    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            final StorageReference filReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = filReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }



    public void SaveButton(View view) {
        System.out.println("TEXT ="+webURL1.getText());

        if(checkwebgood || TextUtils.isEmpty(webURL1.getText())) {

            if(checkfbgood || TextUtils.isEmpty(facebook1.getText())) {

                boolean ready = true;

                final ProgressDialog pd1 = new ProgressDialog(EditProfileActivity.this);
                pd1.setMessage("Updating");
                pd1.show();

                String username = "not provided";
                String facebook = "not provided";
                String mnumber = "not provided";
                String webURL = "not provided";
                String location = "not provided";

                if (!TextUtils.isEmpty(username1.getText().toString())) {
                    username = username1.getText().toString();
                }

                if (!TextUtils.isEmpty(facebook1.getText().toString())) {
                    facebook = facebook1.getText().toString();
                }

                if (!TextUtils.isEmpty(mnumber1.getText().toString())) {
                    mnumber = mnumber1.getText().toString();
                }

                if (!TextUtils.isEmpty(webURL1.getText().toString())) {
                    webURL = webURL1.getText().toString();
                }

                if (!TextUtils.isEmpty(location1.getText().toString())) {
                    location = location1.getText().toString();
                }


                if (ready) {

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", username);
                    map.put("facebook", facebook);
                    map.put("mnumber", mnumber);
                    map.put("webURL", webURL);
                    map.put("location", location);
                    map.put("search", username.toLowerCase());
                    reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd1.dismiss();
                        }
                    });

                    pd1.dismiss();

                }
            } else {
                Toast.makeText(getApplicationContext(), "Account Not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Web Address Not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void CheckWeb (View view) {

        checkwebgood = false;

        final String url1 = ((EditText)findViewById(R.id.weburl)).getText().toString();

        AsyncTask a = new AsyncTask() {

            @Override
            protected void onProgressUpdate(Object[] values) {
                Toast t = Toast.makeText(getApplicationContext(), "checking", Toast.LENGTH_SHORT);
                t.show();
            }

            @Override
            protected Object[] doInBackground(final Object... strings) {


                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HttpURLConnection con = null;
                        try {
                            URL url = new URL(url1);
                            con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setDoOutput(false);

                            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                checkwebgood = true;
                            } else {
                                checkwebgood = false;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                         /*   if (checkwebgood) {
                                Toast t = Toast.makeText(getApplicationContext(), "Web Address found", Toast.LENGTH_SHORT);
                                t.show();
                                System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGG");
                            } else {
                                Toast t = Toast.makeText(getApplicationContext(), "Web Address Not found", Toast.LENGTH_SHORT);
                                t.show();
                                System.out.println("KKKKKKKKKKK");
                            }
                          */

                            if (con != null) {
                                con.disconnect();
                            }
                        }

                    }
                });
                t.start();


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (checkwebgood) {
                    Toast t = Toast.makeText(getApplicationContext(), "Web Address found", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }.execute();

    }

    public void CheckFB (View view) {

        checkfbgood = false;

        final String un = ((EditText)findViewById(R.id.fb_un)).getText().toString();

        AsyncTask a = new AsyncTask() {

            @Override
            protected void onProgressUpdate(Object[] values) {
                Toast t = Toast.makeText(getApplicationContext(), "checking", Toast.LENGTH_SHORT);
                t.show();
            }

            @Override
            protected Object[] doInBackground(final Object... strings) {


                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HttpURLConnection con = null;
                        try {
                            URL url = new URL("https://facebook.com/" + un);
                            con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setDoOutput(false);

                            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                checkfbgood = true;
                                System.out.println("QWWWQWQWQWQWQWWQWQWQWQWQWWQWQWW");
                            } else {
                                checkfbgood = false;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                         /*   if (checkwebgood) {
                                Toast t = Toast.makeText(getApplicationContext(), "Web Address found", Toast.LENGTH_SHORT);
                                t.show();
                                System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGG");
                            } else {
                                Toast t = Toast.makeText(getApplicationContext(), "Web Address Not found", Toast.LENGTH_SHORT);
                                t.show();
                                System.out.println("KKKKKKKKKKK");
                            }
                          */

                            if (con != null) {
                                con.disconnect();
                            }
                        }

                    }
                });
                t.start();


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (checkfbgood) {
                    Toast t = Toast.makeText(getApplicationContext(), "Account found", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }.execute();

    }

    public void GetLocation(View view) {
        startActivity(new Intent(EditProfileActivity.this, GetCurrentLocation.class));
    }


/////

    public void chooseImageFromGallery(View view) {
        customDialog("Confirm","Choose a option");
    }

    private String bitMapToBase64Conntion(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageData = outputStream.toByteArray();
        return Base64.encodeToString(imageData,Base64.DEFAULT);
    }

    public void customDialog(String title, String message){
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this);
        // builderSingle.setIcon(R.mipmap.ic_notification);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Choose From gallery",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,Image_request_code);
                    }
                });

        builderSingle.setPositiveButton(
                "Use camara",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //  intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,Image_request_code);
                    }
                });


        builderSingle.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_request_code && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {

                Bitmap data1 = (Bitmap) data.getExtras().get("data");

                if(data1 != null){
                    if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                        imageUri = data.getData();

                        if (uploadTask != null && uploadTask.isInProgress()) {
                            Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                        }
                    }
                }else{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                    image_profile.setImageBitmap(bitmap);
                    imageUri = data.getData();
                    uploadImage();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
