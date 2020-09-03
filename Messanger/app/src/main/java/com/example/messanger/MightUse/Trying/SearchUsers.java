package com.example.messanger.MightUse.Trying;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messanger.UserObject;
import com.example.messanger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchUsers extends AppCompatActivity {
    private ListView listView;
    private TextView label;
    private RecyclerView List;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        listView = findViewById(R.id.listView);
        label = findViewById(R.id.Email);
        label = findViewById(R.id.label);

        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<UserObject> userList = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                for(DataSnapshot snapshot1: snapshot.getChildren()) {
                    if (!snapshot.getKey().equals(firebaseUser.getUid())){
                        list.add(snapshot1.getValue().toString());
                        UserObject userObject = new UserObject(snapshot1.child("email").getValue().toString(), snapshot1.getKey(),snapshot1.child("Name").getValue().toString());
                        userList.add(userObject);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.label)).getText().toString();
                Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();
            }
        });


    }
}

