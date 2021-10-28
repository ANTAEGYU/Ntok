package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.utils.CircleImageView;

import java.util.ArrayList;


/**
 * Created by Sonam on 08-05-2018.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ItemViewHolder> {


    //    private ItemClick mItemClick;
    private Context context;
    private ArrayList<FriendUser> friendList;

    public FriendListAdapter(Context context, ArrayList<FriendUser> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    //
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new ItemViewHolder(view);


    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        if (friendList != null) {

                holder.mUserName.setText(friendList.get(position).username.equalsIgnoreCase("") ? friendList.get(position).phone_number : friendList.get(position).username);

            if (friendList.get(position).lastMessage != null)
                holder.mUserMessage.setText(friendList.get(position).lastMessage.equalsIgnoreCase("") ? "" : friendList.get(position).lastMessage);
            if (friendList.get(position).userImage != null)
                if (friendList.get(position).userImage != null && !friendList.get(position).userImage.trim().equalsIgnoreCase("")) {
                    ImageDisplayer.displayImage(friendList.get(position).userImage, holder.mUserImage, null, context);
                }
            if (friendList.get(position).onlineStatus != null) {
                if (friendList.get(position).onlineStatus.equalsIgnoreCase("Online")) {
                    holder.mFrameLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.mFrameLayout.setVisibility(View.GONE);
                }
            }

            //on Friend list item click listener
            holder.mItemViewRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).startChartFragment(null, friendList.get(position), null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (friendList != null)
            return friendList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public interface ItemClick {
        public void onItemClickListener(FriendUser user, View view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mUserImage;
        private TextView mUserName;
        private TextView mUserMessage;
        private ImageView status;
        private RelativeLayout mItemViewRelativeLayout;
        private FrameLayout mFrameLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mUserImage = (CircleImageView) itemView.findViewById(R.id.user_image);
            mUserMessage = (TextView) itemView.findViewById(R.id.user_message);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            status = (ImageView) itemView.findViewById(R.id.status);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout);
            mItemViewRelativeLayout = itemView.findViewById(R.id.item_view_relative_layout);
            status.setVisibility(View.GONE);
            itemView.findViewById(R.id.btn_add).setVisibility(View.GONE);
            itemView.findViewById(R.id.btn_chat).setVisibility(View.GONE);


        }


    }

}

