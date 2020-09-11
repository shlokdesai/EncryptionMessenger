package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Objects;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatListObject> chatList;
    private Button newChat, logout;
    ChatListObject chatListObject;
    boolean logedout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FirebaseApp.initializeApp(this);
        recyclerView = findViewById(R.id.ListofChats);
        newChat = findViewById(R.id.newChat);
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatListActivity.this, MainPageActivity.class));
            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneSignal.setSubscription(false);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Offline");
                logedout = true;
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);// this is basically the start activity that i have used everywhere else in the app.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);// start activity method is called.
                finish();
                return;
            }
        });
        chatList = new ArrayList<>();
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatListAdapter(chatList);
        recyclerView.setAdapter(adapter);

        getUserChatList();

    }

    @Override
    protected  void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Online");
    }

    @Override
    protected  void onPause(){
        super.onPause();
        if(!logedout)
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Offline");
    }

    private void getUserChatList(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())).child("chat");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot snapshot1: snapshot.getChildren()){
                        final String key = snapshot1.getKey();
                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("chat").child(key);

                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name;
                                String chat = snapshot.getKey();
                                if(snapshot.child("Group Name").exists()){
                                    name = snapshot.child("Group Name").getValue().toString();
                                    if(name.equals("NoName")){
                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users");

                                        databaseReference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot snapshot2: snapshot.getChildren()){
                                                    final String Key = snapshot2.getKey();
                                                    if(!(Key.equals(FirebaseAuth.getInstance().getUid()))){
                                                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users").child(Key);

                                                        databaseReference3.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference().child("user").child(Key).child("Name");

                                                                databaseReference4.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        String name = snapshot.getValue(String.class);
                                                                        chatListObject = new ChatListObject(snapshot1.getKey(), name);
                                                                        boolean chatExists = false;
                                                                        for(ChatListObject ChatIterator: chatList){
                                                                            if(ChatIterator.getChatID().equals(chatListObject.getChatID())){
                                                                                chatExists = true;
                                                                            }
                                                                        }
                                                                        if(chatExists == false){
                                                                            chatList.add(chatListObject);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    else if (!(name.equals("NoName"))){
                                        chatListObject = new ChatListObject(snapshot1.getKey(), name);
                                        boolean chatExists = false;
                                        for(ChatListObject ChatIterator: chatList){
                                            if(ChatIterator.getChatID().equals(chatListObject.getChatID())){
                                                chatExists = true;
                                            }
                                        }
                                        if(chatExists == false){
                                            chatList.add(chatListObject);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public ArrayList<ChatListObject> getChatList(){
        return chatList;
    }

    @Override
    public void onBackPressed() {//so that we dont go back to the previous activity when back is clicked

    }

}
