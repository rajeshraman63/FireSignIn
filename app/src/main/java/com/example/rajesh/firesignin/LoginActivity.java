package com.example.rajesh.firesignin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.auth.ui.phone.CountryListSpinner;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button btn_signout;
    private Button btn_addImage;
    private Button btn_camera;
    private Button btn_showImage;

    private ImageView iv_image;

    private ListView mListView;

    private Firebase mRoot;

    private StorageReference mStorage;

    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int FINISH_REQUEST_CODE = 3;

    private ProgressDialog mProgressDialog;

    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_signout = (Button)findViewById(R.id.btn_signout);
        btn_addImage = (Button)findViewById(R.id.btn_addImage);
        btn_camera = (Button)findViewById(R.id.btn_camera);
        btn_showImage = (Button)findViewById(R.id.btn_showImage);

        iv_image = (ImageView)findViewById(R.id.iv_image);

        mProgressDialog = new ProgressDialog(LoginActivity.this);

        mStorage = FirebaseStorage.getInstance().getReference();  // reference to the Firebase Storage

        mListView = (ListView)findViewById(R.id.list_items);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,android.R.layout.simple_list_item_1,arrayList);
        mListView.setAdapter(adapter);

        mRoot = new Firebase("https://firesignin-9a178.firebaseio.com/");

        mRoot.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue().toString();
                arrayList.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
            }
        });


        btn_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });


        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                //finishActivity(FINISH_REQUEST_CODE);
                finish();
            }
        });


       btn_showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
                Picasso.with(LoginActivity.this).load(imageUri).into(iv_image);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode == RESULT_OK){

            mProgressDialog.setMessage("Uploading..");
            mProgressDialog.show();

            Uri uri = data.getData();

            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(getApplicationContext(),"Upload Successfull",Toast.LENGTH_SHORT).show();

                   mProgressDialog.dismiss();
                }
            });
        }

        else if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK){

            mProgressDialog.setMessage("Uploading..");
            mProgressDialog.show();

            Uri uri = data.getData();



            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Picasso.with(LoginActivity.this).load(downloadUri).centerCrop().into(iv_image);

                    Toast.makeText(getApplicationContext(),"Upload Successfull",Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
                }
            });

        }

    }
}
