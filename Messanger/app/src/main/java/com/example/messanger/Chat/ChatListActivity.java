package com.example.messanger.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.Authentication.LoginActivity;
import com.example.messanger.ChatCreation.CreateChat;
import com.example.messanger.R;
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
    public static boolean logedout = false;
    ChatListObject chatListObject;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatListObject> chatList;
    private Button newChat, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is the first activity that is shown after the registration or login.
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this).init();

        setContentView(R.layout.list_of_chats);
        FirebaseApp.initializeApp(this);

        //setting notification key for the user.
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        newChat = findViewById(R.id.newChat);
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatListActivity.this, CreateChat.class));
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

        recyclerView = findViewById(R.id.ListofChats);
        chatList = new ArrayList<>();
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatListAdapter(chatList);
        recyclerView.setAdapter(adapter);

        getUserChatList();

    }

    //setting status when the app is abruptly exited or entered.
    @Override
    protected void onResume() {
        super.onResume();
        if (!logedout)
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (!logedout)
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Status").setValue("Offline");
    }

    private void getUserChatList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())).child("chat");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (final DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final String key = snapshot1.getKey();
                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("chat").child(key);

                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name;
                                String chat = snapshot.getKey();
                                if (snapshot.child("Group Name").exists()) {
                                    name = snapshot.child("Group Name").getValue().toString();
                                    if (name.equals("NoName")) {//if the name is NoName, that means that this is a personal chat.

                                        //getting the users in the chat
                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users");
                                        databaseReference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                                    final String Key = snapshot2.getKey();
                                                    if (!(Key.equals(FirebaseAuth.getInstance().getUid()))) {

                                                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("users").child(Key);
                                                        databaseReference3.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                //taking the key that is not of the current users and going into the user to get the name of the user.
                                                                DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference().child("user").child(Key).child("Name");
                                                                databaseReference4.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        String name = snapshot.getValue(String.class);//getting the name from the database.

                                                                        chatListObject = new ChatListObject(snapshot1.getKey(), name);//creating chat object with the name of the second user.

                                                                        boolean chatExists = false;
                                                                        //the object will only be added if the chat ID is not already present. This prevents duplicates.
                                                                        for (ChatListObject ChatIterator : chatList) {
                                                                            if (ChatIterator.getChatID().equals(chatListObject.getChatID())) {
                                                                                chatExists = true;
                                                                            }
                                                                        }
                                                                        if (chatExists == false) {
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
                                    } else if (!(name.equals("NoName"))) {//if name is not equal to NoName, that means that the chat is a group of 3 or more people, they will already have a predefined name, and so we do not need to get the user info before getting the name.
                                        chatListObject = new ChatListObject(snapshot1.getKey(), name);
                                        boolean chatExists = false;
                                        for (ChatListObject ChatIterator : chatList) {
                                            if (ChatIterator.getChatID().equals(chatListObject.getChatID())) {
                                                chatExists = true;
                                            }
                                        }
                                        if (chatExists == false) {
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

    public ArrayList<ChatListObject> getChatList() {
        return chatList;
    }

    @Override
    public void onBackPressed() {//so that we dont go back to the previous activity when back is clicked

    }

}
