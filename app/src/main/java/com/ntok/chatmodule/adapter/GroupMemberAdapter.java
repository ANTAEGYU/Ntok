package com.ntok.chatmodule.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.GroupModel;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    private Activity activity;
    private Fragment fragment;
    private GroupModel groupModel;

    public GroupMemberAdapter(Activity activity, Fragment fragment, GroupModel groupModel) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.fragment = fragment;
        this.groupModel = groupModel;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =  inflater.inflate(R.layout.group_member_view_profile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

       holder.memberPhoneNumber.setText(groupModel.getFriendUsers().get(position).getFriendPhoneNumber());

            holder.memberName.setText(groupModel.getFriendUsers().get(position).getFriendName());


        Glide.with(activity).load(groupModel.getFriendUsers().get(position).getFriendImage()).placeholder(R.drawable.default_user_black).into(holder.memberProfilePic);
       if(groupModel.getFriendUsers().get(position).getFriendPhoneNumber().equalsIgnoreCase(groupModel.getAdmin())){
           holder.memberName.setText(groupModel.getFriendUsers().get(position).getFriendName());
           holder.memberStatus.setText("Admin");
           holder.memberStatus.setBackground(activity.getResources().getDrawable(R.drawable.admin_round));
       }

    }

    @Override
    public int getItemCount() {
        return groupModel.getFriendUsers().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.member_profile_pic)
        ImageView memberProfilePic;
        @BindView(R.id.member_name)
        TextView memberName;
        @BindView(R.id.member_phone_number)
        TextView memberPhoneNumber;
        @BindView(R.id.member_status)
        TextView memberStatus;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
