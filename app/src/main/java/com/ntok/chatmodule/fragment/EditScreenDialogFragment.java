package com.ntok.chatmodule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.utils.Lg;

/**
 * Created by Sonam on 22-05-2018.
 */

public class EditScreenDialogFragment extends Fragment implements View.OnClickListener {
    private EditText etEnterValue;
    private Button btnOk;
    private Button btnCancel;
    private SetValueInterface setValueInterface;

    public void setSetValueInterface(SetValueInterface setValueInterface) {
        this.setValueInterface = setValueInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_screen_layout, container, false);
        initViews(rootView);
        initToolBar();
        return rootView;
    }

    public void initViews(View rootView) {
        etEnterValue = rootView.findViewById(R.id.edit_text);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnOk = rootView.findViewById(R.id.btnOk);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            setValueInterface.setValue(null);
            getActivity().onBackPressed();
        } else if (v == btnOk) {
            setValueInterface.setValue(etEnterValue.getText().toString());
            getActivity().onBackPressed();
        }
    }

    public void initToolBar() {
        try {
            ((MainActivity) getActivity()).backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).userImage.setVisibility(View.GONE);
            ((MainActivity) getActivity()).addUser.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconDelete.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconForward.setVisibility(View.GONE);
            ((MainActivity) getActivity()).iconCopy.setVisibility(View.GONE);
            ((MainActivity) getActivity()).title.setText("Enter Details");
            ((MainActivity)getActivity()).title.setVisibility(View.VISIBLE);
        } catch (NullPointerException ex) {
            Lg.printStackTrace(ex);
        }
    }

    public interface SetValueInterface {
        public void setValue(String value);
    }
}
