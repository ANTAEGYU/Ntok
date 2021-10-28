package com.ntok.chatmodule.interfaces;


import com.ntok.chatmodule.model.MessageModel;

/**
 * Created by roman on 2/1/17.
 */

public interface QBChatAttachClickListener {

    void onLinkClicked(MessageModel attachment, int positionInAdapter);
}
