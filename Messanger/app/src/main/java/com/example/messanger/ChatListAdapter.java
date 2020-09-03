package com.example.messanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.BundleCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
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
    public ChatListAdapter(ArrayList<ChatListObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_info, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//this is to stop the Recycler view from making each contact take up the whole screen.
        layoutView.setLayoutParams(lp);
        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, final int position) {
        holder.chat.setText(chatList.get(position).getChatID());
        ChatID = chatList.get(position).getChatID();
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(v.getContext(), MessageActivity.class);
                final Intent intent1 = new Intent(v.getContext(), EncryptionChoiceforChatActivity.class);
                final Bundle bundle = new Bundle();
                final Bundle bundle1 = new Bundle();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.child("Enter Count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
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

    }

    @Override
    public int getItemCount() {
        if (chatList.size() == 0) {
            return -1;
        } else {
            return chatList.size();
        }
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        public TextView chat;
        public LinearLayout layout;
        public ChatListViewHolder(View view){
            super(view);
            chat = view.findViewById(R.id.chat);
            layout = view.findViewById(R.id.Layout);
        }

    }
}
