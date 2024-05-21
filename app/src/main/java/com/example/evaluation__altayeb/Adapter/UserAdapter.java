package com.example.evaluation__altayeb.Adapter;
// UserAdapter.java

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evaluation__altayeb.Activity.UserDetails;
import com.example.evaluation__altayeb.Model.UserModel;
import com.example.evaluation__altayeb.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List<UserModel> userList;
    public  int page=1;
    public  int per_page=50;

    public UserAdapter(List<UserModel> userList,Context mContext) {
        this.userList = userList;
        this.mContext =mContext ;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_view_widget, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.userName.setText(user.getName());
        holder.status.setText(user.getStatus());

        final String status = user.getStatus();
        if (status.equalsIgnoreCase("active")) {
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active, 0, 0, 0);
        } else {
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_active, 0, 0, 0);
        }
       holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posit = holder.getAdapterPosition();
                UserModel clickedUser = userList.get(posit);
                if (position != RecyclerView.NO_POSITION) {
                    navigateToDetailScreen(clickedUser.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public   void nextPage(){
        page++;
    }
    public   void pageClear(){
        page=1;
    }
    public void clearData() {
        userList.clear();
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView userName;
        AppCompatTextView status;
        MaterialCardView userCard;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            userCard = itemView.findViewById(R.id.user_card);


        }
    }
    private void navigateToDetailScreen(int id) {
        Intent intent = new Intent(mContext, UserDetails.class);
        intent.putExtra("id", id);
        mContext.startActivity(intent);
    }
}
