package com.ntok.chatmodule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.LoginActivity;
import com.ntok.chatmodule.adapter.CountryDropDownListAdapter;
import com.ntok.chatmodule.interfaces.GetSelectedCountryInterface;
import com.ntok.chatmodule.model.CountryModel;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Sonam Gupta on 31/05/18.
 */

public class SearchCountryFragment extends RootFragment {

    private static SearchCountryFragment searchCountryFragment;
    private String TAG = SearchCountryFragment.class.getSimpleName();
    private ListView countryList;
    private CountryDropDownListAdapter adapter;
    private CountryModel selectedCountryModel;
    private ArrayList<CountryModel> countryModels;
    private GetSelectedCountryInterface selectedCountryInterface;

    public static SearchCountryFragment getSearchCountryFragment(ArrayList<CountryModel> countryModels, CountryModel selectedCountryModel, GetSelectedCountryInterface selectedCountryInterface) {
        searchCountryFragment = new SearchCountryFragment();
        searchCountryFragment.selectedCountryModel = selectedCountryModel;
        searchCountryFragment.countryModels = countryModels;
        searchCountryFragment.selectedCountryInterface = selectedCountryInterface;
        return searchCountryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_country_layout, new FrameLayout(getActivity()), false);

        countryList = (ListView) rootView.findViewById(R.id.country_listview);
        initToolBar();
      // getCountry();
        clearSearch();
        return rootView;
    }

    private void clearSearch() {

        adapter = new CountryDropDownListAdapter(getActivity(), countryModels, selectedCountryModel);
        countryList.setAdapter(adapter);
        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryInterface.onReceiveSelectedCountries(adapter.getItem(position));
            }
        });
    }

    private void initToolBar() {
        ((LoginActivity) getActivity()).searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                ((LoginActivity) getActivity()).searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                Log.i("query", "" + query);
                // filter recycler view when query submitted
                if (adapter != null)
                    adapter.getFilter().filter(query);
                else
                    adapter.getFilter().filter(query);

            }

        });
    }
    public void getCountry() {
      //  countryModels = new ArrayList<>();
        String[] locales = Locale.getISOCountries();
        if (countryModels == null) {
            countryModels = new ArrayList<>();
            for (String countryCode : locales) {
                Locale obj = new Locale("", countryCode);
                CountryModel country = new CountryModel();
                country.setCountryName(obj.getDisplayName());
                country.setCountryCode(obj.getCountry());
                countryModels.add(country);

            }
        }
    }
    @Override
    public void onRefreshData() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }


}
