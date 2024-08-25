package com.example.myapplication;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ItemChatBinding;
import com.example.myapplication.databinding.ItemUsersBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends  RecyclerView.Adapter<ChatRecyclerAdapter.Holder>{



    private String currentUserId ;


    public ChatRecyclerAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private ArrayList<ModelChat> list = new ArrayList<>() ;


    public ArrayList<ModelChat> getList() {
        return list;
    }

    public void addItem (ModelChat chat){

        list.add(chat);
      notifyItemInserted(list.size() -1);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1 ) {
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater
                    .from(parent.getContext()), parent, false);

            return new Holder(binding);
        }else {
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater
                    .from(parent.getContext()), parent, false);
            return new Holder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        private ItemChatBinding binding;

        public Holder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind (ModelChat chat){

            if (chat.getType().equals(MessageType.TEXT.name())){
                binding.textMessage.setVisibility(View.VISIBLE);
                binding.imageChat.setVisibility(View.GONE);
                binding.textMessage.setText(chat.getMessage());
                if (chat.getSenderId().equals(currentUserId)){
                    binding.textMessage.setGravity(Gravity.END);
                }else {
                    binding.textMessage.setGravity(Gravity.START);
                }
            }else {
                binding.textMessage.setVisibility(View.GONE);
                binding.imageChat.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot().getContext())
                        .load(chat.getMessage())
                        .into(binding.imageChat);
            }


        }
    }
}
