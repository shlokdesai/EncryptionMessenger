package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {


    private Button Create, go2Login;
    private EditText Email, Name, Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Name = findViewById(R.id.Name);
        Create = findViewById(R.id.Create);
        go2Login = findViewById(R.id.Login);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String name = Name.getText().toString();
                if(email.isEmpty()){
                    Email.setError("Please fill in the required field");
                }
                if(password.isEmpty()){
                    Password.setError("Please fill in the required field");
                }
                if(name.isEmpty()){
                    Name.setError("Please fill in the required field");
                }
                else if(!email.isEmpty() && (!name.isEmpty()) && !password.isEmpty()){
                    createAccount(email, password, name);
                }
            }
        });

        go2Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }

    private void createAccount(final String email, final String password, final String name) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user!=null){
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("email", user.getEmail());
                                    mUserDB.updateChildren(userMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    LoginSuccessful(name);
                }
                else{
                    Toast.makeText(RegistrationActivity.this, " Authentication Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void LoginSuccessful(final String name){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            Toast.makeText(RegistrationActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
            final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> addName = new HashMap<>();
                    addName.put("Name", name);
                    databaseReference1.updateChildren(addName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            startActivity(new Intent(RegistrationActivity.this, ChatListActivity.class));
        }

    }

}

