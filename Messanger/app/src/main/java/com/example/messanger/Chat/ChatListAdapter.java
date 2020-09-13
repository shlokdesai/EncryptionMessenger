package com.example.messanger.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.Messages.MessageActivity;
import com.example.messanger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//here we acc manage the list, display it and bring everything together.
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    int enterCount;
    ArrayList<ChatListObject> chatList;
    public String ChatID;
    public static String ChatName;
    public ChatListAdapter(ArrayList<ChatListObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //setting the layout file that we will use for the adapter.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//this is to stop the Recycler view from making each contact take up the whole screen.
        layoutView.setLayoutParams(lp);
        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, final int position) {
        //in this method we will set the chatName, the status (online/offline) and also the lastMessage.
        holder.chat.setText(chatList.get(position).getName());//setting chat name
        ChatName = chatList.get(position).getName();
        ChatID = chatList.get(position).getChatID();

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID).child("users");
        //getting status
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int users = 0;
                ArrayList<String> UserID = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        users += 1;
                        UserID.add(dataSnapshot.getKey());
                    }
                }
                if(users == 1){
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("user").child(UserID.get(0)).child("Status");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue().equals("Online")){
                                holder.status.setText(snapshot.getValue().toString());
                            }
                            else if(snapshot.getValue().equals("Offline")){
                                holder.status.setText(snapshot.getValue().toString());
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
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(v.getContext(), MessageActivity.class);
                final Intent intent1 = new Intent(v.getContext(), ChooseEncryption.class);
                final Bundle bundle = new Bundle();
                final Bundle bundle1 = new Bundle();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.child("Enter Count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //enter count decides if the user will set the encryption type for the chat
                            enterCount = Integer.parseInt(snapshot.getValue().toString());
                            enterCount += 1;
                            if(enterCount>1){
                                bundle.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatID());
                                intent.putExtras(bundle);
                                v.getContext().startActivity(intent);
                            }
                        }

                        else if(!snapshot.exists()){
                            Map<String, Object> addEnterCount = new HashMap<>();
                            addEnterCount.put("Enter Count", "1");
                            databaseReference.updateChildren(addEnterCount);
                            bundle1.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatID());
                            intent1.putExtras(bundle1);
                            v.getContext().startActivity(intent1);
                        }

                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
        //setting the lastMessage
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Last Message").exists()){
                    String lastMessage = snapshot.child("Last Message").getValue().toString();
                    holder.lastMesssage.setText(lastMessage);
                }
                else if(snapshot.child("Last Message").exists()){
                    String lastMessage = "No Message";
                    holder.lastMesssage.setText(lastMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (chatList.size() == 0) {
            return -1;
        } else {
            return chatList.size();
        }
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {//this class sets the layout file that we will use with the adapter
        public TextView chat, lastMesssage, status;
        public LinearLayout layout;
        public ChatListViewHolder(View view){
            super(view);
            chat = view.findViewById(R.id.chat);
            layout = view.findViewById(R.id.Layout);
            lastMesssage = view.findViewById(R.id.lastMessage);
            status = view.findViewById(R.id.Status);
        }

    }
}
