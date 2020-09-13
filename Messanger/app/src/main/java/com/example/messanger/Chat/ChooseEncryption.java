package com.example.messanger.Chat;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.messanger.Messages.MessageActivity;
import com.example.messanger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseEncryption extends ChatListActivity {
    TextView Text, Text2, Text3;
    Button shifted, simple,ceaser, none, enter_shiftValue, enterKey;
    EditText shift_value, key;
    String EncryptionChoice, ChatID;
    ArrayList<ChatListObject> chatListObjects;
    ChatListObject chatListObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//this is the activity where the user can set the encryption status.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_encryption);

        Text = findViewById(R.id.Text);
        Text2 = findViewById(R.id.Text2);
        Text2.setVisibility(View.INVISIBLE);
        Text3 = findViewById(R.id.Text3);
        Text3.setVisibility(View.INVISIBLE);
        shifted = findViewById(R.id.shifted);
        simple = findViewById(R.id.simple);
        ceaser = findViewById(R.id.ceaser);
        none = findViewById(R.id.none);
        enterKey = findViewById(R.id.enterKey);
        enterKey.setVisibility(View.INVISIBLE);
        shift_value = findViewById(R.id.shift_value);
        shift_value.setVisibility(View.INVISIBLE);
        key = findViewById(R.id.key);
        key.setVisibility(View.INVISIBLE);
        ChatID = getIntent().getExtras().getString("chatID");
        enter_shiftValue = findViewById(R.id.enter_shiftValue);
        enter_shiftValue.setVisibility(View.INVISIBLE);

        chatListObjects = super.getChatList();




        ceaser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_shiftValue.setVisibility(View.VISIBLE);
                Text2.setVisibility(View.VISIBLE);
                shift_value.setVisibility(View.VISIBLE);
                Text3.setVisibility(View.INVISIBLE);
                enterKey.setVisibility(View.INVISIBLE);
                key.setVisibility(View.INVISIBLE);
                EncryptionChoice = "Ceaser Encryption";
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setEncryptionChoice("Ceaser Encryption");
                    }
                }
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addCeaser = new HashMap<>();
                        addCeaser.put("Encryption Type", EncryptionChoice);
                        databaseReference.updateChildren(addCeaser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        shifted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_shiftValue.setVisibility(View.VISIBLE);
                Text2.setVisibility(View.VISIBLE);
                shift_value.setVisibility(View.VISIBLE);
                Text3.setVisibility(View.INVISIBLE);
                enterKey.setVisibility(View.INVISIBLE);
                key.setVisibility(View.INVISIBLE);
                EncryptionChoice = "Shifted Encryption";
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setEncryptionChoice("Shifted Encryption");
                    }
                }
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addShifted = new HashMap<>();
                        addShifted.put("Encryption Type", EncryptionChoice);
                        databaseReference.updateChildren(addShifted);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EncryptionChoice = "Simple Encryption";
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setEncryptionChoice("Simple Encryption");
                    }
                }
                Text3.setVisibility(View.VISIBLE);
                enterKey.setVisibility(View.VISIBLE);
                key.setVisibility(View.VISIBLE);
                enter_shiftValue.setVisibility(View.INVISIBLE);
                Text2.setVisibility(View.INVISIBLE);
                shift_value.setVisibility(View.INVISIBLE);
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addSimple = new HashMap<>();
                        addSimple.put("Encryption Type", EncryptionChoice);
                        databaseReference.updateChildren(addSimple);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_shiftValue.setVisibility(View.INVISIBLE);
                Text2.setVisibility(View.INVISIBLE);
                shift_value.setVisibility(View.INVISIBLE);
                Text3.setVisibility(View.INVISIBLE);
                enterKey.setVisibility(View.INVISIBLE);
                key.setVisibility(View.INVISIBLE);
                EncryptionChoice = "none";
                final String shifts = "0";
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setEncryptionChoice("none");
                        object.setShift(Integer.parseInt(shifts));
                    }
                }
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addnone = new HashMap<>();
                        addnone.put("Encryption Type", EncryptionChoice);
                        databaseReference.updateChildren(addnone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(ChooseEncryption.this, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", ChatID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        enter_shiftValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setShift(Integer.parseInt(shift_value.getText().toString()));
                    }
                }
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addShift = new HashMap<>();
                        addShift.put("shift", shift_value.getText().toString());
                        databaseReference.updateChildren(addShift);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(ChooseEncryption.this, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", ChatID);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        enterKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ChatListObject object: chatListObjects){
                    if(object.getChatID().equals(ChatID)){
                        object.setKey(key.getText().toString());
                    }
                }
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> addKey = new HashMap<>();
                        addKey.put("key", key.getText().toString());
                        databaseReference.updateChildren(addKey);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(ChooseEncryption.this, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", ChatID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public ArrayList<ChatListObject> getChatList(){
        return chatListObjects;
    }
}