package com.ntok.chatmodule.backend.sqlite_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.ntok.chatmodule.model.AudioModelClass;
import com.ntok.chatmodule.model.ImageUploadInfo;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.PhoneContact;
import com.ntok.chatmodule.model.VideoModelClass;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Sonam on 22-05-2018.
 */


public final class MessageDB {
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_FROM + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TO + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_BODY + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TIMESTAMP + REAL_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CONTACT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_VIDEO_MODEL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_IMAGE_MODEL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_AUDIO_MODEL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_ROOM_ID + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    private static MessageDBHelper mDbHelper = null;
    private static MessageDB instance = null;
    Gson gson = new Gson();

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MessageDB() {
    }

    public static MessageDB getInstance(Context context) {
        if (instance == null) {
            instance = new MessageDB();
            mDbHelper = new MessageDBHelper(context);
        }
        return instance;
    }

    public long addMessage(MessageModel messageModel, String roomID) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ID, messageModel.getId());
        values.put(FeedEntry.COLUMN_NAME_FROM, messageModel.getFrom());
        values.put(FeedEntry.COLUMN_NAME_TO, messageModel.getTo());
        values.put(FeedEntry.COLUMN_NAME_BODY, messageModel.getBody());
        values.put(FeedEntry.COLUMN_NAME_TIMESTAMP, messageModel.getTimestamp());
        values.put(FeedEntry.COLUMN_NAME_CONTACT, gson.toJson(messageModel.getContact()));
        values.put(FeedEntry.COLUMN_NAME_VIDEO_MODEL, gson.toJson(messageModel.getVideoModelClass()));
        values.put(FeedEntry.COLUMN_NAME_LOCATION, messageModel.getLocation());
        values.put(FeedEntry.COLUMN_NAME_IMAGE_MODEL, gson.toJson(messageModel.getImageModel()));
        values.put(FeedEntry.COLUMN_NAME_AUDIO_MODEL, gson.toJson(messageModel.getAudioModelClass()));
        values.put(FeedEntry.COLUMN_NAME_TYPE, messageModel.getType());
        values.put(FeedEntry.COLUMN_NAME_ROOM_ID, roomID);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(FeedEntry.TABLE_NAME, null, values);
    }


    public long updateMessage(MessageModel messageModel, String roomID) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ID, messageModel.getId());
        values.put(FeedEntry.COLUMN_NAME_FROM, messageModel.getFrom());
        values.put(FeedEntry.COLUMN_NAME_TO, messageModel.getTo());
        values.put(FeedEntry.COLUMN_NAME_BODY, messageModel.getBody());
        values.put(FeedEntry.COLUMN_NAME_TIMESTAMP, messageModel.getTimestamp());
        values.put(FeedEntry.COLUMN_NAME_CONTACT, gson.toJson(messageModel.getContact()));
        values.put(FeedEntry.COLUMN_NAME_VIDEO_MODEL, gson.toJson(messageModel.getVideoModelClass()));
        values.put(FeedEntry.COLUMN_NAME_LOCATION, messageModel.getLocation());
        values.put(FeedEntry.COLUMN_NAME_IMAGE_MODEL, gson.toJson(messageModel.getImageModel()));
        values.put(FeedEntry.COLUMN_NAME_AUDIO_MODEL, gson.toJson(messageModel.getAudioModelClass()));
        values.put(FeedEntry.COLUMN_NAME_TYPE, messageModel.getType());
        values.put(FeedEntry.COLUMN_NAME_ROOM_ID, roomID);

        // Insert the new row, returning the primary key value of the new row
        return db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_ID + "=?",
                new String[]{messageModel.getId() + ""});
    }

    public void addListMessage(ArrayList<MessageModel> messageList, String roomID) {
        for (MessageModel group : messageList) {
            addMessage(group, roomID);
        }
    }



    public ArrayList<MessageModel> getListMessage(String roomID) {
        ArrayList<MessageModel> messageList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        try {
            //Cursor cursor = db.rawQuery("select * from " + FeedEntry.TABLE_NAME, null);
            Cursor cursor = db.rawQuery("select * from " + FeedEntry.TABLE_NAME + " WHERE " + FeedEntry.COLUMN_NAME_ROOM_ID + "= '" + roomID + "'", null);
            while (cursor.moveToNext()) {
                MessageModel messageModel = new MessageModel();
                messageModel.setId(cursor.getString(0));
                messageModel.setFrom(cursor.getString(1));
                messageModel.setTo(cursor.getString(2));
                messageModel.setBody(cursor.getString(3));
                messageModel.setTimestamp(cursor.getLong(4));
                try {
                    messageModel.setContact(gson.fromJson(String.valueOf(cursor.getString(5)), PhoneContact.class));
                } catch (Exception ex) {

                }
                try {
                    messageModel.setVideoModelClass(gson.fromJson(String.valueOf(cursor.getString(6)), VideoModelClass.class));
                } catch (Exception ex) {

                }
                try {
                    messageModel.setLocation(cursor.getString(7));
                } catch (Exception ex) {

                }
                try {
                    messageModel.setImageModel(gson.fromJson(String.valueOf(cursor.getString(8)), ImageUploadInfo.class));
                } catch (Exception ex) {

                }
                try {
                    messageModel.setAudioModelClass(gson.fromJson(String.valueOf(cursor.getString(9)), AudioModelClass.class));
                } catch (Exception ex) {

                }

                messageModel.setType(cursor.getString(10));
                messageList.add(messageModel);
            }
            cursor.close();
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return messageList;
    }

    public void deleteMessage(String idMessage) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_ID + "=?",
                new String[]{idMessage});
    }

    public void dropDB() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
        instance = null;
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        static final String TABLE_NAME = "MessageTabel";
        static final String COLUMN_ID = "id";
        static final String COLUMN_NAME_FROM = "fromUser";
        static final String COLUMN_NAME_TO = "toUser";
        static final String COLUMN_NAME_BODY = "body";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        static final String COLUMN_NAME_CONTACT = "contact";
        static final String COLUMN_NAME_VIDEO_MODEL = "videoModelClass";
        static final String COLUMN_NAME_LOCATION = "location";
        static final String COLUMN_NAME_IMAGE_MODEL = "imageModel";
        static final String COLUMN_NAME_AUDIO_MODEL = "audioModelClass";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_ROOM_ID = "room_id";

    }

    public static class MessageDBHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "MessageDb.db";

        MessageDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}

