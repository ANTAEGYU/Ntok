package com.ntok.chatmodule.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.model.CountryModel;

import java.util.ArrayList;

public class CountryDropDownListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<CountryModel> filterObjects;
    private ArrayList<CountryModel> objects;
    private CountryModel selectedObject;
    private LayoutInflater mInflater;
    private Activity context;
    private boolean isSelectCountryCode = false;

    public CountryDropDownListAdapter(Activity context, ArrayList<CountryModel> items, CountryModel selectedItems) {
        filterObjects = items;
        objects = items;
        selectedObject = selectedItems;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }


    public int getCount() {
        return filterObjects.size();
    }

    public CountryModel getItem(int position) {
        return filterObjects.get(position);
    }

    public CountryModel getSelectedObject() {
        return selectedObject;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderCountryWithFlag holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.country_with_flag_item, parent, false);
            holder = new ViewHolderCountryWithFlag();
            holder.parentView = convertView.findViewById(R.id.parent_view);
            holder.countryName = (TextView) convertView.findViewById(R.id.name);
            holder.countryImage = (ImageView) convertView.findViewById(R.id.globe_image);
            holder.selectedCountry = (ImageView) convertView.findViewById(R.id.check_box);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolderCountryWithFlag) convertView.getTag();
        }
        holder.countryName.setText(filterObjects.get(position).getCountryName()+" (+"+filterObjects.get(position).getCountryCallingCode()+")");
        try {
            int id;
            if (filterObjects.get(position).getCountryCode().toLowerCase().trim().equalsIgnoreCase("do")) {
                id = context.getResources().getIdentifier("country_" + filterObjects.get(position).getCountryCode().toLowerCase().trim(), "drawable", context.getPackageName());
            } else {
                id = context.getResources().getIdentifier(filterObjects.get(position).getCountryCode().toLowerCase().trim(), "drawable", context.getPackageName());
            }
            holder.countryImage.setImageResource(id);
            if (selectedObject != null && selectedObject.getCountryName().equals(filterObjects.get(position).getCountryName())) {
                holder.selectedCountry.setVisibility(View.VISIBLE);
            } else {
                holder.selectedCountry.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }


        return convertView;
    }


    public void addAll(ArrayList<CountryModel> selectedCountryList) {
        filterObjects = selectedCountryList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterObjects = objects;
                } else {
                    ArrayList<CountryModel> filteredList = new ArrayList<>();
                    for (CountryModel row : objects) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCountryName().toLowerCase().contains(charString.toLowerCase()) || row.getCountryCallingCode().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filterObjects = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterObjects;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterObjects = (ArrayList<CountryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    private class ViewHolderCountryWithFlag {
        public View parentView;
        ImageView countryImage;
        TextView countryName;
        ImageView selectedCountry;
    }
}
