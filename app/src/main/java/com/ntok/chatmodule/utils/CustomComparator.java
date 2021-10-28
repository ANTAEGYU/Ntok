package com.ntok.chatmodule.utils;

import com.ntok.chatmodule.model.PhoneContact;

import java.util.Comparator;

public class CustomComparator implements Comparator<PhoneContact> {
    @Override
    public int compare(PhoneContact o1, PhoneContact o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
