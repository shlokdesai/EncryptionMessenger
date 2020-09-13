package com.example.messanger.ChatCreation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.R;

import java.util.List;

public class CreateChatAdapter extends  RecyclerView.Adapter<CreateChatAdapter.ViewHolder>{
    Context context;
    List<UserObject> userObjectList;
    public CreateChatAdapter(List<UserObject> userObjects){
        this.userObjectList = userObjects;
    }

    @NonNull
    @Override
    public CreateChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);// this is so that the recycler view does not make one contact take up the whole screen.
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CreateChatAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final UserObject userObject = userObjectList.get(position);
        holder.Name.setText(userObject.getName());
        String email = userObject.getEmail();
        holder.Email.setText(email);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userObjectList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userObjectList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView Name, Email;
        private LinearLayout Layout;
        private CheckBox checkBox;
        public ViewHolder(View itemView){
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Layout = itemView.findViewById(R.id.Layout);
            checkBox = itemView.findViewById(R.id.checkBox);
            Email = itemView.findViewById(R.id.Email);
        }
    }
}
