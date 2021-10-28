package com.ntok.chatmodule.manager;

//public class ChatManager   {
//
//    private static ChatManager chatManager;
//    private long currentSendMessageOrderNo = -1;
//    private ChatFragment chatFragment;
//
//
//    public void setChatActivity(ChatFragment chatFragment) {
//        this.chatFragment = chatFragment;
//    }
//
//
//    /**
//     * Get the instance of the Chat Manager to access
//     * method of sending and receiving messages
//     *
//     * @return
//     */
//
//    public static ChatManager getInstance() {
//        if (chatManager == null) {
//            chatManager = new ChatManager();
//        }
//        return chatManager;
//    }
//
//    /**
//     * Method used to send message to local database
//     * @param chatMessage
//     */
////    public void sendMessageToLocal(ChatMessage chatMessage) {
////        long maxLocalId = RealamDatabase.getInstance().fetchMaxLocalId();
////        ChatMessage message = new ChatMessage();
////        message.localId = maxLocalId;
////        message.setStatusIsDelivered(false);
////        RealamDatabase.getInstance().addLocalMessageToDb(chatMessage);
//////        if(chatFragment != null){
//////            chatFragment.addMessage(chatMessage);
//////        }else{
//////            // add local push notification here
//////        }
////
////        fetchMessageToSend();
////    }
////
////
////    public void fetchMessageToSend() {
////
////        ChatMessage localChatMessage = RealamDatabase.getInstance().fetchMessagesToBeSent();
////        if (localChatMessage != null)
////            sendMessageToServer(localChatMessage);
////    }
//
//
//    /**
//     * Once the message is fetched from local table, send it to server
//     *
//     * @param message
//     */
//    private void sendMessageToServer(ChatMessage message) {
//
//        if (currentSendMessageOrderNo == message.getLocalId())
//            return;
//        currentSendMessageOrderNo = message.getLocalId();
//
//
////        add fire base send message code here
//    }
//
//
//
//}
