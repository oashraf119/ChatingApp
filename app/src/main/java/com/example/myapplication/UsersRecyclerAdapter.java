package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ItemUsersBinding;

import java.util.ArrayList;

public class UsersRecyclerAdapter extends  RecyclerView.Adapter<UsersRecyclerAdapter.Holder>{


    private ArrayList<ModelUserModel> list ;

    private OnItemClick onItemClick ;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setList(ArrayList<ModelUserModel> list) {
        this.list = list;
    }
    public ArrayList<ModelUserModel> getList() {
        return list;
    }

    public void addItem (ModelUserModel modelUserModel){

        list.add(modelUserModel);
      notifyItemInserted(list.size() -1);
    }
    public void changeItem (int pos,ModelUserModel modelUserModel){

        list.set(pos,modelUserModel);
        notifyItemChanged(pos);
    }
    public void removeItem (int pos){

        list.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemUsersBinding binding = ItemUsersBinding.inflate(LayoutInflater
                .from(parent.getContext()),parent,false);

        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        private ItemUsersBinding binding;

        public Holder(ItemUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClick(list.get(getLayoutPosition()));
                }
            });
        }
        public void bind (ModelUserModel modelUserModel){
            binding.textName.setText( modelUserModel.getName());
            binding.textPhone.setText(modelUserModel.getPhone());

            Glide.with(binding.getRoot().getContext())
                            .load(modelUserModel.getImageURL())
                                    .into(binding.imageUser);

        }
    }

    interface OnItemClick{
        void onClick(ModelUserModel modelUserModel);
    }
}
