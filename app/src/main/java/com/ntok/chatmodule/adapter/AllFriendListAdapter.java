package com.ntok.chatmodule.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.utils.CircleImageView;

import java.util.ArrayList;


/**
 * Created by Sonam on 08-05-2018.
 */
public class AllFriendListAdapter extends RecyclerView.Adapter<AllFriendListAdapter.ItemViewHolder> implements Filterable {


    private ItemClick mItemClick;
    private Context context;
    public ArrayList<FriendUser> friendListFiltered;
    public ArrayList<FriendUser> friendList;
    public int selectedPosition = -1;

    public AllFriendListAdapter(Context context, ArrayList<FriendUser> friendList) {
        this.context = context;
        this.friendListFiltered = friendList;
        this.friendList = friendList;
    }

    public void setFriendListFiltered(ArrayList<FriendUser> friendListFiltered) {
        this.friendListFiltered = friendListFiltered;
    }

    private void sendItemClickListener(int position, View view) {
        selectedPosition = position;
        notifyDataSetChanged();
    }


    public void setItemClickListener(ItemClick itemClick) {
        mItemClick = itemClick;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new ItemViewHolder(view);


    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (selectedPosition == position) {
            holder.mItemView.setBackgroundColor(context.getResources().getColor(R.color.row_selection_color));
        } else {
            holder.mItemView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
        if (friendListFiltered != null) {

            holder.mUserName.setText(friendListFiltered.get(position).username.equalsIgnoreCase("") ? friendListFiltered.get(position).phone_number : friendListFiltered.get(position).username);

            if (friendListFiltered.get(position).lastMessage != null)
                holder.mUserMessage.setText(friendListFiltered.get(position).lastMessage.equalsIgnoreCase("") ? "" : friendListFiltered.get(position).lastMessage);
            if (friendListFiltered.get(position).userImage != null)
                if (friendListFiltered.get(position).userImage != null && !friendListFiltered.get(position).userImage.trim().equalsIgnoreCase("")) {
                    ImageDisplayer.displayImage(friendListFiltered.get(position).userImage, holder.mUserImage, null, context);
                }
//            if (friendListFiltered.get(position).onlineStatus != null) {
//                if (friendListFiltered.get(position).onlineStatus.equalsIgnoreCase("Online")) {
//                    holder.status.setImageDrawable(context.getResources().getDrawable(R.drawable.status_online));
//                } else {
//                    holder.status.setImageDrawable(context.getResources().getDrawable(R.drawable.status_offline));
//
//                }
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (friendListFiltered != null)
            return friendListFiltered.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public interface ItemClick {
        public void onItemClickListener(FriendUser user, View view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView mUserImage;
        private TextView mUserName;
        private TextView mUserMessage;
        private ImageView status;
        private View mItemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_image);
            mUserMessage = itemView.findViewById(R.id.user_message);
            mUserName = itemView.findViewById(R.id.user_name);
            mItemView = itemView.findViewById(R.id.item_view_relative_layout);
            status = itemView.findViewById(R.id.status);
            status.setVisibility(View.GONE);
            itemView.findViewById(R.id.btn_add).setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sendItemClickListener(getPosition(), v);
        }


    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    friendListFiltered = friendList;
                } else {
                    ArrayList<FriendUser> filteredList = new ArrayList<>();
                    for (FriendUser row : friendList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        if (row.username.toLowerCase().contains(charString.toLowerCase()) || row.phone_number.contains(charSequence)) {
                            filteredList.add(row);
                        }

                    }
                    friendListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = friendListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                friendListFiltered = (ArrayList<FriendUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}


