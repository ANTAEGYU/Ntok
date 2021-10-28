package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.PhoneContact;

import java.util.ArrayList;

/**
 * Created by Sonam on 08-05-2018.
 */
public class PhoneContactListAdapter extends RecyclerView.Adapter<PhoneContactListAdapter.ItemViewHolder> implements Filterable {


    private ItemClick mItemClick;
    private Context context;
    private ArrayList<PhoneContact> contactList ;
    private ArrayList<PhoneContact> contactListFiltered;

    public PhoneContactListAdapter(Context context, ArrayList<PhoneContact> friendList) {
        this.context = context;
        this.contactList = friendList;
        this.contactListFiltered = friendList;
    }

    private void sendItemClickListener(int position, View view) {
        if (mItemClick != null) {
            if (contactListFiltered != null)
                mItemClick.onItemClickListener(contactListFiltered.get(position), view);
        }
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
        if (contactListFiltered != null && contactListFiltered.size() >0) {
            holder.mUserName.setText(contactListFiltered.get(position).getName());
            holder.mUserMessage.setText(contactListFiltered.get(position).getPhone_number());
            if (contactListFiltered.get(position).getIsOurContact()) {
                holder.btnChat.setVisibility(View.VISIBLE);
                holder.btnADD.setVisibility(View.GONE);
            } else {
                holder.btnADD.setVisibility(View.VISIBLE);
                holder.btnChat.setVisibility(View.GONE);
            }
            if (contactListFiltered.get(position).getPhotoUri() != null) {
                Log.e("Tag","Photo Uri-----   "+contactListFiltered.get(position).getPhotoUri());
                ImageDisplayer.displayImage(contactListFiltered.get(position).getPhotoUri(), holder.mUserImage, null, context);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (contactListFiltered != null)
            return contactListFiltered.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public interface ItemClick {
        public void onItemClickListener(PhoneContact user, View view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    ArrayList<PhoneContact> filteredList = new ArrayList<>();
                    for (PhoneContact row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<PhoneContact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}

