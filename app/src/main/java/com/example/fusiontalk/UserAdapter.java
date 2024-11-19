package com.example.fusiontalk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {

    Context welcomePage;
    ArrayList<Users> usersArrayList;

    public UserAdapter(WelcomePage welcomePage, ArrayList<Users> usersArrayList) {
        this.welcomePage = welcomePage;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(welcomePage).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        holder.userStatus.setText(users.status);
        //image ko show krne ke liye
        Picasso.get().load(users.profilePic).into(holder.userImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(welcomePage, ChatWindow.class);
                intent.putExtra("receiverName", users.getUserName());
                intent.putExtra("receiverImg", users.getProfilePic());
                intent.putExtra("uid", users.getUserId());
                welcomePage.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView username;
        TextView userStatus;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            username = itemView.findViewById(R.id.username);
            userStatus = itemView.findViewById(R.id.userStatus);

        }
    }
}
