package com.ntok.chatmodule.interfaces;


import com.ntok.chatmodule.model.CountryModel;

/**
 * Created by jiffy 03 on 05-09-2016.
 */
public interface GetSelectedCountryInterface {
    void onReceiveSelectedCountries(CountryModel selectedCountry);
}
