package com.example.messanger.MightUse.Trying;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.UserObject;
import com.example.messanger.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<UserObject> mUser;

    public UserAdapter(Context context, List<UserObject> mUser) {
        this.context = context;
        this.mUser = mUser;
    }


    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserObject userObject = mUser.get(position);
        holder.email.setText(userObject.getEmail());




    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView email;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.Email);
        }
    }
}

