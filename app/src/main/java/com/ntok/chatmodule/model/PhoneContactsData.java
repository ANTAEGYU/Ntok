package com.ntok.chatmodule.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.ntok.chatmodule.backend.firebase.FireBaseSingleton;
import com.ntok.chatmodule.fragment.ReadPhoneContactsAndFriendFragment;

import java.util.ArrayList;

/**
 * Created by Sonam on 10-05-2018.
 */

public class PhoneContactsData {

    private static PhoneContactsData phoneContactsData;
    public ArrayList<PhoneContact> contactList = new ArrayList<>();
    public boolean isFetchingFriends;
    public ReadPhoneContactsAndFriendFragment.OnCompletionListener onCompletionListener;
    int counter;

    private PhoneContactsData() {

    }

    public static PhoneContactsData getInstance() {
        if (phoneContactsData == null) {
            phoneContactsData = new PhoneContactsData();
        }
        return phoneContactsData;
    }

    public static String phoeNumberWithOutCountryCode(String phoneNumber) {
        if (phoneNumber == null)
            return "";
        if (phoneNumber.startsWith("+")) {
            if (phoneNumber.length() == 13) {
                String str_getMOBILE = phoneNumber.substring(3);
                return str_getMOBILE.replace(" ", "");
            } else if (phoneNumber.length() == 14) {
                String str_getMOBILE = phoneNumber.substring(4);
                return str_getMOBILE.replace(" ", "");
            }
            if (phoneNumber.length() == 12) {
                String str_getMOBILE = phoneNumber.substring(3);
                return str_getMOBILE.replace(" ", "");
            }


        } else if (phoneNumber.startsWith("0")) {
            return phoneNumber.substring(1);
        }
        return phoneNumber.replace(" ", "");
    }

    public void setOnCompletionListener(ReadPhoneContactsAndFriendFragment.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void getContacts(final Activity activity) {

        FireBaseSingleton.getInstance(activity).getUsers();

        new Thread(new Runnable() {
            @Override
            public void run() {

                isFetchingFriends = true;
                contactList = new ArrayList<PhoneContact>();
                String phoneNumber = null;
                String email = null;
                Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
                String _ID = ContactsContract.Contacts._ID;
                String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
                String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
                Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
                Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
                String photoUri = ContactsContract.CommonDataKinds.Phone.PHOTO_URI;
                String DATA = ContactsContract.CommonDataKinds.Email.DATA;
                PhoneContact output;
                ContentResolver contentResolver = activity.getContentResolver();
                Cursor cursor = contentResolver.query(CONTENT_URI, null, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", null, null);

                // Iterate every contact in the phone
                if (cursor.getCount() > 0) {
                    counter = 0;
                    while (cursor.moveToNext()) {
                        output = new PhoneContact();
                        // Update the progress message
                        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        String photo_uri = String.valueOf(getPhotoUri(activity,contact_id));
                        output.setPhotoUri(photo_uri);
                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {
                            output.setName(name);
                            //This is to read multiple phone numbers associated with the same contact
                            Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                output.setPhone_number(phoneNumber);
                            }
                            phoneCursor.close();
                            // Read every email id associated with the contact
                            Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                            while (emailCursor.moveToNext()) {
                                email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                                output.setEmail(email);
                            }
                            emailCursor.close();
                            String columns[] = {
                                    ContactsContract.CommonDataKinds.Event.START_DATE,
                                    ContactsContract.CommonDataKinds.Event.TYPE,
                                    ContactsContract.CommonDataKinds.Event.MIMETYPE,
                            };
                            String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                                    " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + contact_id;
                            String[] selectionArgs = null;
                            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
                            Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder);
                            Log.d("BDAY", birthdayCur.getCount() + "");
                            if (birthdayCur.getCount() > 0) {
                                while (birthdayCur.moveToNext()) {
                                    String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                                    output.setBirthday(birthday);
                                    Log.d("BDAY", birthday);
                                }
                            }
                            birthdayCur.close();
                        }
                        // Add the contact to the ArrayList
                        if (phoneNumber != null && phoneNumber.length()>0)
                            contactList.add(output);
                    }
                    checkFriends(activity);
                }

            }
        }).start();
    }

    public Uri getPhotoUri(Activity activity, String contact_id) {
        try {
            Cursor cur = activity.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contact_id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(contact_id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }


    public void checkFriends(Activity activity) {
        try {
            ArrayList<User> users = FirebaseDataSingleton.getInstance(activity).getAllUsers();
            for (User user : users) {
                for (PhoneContact phoneContact : contactList) {
                    if (phoeNumberWithOutCountryCode(user.phone_number).equalsIgnoreCase(phoeNumberWithOutCountryCode(phoneContact.getPhone_number()))) {
                        phoneContact.setIsOurContact(true);
                        if (phoneContact.getPhotoUri() == null)
                            if (user.userImage != null)
                                phoneContact.setPhotoUri(user.userImage);
                    }
                }
            }
            isFetchingFriends = false;
            if (onCompletionListener != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCompletionListener.onComplete();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
