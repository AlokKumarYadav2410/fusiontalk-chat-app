package com.example.fusiontalk;

import static com.example.fusiontalk.ChatWindow.receiverImg;
import static com.example.fusiontalk.ChatWindow.senderImg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MsgModelClass> messageAdapter;
    private static final int ITEM_SEND = 1;
    private static final int ITEM_RECEIVE = 2;

    public MessagesAdapter(Context context, ArrayList<MsgModelClass> messageAdapter) {
        this.context = context;
        this.messageAdapter = messageAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgModelClass msg = messageAdapter.get(position);
        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.msgText.setText(msg.getMessage());
            Picasso.get().load(senderImg).into(senderViewHolder.circleImageView);

        }
        else{
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.msgText.setText(msg.getMessage());
            Picasso.get().load(receiverImg).into(receiverViewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageAdapter.size();
    }

    @Override
    public int getItemViewType(int position) {
        MsgModelClass msg = messageAdapter.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(msg.getSenderId())){
            return ITEM_SEND;
        }
        else{
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgText;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profileSender);
            msgText = itemView.findViewById(R.id.msgSenderTyp);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgText;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profileReceiver);
            msgText = itemView.findViewById(R.id.msgReceiverTyp );
        }
    }
}
