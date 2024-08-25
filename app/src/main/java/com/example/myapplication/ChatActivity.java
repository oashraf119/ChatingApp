package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {



    ActivityChatBinding binding;
    private ChatRecyclerAdapter adapter = new ChatRecyclerAdapter(FirebaseAuth.getInstance().getUid());
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ChildEventListener eventListener ;
    private String reciverId = "";
    private String firebasePrivateNode ="" ;
    private Uri imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reciverId = getIntent().getStringExtra("id");
        firebasePrivateNode = getIntent().getStringExtra("chatNode");

        binding.chatRecycler.setAdapter(adapter);


        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(binding.editMessage.getText().toString(), MessageType.TEXT);
            }
        });
        binding.btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGallery();
            }
        });

        getMessages();

    }

    private void goToGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    private void sendMessage(String message, MessageType type) {

        ref.child("chats")
                .child(firebasePrivateNode)
                .push()
                .setValue(new ModelChat(message
                        , type.name(), FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.editMessage.setText("");
                    }
                });
    }
    private void getMessages(){

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ModelChat chat = dataSnapshot.getValue(ModelChat.class);
                adapter.addItem(chat);
                if (adapter.getItemCount() > 0) {
                    binding.chatRecycler.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.child("chats").child(firebasePrivateNode).addChildEventListener(eventListener);

    }
    private void uploadImage(Uri imageUri){
        StorageReference reference = storageReference.child("chatImages")
                .child(firebasePrivateNode)
                .child(System.currentTimeMillis()+FirebaseAuth.getInstance().getUid());

        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                sendMessage(uri.toString(), MessageType.IMAGE);
                            }
                        });
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (data != null){
                imagePath = data.getData();
                uploadImage(imagePath);

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.child("chats").removeEventListener(eventListener);
        eventListener = null;
        binding = null;
    }
}