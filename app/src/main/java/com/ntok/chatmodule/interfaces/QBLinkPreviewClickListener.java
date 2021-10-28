package com.ntok.chatmodule.interfaces;


import com.ntok.chatmodule.model.MessageModel;

public interface QBLinkPreviewClickListener {

    void onLinkPreviewClicked(String link, MessageModel linkPreview, int position);

    void onLinkPreviewLongClicked(String link, MessageModel linkPreview, int position);
}
