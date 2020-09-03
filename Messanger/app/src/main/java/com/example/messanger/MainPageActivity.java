package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainPageActivity extends AppCompatActivity {
    private List<UserObject> userObjects;
    private Button chatCreate;
    private RecyclerView rv;
    private RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();
        setContentView(R.layout.activity_main_page);
        userObjects = new ArrayList<>();
        chatCreate = findViewById(R.id.createChat);
        rv = findViewById(R.id.List);
        rv.setNestedScrollingEnabled(true);
        rv.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter( userObjects);
        rv.setAdapter(adapter);
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        chatCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateChat();
            }
        });

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        if(!(firebaseUser.getUid().equals(snapshot1.getKey()))){
                            String email = snapshot1.child("email").getValue().toString();
                            String key = snapshot1.getKey();
                            UserObject userObject = new UserObject(email, key/*, snapshot1.child("Name").getValue().toString()*/);
                            userObjects.add(userObject);
                            adapter  = new RecyclerViewAdapter(userObjects);
                        }
                    }
                    //rv.setHasFixedSize(true);
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void CreateChat() {
        int numberSelected = 0;
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();// push method creates a key
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(key);
        for(UserObject object: userObjects){
            if(object.getSelected()){
                FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("chat").child(key).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("user").child(object.getUid()).child("chat").child(key).setValue(true);
                numberSelected++;
            }
        }
        FirebaseDatabase.getInstance().getReference().child("chat").child(key).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users").child(FirebaseAuth.getInstance().getUid());


        for (UserObject object: userObjects){
            if (object.getSelected()){
                FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users").child(object.getUid()).setValue(true);
            }
        }
        FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users").child(FirebaseAuth.getInstance().getUid()).setValue(true);
        startActivity(new Intent(MainPageActivity.this, ChatListActivity.class) ) ;
    }
}
