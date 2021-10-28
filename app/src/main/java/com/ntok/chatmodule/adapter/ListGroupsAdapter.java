package com.ntok.chatmodule.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.fragment.GroupListFragment;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.utils.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class  ListGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GroupModel> listGroup;
    private Activity context;

    public ListGroupsAdapter(Activity context, ArrayList<GroupModel> listGroup) {
        this.context = context;
        this.listGroup = listGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group, parent, false);
        return new ItemGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String groupName = listGroup.get(position).getGroup_name();

        if (groupName != null && groupName.length() > 0) {
             Log.e("Tag",listGroup.get(position).getGroup_name());
            ((ItemGroupViewHolder) holder).txtGroupName.setText(groupName);
        }
        ((ItemGroupViewHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(new Object[]{groupName, position});
                view.getParent().showContextMenuForChild(view);
            }
        });
        ((LinearLayout) ((ItemGroupViewHolder) holder).txtGroupName.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).startChartFragment(listGroup.get(position), null, null);


            }
        });
        Glide.with(context).load(listGroup.get(position).getGroupImage()).placeholder(R.drawable.default_user_black).into(((ItemGroupViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    class ItemGroupViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView  txtGroupName;
        public ImageButton btnMore;
        public CircleImageView imageView;

        public ItemGroupViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            imageView = (CircleImageView) itemView.findViewById(R.id.icon_group);
            txtGroupName = (TextView) itemView.findViewById(R.id.txtName);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMoreAction);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle((String) ((Object[]) btnMore.getTag())[0]);
            Intent data = new Intent();
            data.putExtra(GroupListFragment.CONTEXT_MENU_KEY_INTENT_DATA_POS, (Integer) ((Object[]) btnMore.getTag())[1]);
            data.putExtra(GroupListFragment.CONTEXT_MENU_KEY_INTENT_DATA_GROUP_ID, listGroup.get((Integer) ((Object[]) btnMore.getTag())[1]).getId());
            if(listGroup.get((Integer) ((Object[]) btnMore.getTag())[1]).getAdmin().equalsIgnoreCase(FirebaseDataSingleton.getInstance(context).getUser().phone_number)) {
                menu.add(Menu.NONE, GroupListFragment.CONTEXT_MENU_EDIT, Menu.NONE, "Edit group").setIntent(data);
                menu.add(Menu.NONE, GroupListFragment.CONTEXT_MENU_DELETE, Menu.NONE, "Delete group").setIntent(data);
            }else{
                menu.add(Menu.NONE, GroupListFragment.CONTEXT_MENU_LEAVE, Menu.NONE, "Leave group").setIntent(data);
            }
        }
    }
}



