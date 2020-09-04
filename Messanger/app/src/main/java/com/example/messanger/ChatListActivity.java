package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatListObject> chatList;
    private Button grpChat;
    ChatListObject chatListObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FirebaseApp.initializeApp(this);
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

    private void getUserChatList(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot snapshot1: snapshot.getChildren()){

                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("chat").child(snapshot1.getKey());
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name;
                                if(snapshot.child("Group Name").exists()){
                                    name = snapshot.child("Group Name").getValue().toString();
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
                                else if(!snapshot.child("Group Name").exists()){
                                    DatabaseReference databaseReference2  = databaseReference1.child("users");
                                    databaseReference2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snapshot2: snapshot.getChildren()){
                                                if(!snapshot2.getValue().toString().equals(FirebaseAuth.getInstance().getUid())){
                                                    String Uid = snapshot2.getValue().toString();
                                                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("user").child(Uid);
                                                    databaseReference3.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String name;
                                                            if(snapshot.child("Name").exists()){
                                                                name = snapshot.child("Name").getValue().toString();
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //chatListObject = new ChatListObject(snapshot1.getKey());
                        /*boolean chatExists = false;
                        for(ChatListObject ChatIterator: chatList){
                            if(ChatIterator.getChatID().equals(chatListObject.getChatID())){
                                chatExists = true;
                            }
                        }
                        if(chatExists == false){
                            chatList.add(chatListObject);
                            adapter.notifyDataSetChanged();
                        }*/

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

}
