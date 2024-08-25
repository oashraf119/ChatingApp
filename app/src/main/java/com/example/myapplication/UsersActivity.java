package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityUsersBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class UsersActivity extends AppCompatActivity {

    DatabaseReference ref = FirebaseDatabase
            .getInstance().getReference();
    ActivityUsersBinding binding;
    ArrayList<ModelUserModel> list = new ArrayList<>();

    ChildEventListener eventListener ;
    UsersRecyclerAdapter adapter = new UsersRecyclerAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter.setList(list);
        binding.recyclerUsers.setAdapter(adapter);
        getUsers();
        adapter.setOnItemClick(new UsersRecyclerAdapter.OnItemClick() {
            @Override
            public void onClick(ModelUserModel modelUserModel) {
                binding.progress.setVisibility(View.VISIBLE);
                checkNode(FirebaseAuth.getInstance().getUid(), modelUserModel.getId());

            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UsersActivity.this,LoginActivity.class));
                finishAffinity();

            }
        });

    }
    private void checkNode (String senderId , String reciverId){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String privateNodePath = senderId+reciverId ;
                if (!dataSnapshot.child("chats").hasChild(privateNodePath)) {
                    privateNodePath = reciverId+senderId ;
                    if (!dataSnapshot.child("chats").hasChild(privateNodePath)){
                        ref.child("chats").child(privateNodePath).setValue("");
                    }
                }
                Intent intent =  new Intent(UsersActivity.this,ChatActivity.class);
                intent.putExtra("id", reciverId);
                intent.putExtra("chatNode", privateNodePath);
                startActivity(intent);
                binding.progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progress.setVisibility(View.GONE);
            }
        });

    }

    private void getUsers() {
        eventListener  = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ModelUserModel userModel = dataSnapshot.getValue(ModelUserModel.class);
                if (!userModel.getId().equals(FirebaseAuth.getInstance().getUid())){
                   adapter.addItem(userModel);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ModelUserModel model = dataSnapshot.getValue(ModelUserModel.class);

                int size  =  adapter.getList().size();
                ArrayList<ModelUserModel> localList = adapter.getList();
                for (int i = 0; i < size; i++) {
                    if (localList.get(i).getId().equals(model.getId())) {
                        adapter.changeItem(i, model);
                    }
                }
                Toast.makeText(UsersActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ModelUserModel model = dataSnapshot.getValue(ModelUserModel.class);

                int size  =  adapter.getList().size();
                ArrayList<ModelUserModel> localList = adapter.getList();
                int pos = getItemPos(size,localList,model);
                if (pos != -1){
                    adapter.removeItem(pos);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("TAG", "onChildChanged: " );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child("users")
//                .orderByChild("name")
//                .equalTo("ali")
                .addChildEventListener(eventListener);
    }

    private int getItemPos (int size , ArrayList<ModelUserModel> localList
            ,ModelUserModel model){
        for (int i = 0; i < size; i++) {
            if (localList.get(i).getId().equals(model.getId())) {
                return i ;
            }
        }
        return  -1 ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.child("users")
                .removeEventListener(eventListener);
        eventListener = null ;
        binding = null;
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }
}