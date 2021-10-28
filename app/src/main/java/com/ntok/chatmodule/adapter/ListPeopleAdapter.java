package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ntok.chatmodule.ImageDisplayer.ImageDisplayer;
import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.FriendUserDetail;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.utils.CircleImageView;
import com.ntok.chatmodule.utils.Lg;

import java.util.ArrayList;


/**
 * Created by Sonam on 21-05-2018.
 */

public class ListPeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FriendUser> listFriend;
    private  Button btnAddGroup;
    private ArrayList<FriendUserDetail> listIDChoose;
    private ArrayList<FriendUserDetail> listIDRemove;
    private boolean isEdit;
    private GroupModel editGroup;

    public ListPeopleAdapter(Context context, ArrayList<FriendUser> listFriend, Button btnAddGroup, ArrayList<FriendUserDetail> listIDChoose, ArrayList<FriendUserDetail> listIDRemove, boolean isEdit, GroupModel editGroup) {
        this.context = context;
        this.listFriend = listFriend;
        this.btnAddGroup = btnAddGroup;
        this.listIDChoose = (ArrayList<FriendUserDetail>) listIDChoose;
        this.listIDRemove =  (ArrayList<FriendUserDetail>)listIDRemove;

        this.isEdit = isEdit;
        this.editGroup = editGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_add_friend, parent, false);
        return new ItemGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((ItemGroupHolder) holder).txtName.setText(listFriend.get(position).username);

        ((ItemGroupHolder) holder).txtEmail.setText(listFriend.get(position).phone_number);
        try {
            String avata = listFriend.get(position).userImage;
            ImageDisplayer.displayImage(avata, ((ItemGroupHolder) holder).avata, null, context);
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
        final String id = listFriend.get(position).phone_number;
        final String name = listFriend.get(position).username;
        final String image = listFriend.get(position).userImage;

        ((ItemGroupHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    FriendUserDetail friendUserDetail = new FriendUserDetail();
                    friendUserDetail.setFriendPhoneNumber(id);
                    friendUserDetail.setFriendImage(image);
                    friendUserDetail.setFriendName(name);
                    listIDChoose.add(friendUserDetail);

                    if(listIDRemove.size()>0){
                        for(int i=0;i<listIDRemove.size();i++){
                            if(listIDRemove.get(i).getFriendPhoneNumber().equalsIgnoreCase(id)){
                                listIDRemove.remove(i);
                                break;
                            }
                        }
                    }

                } else {
                    FriendUserDetail friendUserDetail = new FriendUserDetail();
                    friendUserDetail.setFriendPhoneNumber(id);
                    friendUserDetail.setFriendImage(image);
                    friendUserDetail.setFriendName(name);
                    listIDRemove.add(friendUserDetail);

                    if(listIDChoose.size()>0){
                        for(int i=0;i<listIDChoose.size();i++){
                            if(listIDChoose.get(i).getFriendPhoneNumber().equalsIgnoreCase(id)){
                                listIDChoose.remove(i);
                                break;
                            }
                        }
                    }
                }
                if (listIDChoose.size() >= 3) {
                    Drawable img = context.getResources().getDrawable( R.drawable.ic_add_group);
                    btnAddGroup.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    btnAddGroup.setEnabled(true);
                    btnAddGroup.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.enable_button));
                    btnAddGroup.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    Drawable img = context.getResources().getDrawable( R.drawable.ic_add_group_disable);
                    btnAddGroup.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    btnAddGroup.setEnabled(false);
                    btnAddGroup.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.disable_button));
                    btnAddGroup.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            }
        });

        if (isEdit && editGroup.getFriendUsers().contains(id)) {
            ((ItemGroupHolder) holder).checkBox.setChecked(true);
        } else if (editGroup != null && !editGroup.getFriendUsers().contains(id)) {
            ((ItemGroupHolder) holder).checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return listFriend.size();
    }

    public interface OnCompletionListener {
        public void onComplete();
    }

    class ItemGroupHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtEmail;
        public CircleImageView avata;
        public CheckBox checkBox;

        public ItemGroupHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkAddPeople);
        }

    }
}

