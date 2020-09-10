package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends EncryptionChoiceforChatActivity {
    private RecyclerView messageList;
    private RecyclerView.Adapter adapter;
    private List<MessageObject> messageObjectList;
    EditText message;
    Button send;
    public String ChatID, encryptionChoice, shiftValue, theMessage, key, notificationKey;
    ArrayList<ChatListObject> chatListObjects;
    ArrayList<String> chatIDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_main);

        //make toolbar and add a back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ChatListAdapter.ChatName);
        /*toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, ChatListActivity.class));
            }
        });*/

        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popup = layoutInflater.inflate(R.layout.notification_status_popup, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int hieght = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popup, width, hieght, focusable);
                CheckBox status = popup.findViewById(R.id.status);
                popupWindow.showAtLocation(popup, Gravity.CENTER, 0,0);
            }
        });*/

        chatListObjects = super.getChatList();
        chatIDs = new ArrayList<>();
        shiftValue = "0";
        ChatID = getIntent().getExtras().getString("chatID");
        encryptionChoice = "";
        key = "";
        message = findViewById(R.id.message);
        send = findViewById(R.id.Send);
        messageObjectList = new ArrayList<>();
        messageList = findViewById(R.id.messageList);
        messageList.setNestedScrollingEnabled(true);
        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        getChatMessages();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        messageList.setAdapter(adapter);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.child("users").getChildren()){
                    boolean equal = false;
                    if(chatIDs.size()!=0){
                        for(String IDs: chatIDs){//so the app doesnt keep on adding the same userIDs every time a message is sent.
                            if((snapshot1.getKey().equals(IDs)) || snapshot1.getKey().equals(FirebaseAuth.getInstance().getUid())){
                                equal = true;
                            }
                        }
                    }
                    if(equal == false)
                        chatIDs.add(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getChatMessages() {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID).child("Messages");
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(snapshot.exists()){
                                String text = "", creator = "";
                                if(snapshot.child("text").getValue()!= null){
                                    text = snapshot.child("text").getValue().toString();
                                }
                                if(snapshot.child("creator").getValue() != null){
                                    creator = snapshot.child("creator").getValue().toString();
                                }

                                MessageObject messageObject = new MessageObject(text, creator, snapshot.getKey(), ChatID);
                                if(!messageObject.getMessage().equals(""))
                                    messageObjectList.add(messageObject);
                                messageList.getLayoutManager().scrollToPosition(messageObjectList.size()-1);//helps scroll to the last message in the chat
                                adapter = new MessageAdapter(messageObjectList);
                                messageList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

    }

    private void sendMessage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    getInfo(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(!message.getText().toString().isEmpty()){
            if (encryptionChoice.equals("Shifted Encryption")){
               theMessage = shifted();
            }
            else if(encryptionChoice.equals("Simple Encryption")){
                theMessage = simple();
            }
            else if(encryptionChoice.equals("Ceaser Encryption")){
                theMessage = ceaser();
            }
            else if(encryptionChoice.equals("none")){
                theMessage = none();
            }
            FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID).child("Last Message").setValue(theMessage);
            DatabaseReference newMessageDb = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID).child("Messages").push();
            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", theMessage);
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMessageDb.updateChildren(newMessageMap);
            for(String ChatID: chatIDs){
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("user").child(ChatID);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            notificationKey = Objects.requireNonNull(snapshot.child("notificationKey").getValue()).toString();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                new SendNotifications(theMessage, "New Message", notificationKey);
            }
        }
        message.setText(null);
    }

    private void getInfo(@NonNull DataSnapshot snapshot) {

        if(snapshot.exists()){
            if(snapshot.child("Encryption Type").getValue() != null) {
                encryptionChoice = snapshot.child("Encryption Type").getValue().toString();
                if (encryptionChoice.equals("Simple Encryption")) {
                    key = snapshot.child("key").getValue().toString();
                }
            }
            if(!encryptionChoice.equals("Simple Encryption")) {
                if (snapshot.child("shift").getValue() != null)
                    shiftValue = snapshot.child("shift").getValue().toString();
            }
        }

    }

    private String none() {
        return theMessage = message.getText().toString();
    }

    private String ceaser() {
            //encrypting text with ceaser cipher
        int shift = Integer.parseInt(shiftValue);//shift by this value
        if(shift > 26){//to ensure that the user does not enter a very large number, even with modulo on entering very large numbers, the app can not handle it and than crashes, even after this if numbers like 449393847848484 are entered the app crashes and gives the Number Format Exception
            shift = shift%26;
        }
        String phrase = message.getText().toString();
        int start = 1;
        String[] phraseArray = new String[phrase.length()];//array for the phrase;
        for (int i = 0; i < phraseArray.length; i++) {//for loop to put the phrase into an array;
            phraseArray[i] = phrase.substring(i, i + 1);
        }
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};//alphabet array;
        //starting the process to actually create the encoded;
        for (int i = 0; i < phraseArray.length; i++) {
            for (int j = 0; j < alphabet.length; j++) {
                if (phraseArray[i].equals(alphabet[j])) {
                    int k = j + shift;
                    if (k < alphabet.length) {
                        phraseArray[i] = alphabet[k];
                    } else if (k > alphabet.length) {
                        phraseArray[i] = alphabet[k - alphabet.length];
                    }
                    break;
                }
            }
        }
        //printing out new phrase;
        for (int i = 0; i < phraseArray.length; i++) {
            System.out.println(phraseArray[i]);
        }
        String phrase2 = "";
        for (int i = 0; i < phraseArray.length; i++) {
            phrase2 = phrase2 + phraseArray[i];
        }
        return theMessage = phrase2;
    }

    private String shifted() {
        int shift = Integer.parseInt(shiftValue);
        if(shift>26){//to ensure that the app does not crash when the user enters extremely large numbers
            shift = shift % 26;
        }
        String phrase = message.getText().toString();
        int x = phrase.length();//finding the length of the String entered by the user.
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        //turning the phrase into an array;
        final String[] phraseArray = new String[phrase.length()];
        //for loop to enter all characters of the phrase into the array;
        for (int i = 0; i < phrase.length(); i++) {
            phraseArray[i] = phrase.substring(i, i + 1);
            System.out.println(phraseArray[i]);
        }
        //phrase inserted into the array;
        //implementing shift;
        //to do this we will need to store the last letters in an array;
        String[] lastalphabets = new String[shift];//here we create an array to store the last elements into;
        int start = 0;//to move along the lastalphabets array;
        for (int j = (26 - shift); j < alphabet.length; j++) {
            lastalphabets[start] = alphabet[j];
            start = start + 1;//this incrementing will help us move along the lastalphabet array;
        }
        String[] newalphabet = new String[26];//array to store new and shifted alphabets;
        for (int i = 0; i < lastalphabets.length; i++) {
            newalphabet[i] = lastalphabets[i];//putting the last alphabets into the newalphabet array;
        }
        //put the rest of the alphabet shifted into the new alphabet;
        //this time the for loop will go backwards;
        start = alphabet.length - shift - 1;
        System.out.println(alphabet.length);
        for (int i = 25; i >= shift; i--) {//putting the rest of the alphabets into the new array;
            newalphabet[i] = alphabet[start];
            start--;
        }
        for (int i = 0; i <= newalphabet.length - 1; i++) {
            System.out.print(newalphabet[i]);
        }
        System.out.println();
        for (int i = 0; i < alphabet.length; i++) {
            System.out.print(alphabet[i]);
        }
        //now creating for loops to make the new word;
        for (int i = 0; i < phraseArray.length; i++) {//this for loop goes though the phase array;
            for (int j = 0; j < alphabet.length; j++) {//this for loop goes through thealphabet array;
                if (phraseArray[i].equals(alphabet[j])) {//this if statement checks if the two letter match;
                    phraseArray[i] = newalphabet[j];//this replaces the letter with the new letter in that plavc
                    break;
                }
            }
        }
        System.out.println();
        //time to print the new and edited word;
        String newphrase = "";
        for (int i = 0; i < phraseArray.length; i++) {
            newphrase = newphrase + phraseArray[i];
        }
        return theMessage = newphrase;
    }
    private String simple(){
        String keyword = key;
        String themessage = message.getText().toString();
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String[] messageArray = new String[themessage.length()];//array for the message
        String[] keywordArray = new String[keyword.length()];//array for the keyword
        for (int i = 0; i < keyword.length(); i++) {
            keywordArray[i] = keyword.substring(i, i + 1);
        }
        for (int i = 0; i < themessage.length(); i++) {
            messageArray[i] = themessage.substring(i, i + 1);
        }
        int k = 0;
        for (int i = 0; i < messageArray.length; i++) {
            if (k >= keyword.length()) {//prevents k from exceeding the length of the keyword(necessary)
                k = 0;
            }
            for (int j = 0; j < alphabet.length; j++) {//encryption with the simple is just the opposite of decryption with it
                if (alphabet[j].equals(keywordArray[k])) {//compares letters int the keyword array and the alphabet to judge when they are the same
                    int shifted = j;//sets number of shifts we have to do to the place in the alphabet where the letter in the keyword is the same as the letter in the alphabet
                    for (int h = 0; h < alphabet.length; h++) {//in this forloop we will search for the message letter at i in the alphabet.
                        if (alphabet[h].equals(messageArray[i])) {//this if statement helps us judge if we found it or not
                            if ((h + shifted) > alphabet.length) {//h+shifted is the placement of the new letter
                                messageArray[i] = alphabet[(shifted + h) - alphabet.length];//we need to judge this so we do not have out of bounds errors
                                k++;//increments k as to move to the next letter in the keyword
                            } else if ((h + shifted) < alphabet.length) {
                                messageArray[i] = alphabet[h + shifted];
                                k++;
                            }
                            break;//important to break away
                        }
                    }
                    break;
                }
            }
        }
        String newphrase = "";
        for (int l = 0; l < messageArray.length; l++) {
            System.out.print(messageArray[l]);
        }
        for (int i = 0; i < messageArray.length; i++) {
            newphrase = newphrase + messageArray[i];
        }
        System.out.println(newphrase);
        System.out.println(messageArray[0]);
        return newphrase;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MessageActivity.this, ChatListActivity.class));
    }
}