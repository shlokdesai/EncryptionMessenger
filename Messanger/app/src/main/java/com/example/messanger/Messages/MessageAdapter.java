package com.example.messanger.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    List<MessageObject> messageObjectList;
    String EncryptionChoice = "", key = "", sentmessage = "", Message = "", lastMessage = "";
    int ShiftValue;
    int MSG_LEFT = 0;
    int MSG_RIGHT = 1;
    public MessageAdapter(List<MessageObject> messageObjectList) {// this is not getting called on pressing the send button
        this.messageObjectList = messageObjectList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sender_bubble, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recieved_bubble, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        String senderID = messageObjectList.get(position).getSender();
        holder.message.setText(messageObjectList.get(position).getMessage());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(senderID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.sender.setText(snapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //holder.sender.setText(messageObjectList.get(position).getSender());
        sentmessage = messageObjectList.get(position).getMessage();
        final String ChatID = messageObjectList.get(position).getChatID();
        holder.layout.setOnClickListener(new View.OnClickListener() {//problem with the listener, the method for deciphering is only called after the 3rd trial, for the same word
            //for some reason this on click listener is taking the event of the send button being clicked, not the message being clicked.
            @Override
            public void onClick(View v) {
                Message = holder.message.getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(ChatID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Encryption Type").exists()) {
                            EncryptionChoice = snapshot.child("Encryption Type").getValue().toString();
                            if (EncryptionChoice.equals("Simple Encryption"))
                                key = snapshot.child("key").getValue().toString();

                            else if (EncryptionChoice.equals("Shifted Encryption") || EncryptionChoice.equals("Ceaser Encryption"))
                                if(snapshot.child("shift").exists())
                                    ShiftValue = Integer.parseInt(snapshot.child("shift").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                switch (EncryptionChoice) {
                    case "Shifted Encryption": {
                        String messageCipherd = shiftedCipher(Message);
                        holder.message.setText(messageCipherd);
                        break;
                    }
                    case "Simple Encryption": {
                        String messageCipherd = simpleCipher(Message);
                        holder.message.setText(messageCipherd);

                        break;
                    }
                    case "Ceaser Encryption": {
                        String messageCipherd = ceaserCipher(Message);
                        holder.message.setText(messageCipherd);
                        break;
                    }
                    case "none":
                        holder.message.setText(messageObjectList.get(position).getMessage());
                        break;
                }

            }
        });
    }

    private String shiftedCipher(String Message) {

        if (ShiftValue > 26) {//if the number is greater than the length of the alphabet;
            ShiftValue = ShiftValue % 26;
        }
            String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};//alphabet array.
            String[] encodedphraseArray = new String[Message.length()];//array for the phrase.
            for (int i = 0; i < Message.length(); i++) {//for loop to put the encoded phrase into an array
                encodedphraseArray[i] = Message.substring(i, i + 1);
            }
            //now let us create the shifted alphabet;
            //storing the last  letters of the alphabet in a seperate array;
            String[] newalphabet = new String[26];// array for the new alphabet;
            String[] thelastletters = new String[ShiftValue];// array for the last alphabets that will be shifted;
            //the following two arrays will help us create the lastletter array, and the new alphabet.
            thelastletters = lstletter(ShiftValue, thelastletters, alphabet);//method call to make the last alphabet array;
            newalphabet = makenewalphabet(thelastletters, alphabet, newalphabet, ShiftValue);//method call to make the new alphabet;
            //now decoding the encoded text;
            encodedphraseArray = decode(encodedphraseArray, newalphabet, alphabet);//this method call calls the method decode, which will decode the text for us;
            String newphrase = "";//this is the decoded phrase;
            for (int i = 0; i < encodedphraseArray.length; i++) {
                newphrase = newphrase + encodedphraseArray[i];

            }
            System.out.println(newphrase);
        return newphrase;
    }

    public String[] lstletter(int a, String[] b, String[] c){//method that makes the last alphabet array;
        int start = 0;// position in the last alphabet array;
        for(int i = 26-a ; i < c.length ; i++){
            b[start] = c[i];
            start++;
        }
        return b;
    }
    public  String[] makenewalphabet(String[] a, String[] b, String[] c, int d){//method that will create the newalohabet;
        for(int i = 0 ; i < a.length ; i++){//this forloop fills in the starrt of the new alphabet with the lasst letters that we found;
            c[i] = a[i];
        }
        int start = b.length - d - 1;
        for(int j = 25 ; j >= d ; j--) {//this for loop will complete the rest of the array;
            c[j] = b[start];
            start--;
        }
        return c;//returning the final array;
    }
    public  String[] decode(String[] a, String[] b, String[] c){//decoding method;
        for(int i = 0; i < a.length ; i++){//for loop that goes throught the encoded txt;
            for(int j = 0; j < b.length ; j++){//for loop that goes through the new alphabet array;
                if(a[i].equals(b[j])){//if statement that compares if the alphabet in the encoded txt and the new alphabet match;
                    a[i] = c[j];//replaces the right letter from the alphabet in the encodedphraseArray;
                    break;//so that te loop doesn't keep on repeating after finding the letter;
                }

            }
        }
        return a;
    }


    private String simpleCipher(String Message){
        //the following code is buggy, useless and does not work as intended, and so it has been replaced with another suitable operation, delete if you wish.
        String[] keywordsArray = new String[key.length()];
        for (int i = 0; i < key.length(); i++) {
            keywordsArray[i] = key.substring(i, i + 1);
        }
        String[] messageArrayenc = new String[Message.length()];
        int k = 0;
        for (int i = 0; i < messageArrayenc.length; i++) {
            messageArrayenc[i] = Message.substring(i, i + 1);
        }
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        for (int i = 0; i < messageArrayenc.length; i++) {//goes through the message
            if (k >= key.length()) {//goes through the keyword
                k = 0;
            }
            for (int j = 0; j < alphabet.length; j++) {//these arrays work to sort letters of the phrase under letters of the keywords
                if (alphabet[j].equals(keywordsArray[k])) {//comparing the alphabet and keywords to find where the letter is
                    int shifted = j;//sets the shifts we have to make to the placement of the letter in the keyword in the alphabet array.
                    for (int h = 0; h < alphabet.length; h++) {
                        if (alphabet[h].equals(messageArrayenc[i])) {//now to find the placement of the message letter in the alphabet
                            if ((h - shifted) >= 0) {
                                messageArrayenc[i] = alphabet[h - shifted];//applies the shift
                                k++;
                            } else if ((h - shifted) < 0) {
                                messageArrayenc[i] = alphabet[26 + (h - shifted)];
                                k++;
                            }
                            break;
                        }
                    }
                    break;
                }

            }
        }
        for (int i = 0; i < messageArrayenc.length; i++) {
            System.out.print(messageArrayenc[i]);
        }
        String newphrase = "";
        for (int i = 0; i < messageArrayenc.length; i++) {
            newphrase = newphrase + messageArrayenc[i];
        }
        return  newphrase;
    }

    private String ceaserCipher(String Message) {
            if (ShiftValue > 26) {//to ensure that the app does not crash when the user enters extremely large numbers
                ShiftValue = ShiftValue%26;
            }
                String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};//the whole alphabet;
                String[] encodedArray = new String[Message.length()];
                //putting the encoded phrase into an array using a for loop;
                for (int i = 0; i < encodedArray.length; i++) {
                    encodedArray[i] = Message.substring(i, i + 1);//putting each letter into the alphabet;
                }
                //time to actually decode the encoded phrase;
                //to do this we will be using a forloop, so...
                for (int i = 0; i < encodedArray.length; i++) {
                    for (int j = 0; j < alphabet.length; j++) {
                        if (encodedArray[i].equals(alphabet[j])) {//comparing both the letters, if this statement is true then only will our code move on;
                            int k = j - ShiftValue;
                            if (k < 0) {
                                encodedArray[i] = alphabet[26 + k];
                            } else if (k >= 0) {
                                encodedArray[i] = alphabet[k];
                            }
                            break;
                        }

                    }
                }
                //printing the final and changed alphabet;
                for (int i = 0; i < encodedArray.length; i++) {
                    System.out.println(encodedArray[i]);
                }
                String phrase = "";
                for (int i = 0; i < encodedArray.length; i++) {
                    phrase = phrase + encodedArray[i];
                }

        return  phrase;
    }

    @Override
    public int getItemCount() {
        return messageObjectList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, sender;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.Message);
            sender = itemView.findViewById(R.id.Sender);
            layout = itemView.findViewById(R.id.Layout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String senderID = messageObjectList.get(position).getSender();
        int viewType;
        if(senderID.equals(firebaseUser.getUid())){
            viewType = MSG_RIGHT;
        }
        else{
            viewType = MSG_LEFT;
        }
        return  viewType;
    }
}
