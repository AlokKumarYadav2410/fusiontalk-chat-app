package com.example.fusiontalk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindow extends AppCompatActivity {

    String receiverImage, receiverUid, receiverNames, senderUid;
    CircleImageView profile;
    TextView receiversName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderImg;
    public static String receiverImg;

    String senderRoom, receiverRoom;
    RecyclerView msgAdapters;
    ArrayList<MsgModelClass> msgArrayList;
    MessagesAdapter messagesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        msgAdapters = findViewById(R.id.msgAdapter);
        sendbtn = findViewById(R.id.sendBtn);
        textmsg = findViewById(R.id.textMsg);
        profile = findViewById(R.id.profileImgRec);
        receiversName = findViewById(R.id.receiverName);

        // Get data from Intent
        receiverNames = getIntent().getStringExtra("receiverName");
        receiverImage = getIntent().getStringExtra("receiverImg");
        receiverUid = getIntent().getStringExtra("uid");

        // Set up RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgAdapters.setLayoutManager(linearLayoutManager);
        msgArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(ChatWindow.this, msgArrayList);
        msgAdapters.setAdapter(messagesAdapter);

        // Set receiver's profile picture and name
        Picasso.get().load(receiverImage).into(profile);
        receiversName.setText(receiverNames);

        // Get sender UID and create chat rooms
        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        // Debugging logs
        Log.d("ChatWindow", "SenderRoom: " + senderRoom + ", ReceiverRoom: " + receiverRoom);

        // Load sender's profile image
        DatabaseReference userReference = database.getReference().child("user").child(senderUid);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilePic").getValue(String.class);
                receiverImg = receiverImage; // Already received from Intent
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWindow.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Load chat messages
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MsgModelClass messages = dataSnapshot.getValue(MsgModelClass.class);
                    msgArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
                msgAdapters.scrollToPosition(msgArrayList.size() - 1); // Scroll to the latest message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWindow.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });

        // Send button logic
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(ChatWindow.this, "Please enter the message", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for harmful content
                if (isHarmfulMessage(message)) {
                    Toast.makeText(ChatWindow.this, "This message contains harmful language and cannot be sent.", Toast.LENGTH_SHORT).show();
                    return; // Stop message from being sent
                }

                textmsg.setText("");
                Date date = new Date();
                MsgModelClass msg = new MsgModelClass(message, senderUid, date.getTime());

                // Store message in sender and receiver chat rooms
                database.getReference().child("chats").child(senderRoom).child("messages")
                        .push().setValue(msg).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                database.getReference().child("chats").child(receiverRoom).child("messages")
                                        .push().setValue(msg);
                            }
                        });
            }

            private boolean isHarmfulMessage(String message) {
                String[] harmfulWords = {"abuse1", "abuse2", "badword1", "stupid", "dumb"}; // Add harmful words
                for (String word : harmfulWords) {
                    if (message.toLowerCase().contains(word)) {
                        return true;
                    }
                }
                return false;
            }

        });



    }
}
