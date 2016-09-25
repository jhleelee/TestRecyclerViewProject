package com.jackleeentertainment.testrecyclerviewproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static String TAG = "MainActivity";
     static ArrayList<String> arl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    protected void onResume() {
        super.onPostResume();
        initFbaseDatabase();
        initFbaseDatabaseRef();
        setContentView(R.layout.activity_main);
        getUserSpecificArrayListOfIdsOfPostsFromFirebase("user1");
    }

    public static void initFbaseDatabase() {
        App.firebaseDatabase = FirebaseDatabase.getInstance();
        App.firebaseDatabase.setPersistenceEnabled(true);
        Log.d(TAG, "firebaseDatabase.getApp().getMname() :"+App.firebaseDatabase.getApp().getName());
    }

    public static void initFbaseDatabaseRef() {
        Log.d(TAG, "initFbaseDatabaseRef()");
        App.fbaseDbRef = App.firebaseDatabase.getReference();
    }




    public static void getUserSpecificArrayListOfIdsOfPostsFromFirebase(String userId){

       arl = new ArrayList<>();

        App.fbaseDbRef
                .child("user_data")
                .child(userId) // which is, "user1"
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Iterable<DataSnapshot> i = dataSnapshot.getChildren();

                                for (DataSnapshot d : i){
                                    String key = dataSnapshot.getKey(); //user1
                                    HashMap<String, Object> value =(HashMap<String, Object>)
                                            dataSnapshot.getValue();  //postId,ts
                                    for ( String keyOfMap : value.keySet() ) {
                                        arl.add(keyOfMap);
                                        Log.d("IdsOfPosts", key+ " , "+keyOfMap);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
    }



    public static void getSinglePostFromFirebase(String postId, final TextView textView){
        App.fbaseDbRef
                .child("post")
                .child(postId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getKey();
                                String value = dataSnapshot.getValue(String.class);
                                textView.setText(key + " , "+value);
                                Log.d(TAG, key + " , "+value);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
    }

    public static void loadPostImageFromFirebase(String filename, final ImageView imageView){
        final long TEN_MEGABYTE = 10 * 1024 * 1024;
        App.fbaseStorageRef
                .child("img")
                .child(filename)
                .getBytes(TEN_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }
                });

    }
}
