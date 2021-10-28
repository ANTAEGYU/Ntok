package com.ntok.chatmodule.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ntok.chatmodule.R;


public class CopyPasteOptionsClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public CopyPasteOptionsInterface copyPasteOptionsInterface;
    public Dialog d;
    public TextView txtCopy;
    public TextView txtForward;
    public TextView txtDelete;

    public CopyPasteOptionsClass(Activity a, CopyPasteOptionsInterface copyPasteOptionsInterface) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.copyPasteOptionsInterface = copyPasteOptionsInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.copy_paste_dialog);
        txtCopy = findViewById(R.id.txt_copy);
        txtDelete = findViewById(R.id.txt_delete);
        txtForward = findViewById(R.id.txt_forward);
        txtCopy.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtForward.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_delete:
                copyPasteOptionsInterface.delete();
                break;
            case R.id.txt_forward:
                copyPasteOptionsInterface.forward();
                break;

            default:
                break;
        }
        dismiss();
    }

    public interface CopyPasteOptionsInterface{
        public void forward();
        public void delete();
    }
}
