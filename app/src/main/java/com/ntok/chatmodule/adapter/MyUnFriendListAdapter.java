package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MyUnFriendListAdapter extends RecyclerView.Adapter<MyUnFriendListAdapter.ItemViewHolder> {

    private ItemClick mItemClick;
    private Context context;
    private ArrayList<PhoneContact> myUnFriendContactList;

    public MyUnFriendListAdapter(Context context, ArrayList<PhoneContact> myUnFriendContactList) {
        this.context = context;
        this.myUnFriendContactList = myUnFriendContactList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new MyUnFriendListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (myUnFriendContactList != null && myUnFriendContactList.size() > 0) {
            holder.mUserName.setText(myUnFriendContactList.get(position).getName());
            holder.mUserMessage.setText(myUnFriendContactList.get(position).getPhone_number());

            holder.btnADD.setVisibility(View.VISIBLE);
            holder.btnChat.setVisibility(View.GONE);

            if (myUnFriendContactList.get(position).getPhotoUri() != null) {
                Log.e("Tag", "Photo Uri-----   " + myUnFriendContactList.get(position).getPhotoUri());
                ImageDisplayer.displayImage(myUnFriendContactList.get(position).getPhotoUri(), holder.mUserImage, null, context);
            }
        }
    }

    @Override
    public int getItemCount() {
        return myUnFriendContactList.size();
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
            if (myUnFriendContactList != null)
                mItemClick.onItemClickListener(myUnFriendContactList.get(position), view);
        }
    }


    public interface ItemClick {
        public void onItemClickListener(PhoneContact user, View view);
    }

    public void setItemClickListener(ItemClick itemClick) {
        mItemClick = itemClick;
    }

}
