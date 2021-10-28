package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.PhoneContact;

import java.util.ArrayList;

public class MyFriendListAdapter extends RecyclerView.Adapter<MyFriendListAdapter.ItemViewHolder> {

    private ItemClick mItemClick;
    private Context context;
    private ArrayList<PhoneContact> myFriendContactList;

    public MyFriendListAdapter(Context context, ArrayList<PhoneContact> myFriendContactList) {
        this.context = context;
        this.myFriendContactList = myFriendContactList;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new MyFriendListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (myFriendContactList != null && myFriendContactList.size() > 0) {
            holder.mUserName.setText(myFriendContactList.get(position).getName());
            holder.mUserMessage.setText(myFriendContactList.get(position).getPhone_number());
            holder.btnChat.setVisibility(View.VISIBLE);
            holder.btnADD.setVisibility(View.GONE);
            if (myFriendContactList.get(position).getPhotoUri() != null) {
                ImageDisplayer.displayImage(myFriendContactList.get(position).getPhotoUri(), holder.mUserImage, null, context);
            }
        }
    }

    @Override
    public int getItemCount() {
        return myFriendContactList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mUserMessage;
        private ImageView btnADD;
        private ImageView btnChat;
        private FrameLayout frameLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mUserImage = (ImageView) itemView.findViewById(R.id.user_image);
            mUserMessage = (TextView) itemView.findViewById(R.id.user_message);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            btnADD = (ImageView) itemView.findViewById(R.id.btn_add);
            btnChat = (ImageView) itemView.findViewById(R.id.btn_chat);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout);
            btnADD.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
            btnADD.setOnClickListener(this);
            btnChat.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sendItemClickListener(getPosition(), v);
        }
    }

    private void sendItemClickListener(int position, View view) {
        if (mItemClick != null) {
            if (myFriendContactList != null)
                mItemClick.onItemClickListener(myFriendContactList.get(position), view);
        }
    }


    public interface ItemClick {
        public void onItemClickListener(PhoneContact user, View view);
    }

    public void setItemClickListener(ItemClick itemClick) {
        mItemClick = itemClick;
    }
}
