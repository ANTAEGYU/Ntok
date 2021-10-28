package com.ntok.chatmodule.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.ntok.chatmodule.backend.sqlite_database.MessageDB;
import com.ntok.chatmodule.fragment.ChatFragment;
import com.ntok.chatmodule.interfaces.QBChatAttachClickListener;
import com.ntok.chatmodule.interfaces.QBLinkPreviewClickListener;
import com.ntok.chatmodule.interfaces.QBMediaPlayerListener;
import com.ntok.chatmodule.media.AudioController;
import com.ntok.chatmodule.media.MediaController;
import com.ntok.chatmodule.media.SingleMediaManager;
import com.ntok.chatmodule.media.utils.Utils;
import com.ntok.chatmodule.media.view.QBPlaybackControlView;
import com.ntok.chatmodule.model.FirebaseDataSingleton;
import com.ntok.chatmodule.model.FriendUser;
import com.ntok.chatmodule.model.GroupModel;
import com.ntok.chatmodule.model.MessageModel;
import com.ntok.chatmodule.model.PhoneContact;
import com.ntok.chatmodule.model.QBLinkPreview;
import com.ntok.chatmodule.utils.AnimationsUtils;
import com.ntok.chatmodule.utils.Lg;
import com.ntok.chatmodule.utils.LinkUtils;
import com.ntok.chatmodule.utils.LoadImagesUtils;
import com.ntok.chatmodule.utils.LocaFileSaver;
import com.ntok.chatmodule.utils.LocationUtils;
import com.ntok.chatmodule.widget.MessageTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Sonam on 14-05-2018.
 */

public class ListMessageAdapter extends RecyclerView.Adapter<QBMessageViewHolder> {



    protected static final long SHOW_VIEW_ANIMATION_DURATION = 500;
    protected static final int TYPE_TEXT_RIGHT = 1;
    protected static final int TYPE_TEXT_LEFT = 2;
    protected static final int TYPE_ATTACH_RIGHT = 3;
    protected static final int TYPE_ATTACH_LEFT = 4;
    protected static final int TYPE_ATTACH_RIGHT_AUDIO = 5;
    protected static final int TYPE_ATTACH_LEFT_AUDIO = 6;
    protected static final int TYPE_ATTACH_RIGHT_VIDEO = 7;
    protected static final int TYPE_ATTACH_LEFT_VIDEO = 8;
    protected static final int TYPE_ATTACH_LEFT_CONTACT = 9;
    protected static final int TYPE_ATTACH_RIGHT_CONTACT = 10;


    public HashMap<Integer, MessageModel> selected;
    protected QBMessageViewHolder qbViewHolder;
    protected LayoutInflater inflater;
    String TAG = "ListMessageAdapter";
    private Context context;
    private HashMap<String, Bitmap> bitmapAvata;
    private Bitmap bitmapAvataUser;
    private FriendUser friendUSer;
    private GroupModel groupModel;


    private SparseIntArray containerLayoutRes = new SparseIntArray() {
        {
            put(TYPE_TEXT_RIGHT, R.layout.list_item_text_right);
            put(TYPE_TEXT_LEFT, R.layout.list_item_text_left);
            put(TYPE_ATTACH_RIGHT, R.layout.list_item_attach_right);
            put(TYPE_ATTACH_LEFT, R.layout.list_item_attach_left);
            put(TYPE_ATTACH_RIGHT_AUDIO, R.layout.list_item_attach_right_audio);
            put(TYPE_ATTACH_LEFT_AUDIO, R.layout.list_item_attach_left_audio);
            put(TYPE_ATTACH_RIGHT_VIDEO, R.layout.list_item_attach_right_video);
            put(TYPE_ATTACH_LEFT_VIDEO, R.layout.list_item_attach_left_video);
            put(TYPE_ATTACH_LEFT_CONTACT, R.layout.list_item_attach_left_contact);
            put(TYPE_ATTACH_RIGHT_CONTACT, R.layout.list_item_attach_right_contact);
        }
    };


    private QBChatAttachClickListener attachImageClickListener;
    private QBChatAttachClickListener attachLocationClickListener;
    private QBChatAttachClickListener attachAudioClickListener;
    private QBChatAttachClickListener attachVideoClickListener;
    private QBChatAttachClickListener contactClickListener;
    private QBLinkPreviewClickListener linkPreviewClickListener;
    private boolean overrideOnLinkPreviewClick;
    private QBChatAttachClickListener messageTextViewLinkClickListener;
    private boolean overrideOnClick;
    private SingleMediaManager mediaManager;
    private AudioController audioController;
    private MediaControllerEventListener mediaControllerEventListener;
    private Map<QBPlaybackControlView, Integer> playerViewHashMap;
    private int activePlayerViewPosition = -1;
    private ChatFragment chatFragment;


    public ListMessageAdapter(Context context, HashMap<String, Bitmap> bitmapAvata, FriendUser user, GroupModel groupModel, Bitmap bitmapAvataUser, ChatFragment chatFragment) {
        this.context = context;
        this.bitmapAvata = bitmapAvata;
        this.bitmapAvataUser = bitmapAvataUser;
        this.friendUSer = user;
        this.groupModel = groupModel;
        this.chatFragment = chatFragment;
        this.inflater = LayoutInflater.from(context);
        this.selected = new HashMap<>();

//        if(user.onlineStatus != null){
//            if (user.onlineStatus.equalsIgnoreCase("Online")) {
//                ((MainActivity) this.context).onlineStatus.setVisibility(View.VISIBLE);
//                ((MainActivity) this.context).onlineStatus.setText("Online");
//            }else{
//                ((MainActivity) this.context).onlineStatus.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public QBMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // parent.clearAnimation();
        switch (viewType) {
            case TYPE_TEXT_RIGHT:
                qbViewHolder = new TextMessageHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview, R.id.msg_bubble_background, R.id.msg_message_text_view_right);
                return qbViewHolder;
            case TYPE_TEXT_LEFT:
                qbViewHolder = new TextMessageHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview, R.id.msg_bubble_background, R.id.msg_message_text_view_left);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT:
                qbViewHolder = new ImageAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_right);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT:
                qbViewHolder = new ImageAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_left);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT_AUDIO:
                qbViewHolder = new AudioAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_audio_attach, R.id.msg_attach_duration,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_audio_right);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT_AUDIO:
                qbViewHolder = new AudioAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_audio_attach, R.id.msg_attach_duration,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_audio_left);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT_VIDEO:
                qbViewHolder = new VideoAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_video_attach, R.id.msg_progressbar_attach,
                        R.id.msg_attach_duration, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_video_play_icon, R.id.msg_widget_attach_right);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT_VIDEO:
                qbViewHolder = new VideoAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_video_attach, R.id.msg_progressbar_attach,
                        R.id.msg_attach_duration, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_video_play_icon, R.id.msg_widget_attach_left);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT_CONTACT:
                qbViewHolder = new ContactAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.user_image, R.id.user_name, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_audio_left);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT_CONTACT:
                qbViewHolder = new ContactAttachHolder(context, inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.user_image, R.id.user_name, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_widget_attach_audio_right);
                return qbViewHolder;

        }
        return null;
    }


    @Override
    public void onBindViewHolder(QBMessageViewHolder holder, int position) {
        MessageModel chatMessage = getItem(position);
        int valueType = getItemViewType(position);
        setBackgroundColor(holder, valueType);
        switch (valueType) {
            case TYPE_TEXT_RIGHT:
                onBindViewMsgRightHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_TEXT_LEFT:
                onBindViewMsgLeftHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT");
                onBindViewAttachRightHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT");
                onBindViewAttachLeftHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT_AUDIO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT_AUDIO");
                onBindViewAttachRightAudioHolder((AudioAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT_AUDIO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_AUDIO");
                onBindViewAttachLeftAudioHolder((AudioAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT_VIDEO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT_VIDEO");
                onBindViewAttachRightVideoHolder((VideoAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT_VIDEO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_VIDEO");
                onBindViewAttachLeftVideoHolder((VideoAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT_CONTACT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_CONTACT");
                onBindViewAttachLeftContactHolder((ContactAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT_CONTACT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_CONTACT");
                onBindViewAttachLeftContactHolder((ContactAttachHolder) holder, chatMessage, position);
                break;
            default:
                onBindViewCustomHolder((QBMessageViewHolder) holder, chatMessage, position);
                Log.i(TAG, "onBindViewHolder TYPE_ATTACHMENT_CUSTOM");
                break;
        }

    }

    public MessageModel getItem(int position) {
        return FirebaseDataSingleton.getInstance(context).getMessageListHashMap().get(friendUSer != null ? friendUSer.room_id : groupModel.getRoom_id()).get(position);
    }

    public void setBackgroundColor(QBMessageViewHolder holder, int type) {
        switch (type) {
            case TYPE_TEXT_RIGHT:
            case TYPE_ATTACH_RIGHT:
            case TYPE_ATTACH_RIGHT_AUDIO:
            case TYPE_ATTACH_RIGHT_VIDEO:
            case TYPE_ATTACH_RIGHT_CONTACT:
                holder.bubbleFrame.setBackground(Utils.tintDrawable(holder.bubbleFrame.getBackground(), ColorStateList.valueOf(context.getResources().getColor(R.color.receview_color))));
                break;
            case TYPE_TEXT_LEFT:
            case TYPE_ATTACH_LEFT:
            case TYPE_ATTACH_LEFT_AUDIO:
            case TYPE_ATTACH_LEFT_VIDEO:
            case TYPE_ATTACH_LEFT_CONTACT:
                holder.bubbleFrame.setBackground(Utils.tintDrawable(holder.bubbleFrame.getBackground(), ColorStateList.valueOf(context.getResources().getColor(R.color.white))));
                break;
        }

    }

    public void setAnimation(QBMessageViewHolder holder, int position, int type) {
        switch (type) {
            case TYPE_TEXT_RIGHT:
            case TYPE_ATTACH_RIGHT:
            case TYPE_ATTACH_RIGHT_AUDIO:
            case TYPE_ATTACH_RIGHT_VIDEO:
            case TYPE_ATTACH_RIGHT_CONTACT:
//                setAnimation(holder.itemView, position, true);
                break;
            case TYPE_TEXT_LEFT:
            case TYPE_ATTACH_LEFT:
            case TYPE_ATTACH_LEFT_AUDIO:
            case TYPE_ATTACH_LEFT_VIDEO:
            case TYPE_ATTACH_LEFT_CONTACT:
//                setAnimation(holder.itemView, position, false);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        MessageModel chatMessage = getItem(position);

        if (hasAttachments(chatMessage)) {
            if (MessageModel.IMAGE_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT : TYPE_ATTACH_RIGHT;
            } else if (MessageModel.LOCATION_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                return getLocationView(chatMessage);
            } else if (MessageModel.AUDIO_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_AUDIO : TYPE_ATTACH_RIGHT_AUDIO;
            } else if (MessageModel.VIDEO_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_VIDEO : TYPE_ATTACH_RIGHT_VIDEO;
            } else if (MessageModel.Contact_Type.equalsIgnoreCase(chatMessage.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_CONTACT : TYPE_ATTACH_RIGHT_CONTACT;
            }

        } else {
            return isIncoming(chatMessage) ? TYPE_TEXT_LEFT : TYPE_TEXT_RIGHT;
        }
        return customViewType(position);
    }

    protected int getLocationView(MessageModel chatMessage) {
        return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT : TYPE_ATTACH_RIGHT;
    }

    protected int customViewType(int position) {
        return -1;
    }

    protected boolean isIncoming(MessageModel chatMessage) {
        return chatMessage.getFrom() != null && !chatMessage.getFrom().equals(FirebaseDataSingleton.getInstance(context).getUser().phone_number);
    }

    protected boolean hasAttachments(MessageModel chatMessage) {
        // Collection<MessageModel> attachments = chatMessage.getAttachments();
        //  return attachments != null && !attachments.isEmpty();
        return chatMessage.getImageModel() != null || chatMessage.getLocation() != null || chatMessage.getAudioModelClass() != null || chatMessage.getVideoModelClass() != null || chatMessage.getContact() != null;
    }

    @Override
    public int getItemCount() {
        try {
            if (FirebaseDataSingleton.getInstance(context).getMessageListHashMap().get(friendUSer != null ? friendUSer.room_id : groupModel.getRoom_id()) == null)
                return 0;
            return FirebaseDataSingleton.getInstance(context).getMessageListHashMap().get(friendUSer != null ? friendUSer.room_id : groupModel.getRoom_id()).size();
        } catch (Exception ex) {
            Lg.printStackTrace(ex);
        }
        return 0;
    }

    protected void onBindViewCustomHolder(QBMessageViewHolder holder, MessageModel chatMessage, int position) {
    }

    protected void onBindViewAttachRightHolder(ImageAttachHolder holder, MessageModel chatMessage, int position) {
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        setDateSentAttach(holder, chatMessage);
        displayAttachment(holder, position);


        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachLeftHolder(ImageAttachHolder holder, MessageModel chatMessage, int position) {
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        setDateSentAttach(holder, chatMessage);
        displayAttachment(holder, position);
        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachRightAudioHolder(AudioAttachHolder holder, MessageModel chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentAudio(holder, position);
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
    }

    protected void setItemAttachClickListener(QBChatAttachClickListener listener, QBMessageViewHolder holder, MessageModel MessageModel, int position) {
        if (listener != null) {
            holder.bubbleFrame.setOnClickListener(new QBItemClickListenerFilter(listener, MessageModel, position));
            holder.bubbleFrame.setOnLongClickListener(new QBItemClickListenerFilter(listener, MessageModel, position));
        }
    }

    protected void onBindViewAttachLeftAudioHolder(AudioAttachHolder holder, MessageModel chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentAudio(holder, position);
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
    }

    protected void onBindViewAttachRightVideoHolder(VideoAttachHolder holder, MessageModel chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentVideo(holder, position);
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachLeftVideoHolder(VideoAttachHolder holder, MessageModel chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentVideo(holder, position);
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachLeftContactHolder(ContactAttachHolder holder, MessageModel chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayContactInfo(chatMessage.getContact(), holder);
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, MessageModel chatMessage, int position) {
        fillTextMessageHolder(holder, chatMessage, position, true);
    }

    protected void onBindViewMsgRightHolder(TextMessageHolder holder, MessageModel chatMessage, int position) {
        fillTextMessageHolder(holder, chatMessage, position, false);
    }

    protected void fillTextMessageHolder(TextMessageHolder holder, MessageModel chatMessage, int position, boolean isLeftMessage) {
        if (selected.containsKey(position)) {
            holder.topLayout.setEnabled(true);
        } else {
            holder.topLayout.setEnabled(false);
        }
        holder.linkPreviewLayout.setVisibility(View.GONE);
        holder.messageTextView.setText(chatMessage.getBody());
        if (FirebaseDataSingleton.getInstance(context).getUser().phone_number.equals(chatMessage.getFrom())) {
            holder.timeTextMessageTextView.setText(getDate(chatMessage.getTimestamp()));
        } else {
            holder.timeTextMessageTextView.setText((FirebaseDataSingleton.getInstance(context).getFriendFromUserID(chatMessage.getFrom()) != null) ?  getDate(chatMessage.getTimestamp()) : getDate(chatMessage.getTimestamp()));

        }
        setMessageTextViewLinkClickListener(holder, position);
        final List<String> urlsList = LinkUtils.extractUrls(chatMessage.getBody());
        if (!urlsList.isEmpty()) {
            holder.messageTextView.setMaxWidth((int) context.getResources().getDimension(R.dimen.link_preview_width));
            holder.linkPreviewLayout.setTag(chatMessage.getBody());

            if (isLeftMessage) {
                processLinksFromLeftMessage(holder, urlsList, position);
            } else {
                processLinksFromRightMessage(holder, urlsList, position);
            }
        } else {
            holder.messageTextView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
        }
    }

    protected void processLinksFromLeftMessage(TextMessageHolder holder, List<String> urlsList, int position) {
        processLinksFromMessage(holder, urlsList, position);
    }

    protected void processLinksFromRightMessage(TextMessageHolder holder, List<String> urlsList, int position) {
        processLinksFromMessage(holder, urlsList, position);
    }

    protected void processLinksFromMessage(TextMessageHolder holder, final List<String> urlsList, final int position) {
        final String firstLink = LinkUtils.getLinkWithProtocol(urlsList.get(0));
        String linkPreviewViewIdentifier = (String) holder.linkPreviewLayout.getTag();

//        QBLinkPreviewCashService.getInstance().getLinkPreview(firstLink,
//                null,
//                false,
//                new LoadLinkPreviewHandler(holder, urlsList, position, linkPreviewViewIdentifier));
    }

    protected void fillLinkPreviewLayout(final View linkPreviewLayout, final QBLinkPreview linkPreview, String link) {
        TextView linkTitle = (TextView) linkPreviewLayout.findViewById(R.id.link_preview_title);
        TextView linkDescription = (TextView) linkPreviewLayout.findViewById(R.id.link_preview_description);
        ImageView linkImage = (ImageView) linkPreviewLayout.findViewById(R.id.link_preview_image);
        ImageView linkHostIcon = (ImageView) linkPreviewLayout.findViewById(R.id.link_host_icon);
        TextView linkHost = (TextView) linkPreviewLayout.findViewById(R.id.link_host_url);

        linkTitle.setText(linkPreview.getTitle());

        if (!TextUtils.isEmpty(linkPreview.getDescription())) {
            linkDescription.setText(linkPreview.getDescription());
            linkDescription.setVisibility(View.VISIBLE);
        } else {
            linkDescription.setVisibility(View.GONE);
        }

        linkHost.setText(LinkUtils.getHostFromLink(link));

        linkImage.setVisibility(View.GONE);
        if (linkPreview.getImage() != null && linkPreview.getImage().getImageUrl() != null && LoadImagesUtils.isPossibleToDisplayImage(context)) {
            loadImageOrHideView(LinkUtils.prepareCorrectLink(linkPreview.getImage().getImageUrl()), linkImage);
        }

        linkHostIcon.setVisibility(View.GONE);
        if (LinkUtils.getLinkForHostIcon(link) != null && LoadImagesUtils.isPossibleToDisplayImage(context)) {
            loadImageOrHideView(LinkUtils.getLinkForHostIcon(link), linkHostIcon);
        }
    }

    protected void loadImageOrHideView(final String imageUrl, final ImageView imageView) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        AnimationsUtils.showView(imageView, SHOW_VIEW_ANIMATION_DURATION);
                        return false;
                    }
                })
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .into(imageView);
    }

    public void setMessageTextViewLinkClickListener(TextMessageHolder holder, final int position) {
        if (messageTextViewLinkClickListener != null) {
            holder.msg_bubble_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageTextViewLinkClickListener.onLinkClicked(getItem(position), position);
                }
            });
            holder.msg_bubble_background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (selected.isEmpty() && getItem(position).getType().equalsIgnoreCase(MessageModel.TEXT_TYPE)) {
                        ((MainActivity) context).iconCopy.setVisibility(View.VISIBLE);
                    } else {
                        ((MainActivity) context).iconCopy.setVisibility(View.GONE);
                    }
                    selected.put(position, getItem(position));
                    ((MainActivity) context).iconForward.setVisibility(View.VISIBLE);



                    ((MainActivity) context).iconDelete.setVisibility(View.VISIBLE);
                    ((MainActivity) context).title.setVisibility(View.GONE);
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }

    public void setMessageTextViewLinkClickListener(QBChatAttachClickListener textViewLinkClickListener, boolean overrideOnClick) {
        this.messageTextViewLinkClickListener = textViewLinkClickListener;
        this.overrideOnClick = overrideOnClick;
    }

    public void setMediaPlayerListener(QBMediaPlayerListener mediaPlayerListener) {
        getMediaManagerInstance().addListener(mediaPlayerListener);
    }

    private QBChatAttachClickListener getAttachListenerByType(int position) {
        MessageModel attachment = getQBAttach(position);

        if (MessageModel.IMAGE_TYPE.equalsIgnoreCase(attachment.getType()) ||
                MessageModel.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachImageClickListener;
        } else if (MessageModel.LOCATION_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachLocationClickListener;
        } else if (MessageModel.AUDIO_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachAudioClickListener;
        } else if (MessageModel.VIDEO_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachVideoClickListener;
        } else if (MessageModel.Contact_Type.equalsIgnoreCase(attachment.getType())) {
            return contactClickListener;
        }
        return null;
    }

    public void setAttachImageClickListener(QBChatAttachClickListener clickListener) {
        attachImageClickListener = clickListener;
    }

    public void setAttachContactClickListener(QBChatAttachClickListener clickListener) {
        contactClickListener = clickListener;
    }

    public void setAttachLocationClickListener(QBChatAttachClickListener clickListener) {
        attachLocationClickListener = clickListener;
    }

    public void setLinkPreviewClickListener(QBLinkPreviewClickListener linkPreviewClickListener, boolean overrideOnLinkpreviewClick) {
        this.linkPreviewClickListener = linkPreviewClickListener;
        this.overrideOnLinkPreviewClick = overrideOnLinkpreviewClick;
    }

    public void setAttachAudioClickListener(QBChatAttachClickListener clickListener) {
        this.attachAudioClickListener = clickListener;
    }

    public void setAttachVideoClickListener(QBChatAttachClickListener clickListener) {
        this.attachVideoClickListener = clickListener;
    }

    public void removeAttachImageClickListener(QBChatAttachClickListener clickListener) {
        attachImageClickListener = null;
    }

    public void removeLocationImageClickListener(QBChatAttachClickListener clickListener) {
        attachLocationClickListener = null;
    }

    public void removeAttachAudioClickListener(QBChatAttachClickListener clickListener) {
        attachAudioClickListener = null;
    }

    public void removeAttachVideoClickListener(QBChatAttachClickListener clickListener) {
        attachVideoClickListener = null;
    }

    /**
     * Removes listener for handling onLinkClick event on message text.
     */
    public void removeMessageTextViewLinkClickListener() {
        this.messageTextViewLinkClickListener = null;
        this.overrideOnClick = false;
    }

    protected void setDateSentAttach(BaseAttachHolder holder, MessageModel chatMessage) {
        // holder.attachTextTime.setText(getDate(chatMessage.getTimestamp()));

        if (FirebaseDataSingleton.getInstance(context).getUser().phone_number.equals(chatMessage.getFrom())) {
            holder.attachTextTime.setText(getDate(chatMessage.getTimestamp()));
        } else {
            holder.attachTextTime.setText((FirebaseDataSingleton.getInstance(context).getFriendFromUserID(chatMessage.getFrom()) != null) ? /*FirebaseDataSingleton.getInstance(context).getFriendFromUserID(chatMessage.getFrom()).username + "  " +*/ getDate(chatMessage.getTimestamp()) : getDate(chatMessage.getTimestamp()));

        }
    }

    /**
     * @return string in "Hours:Minutes" format, i.e. <b>10:15</b>
     */
    protected String getDate(long seconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(seconds));
    }

    /**
     * displayAttachment must be implemented in derived class
     */
    protected void displayAttachment(QBMessageViewHolder holder, int position) {
        MessageModel attachment = getQBAttach(position);

        if (MessageModel.IMAGE_TYPE.equalsIgnoreCase(attachment.getType()) ||
                MessageModel.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            showPhotoAttach(holder, position);
            if (attachment.getImageModel().getLocalImageURI() == null) {
                saveFile(attachment.getImageModel().getImageURL(), attachment.getType(), attachment);
            }
        } else if (MessageModel.LOCATION_TYPE.equalsIgnoreCase(attachment.getType())) {
            showLocationAttach(holder, position);
        }
    }

    protected void displayAttachmentAudio(QBMessageViewHolder holder, int position) {
        MessageModel attachment = getQBAttach(position);

        Uri uri = getUriFromAttach(attachment);
        long duration = attachment.getAudioModelClass().getTime();
        setDurationAudio(duration, holder);
        QBPlaybackControlView playerView = ((AudioAttachHolder) holder).playerView;

        showAudioView(playerView, uri, position);
        setItemAttachAudioClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position, playerView);
        if (attachment.getAudioModelClass().getLocalAudioLink() == null)
            saveFile(attachment.getAudioModelClass().getAudioLink(), attachment.getType(), attachment);
    }

    protected void setItemAttachAudioClickListener(QBChatAttachClickListener listener, QBMessageViewHolder holder, MessageModel qbAttachment, int position,
                                                   QBPlaybackControlView controlView) {
        holder.bubbleFrame.setOnClickListener(new QBItemAudioClickListener(listener, qbAttachment, position, controlView));
        holder.bubbleFrame.setOnLongClickListener(new QBItemAudioClickListener(listener, qbAttachment, position, controlView));
    }

    protected void displayAttachmentVideo(final QBMessageViewHolder holder, final int position) {
        MessageModel attachment = getQBAttach(position);
        long duration = attachment.getVideoModelClass().getTime();
        setDurationVideo(duration, holder);
        final String url = getQBAttach(position).getVideoModelClass().getVideoThumb();
        showVideoThumbnail(holder, url, position);
        if (attachment.getVideoModelClass().getLocalVideoLink() == null)
            saveFile(attachment.getVideoModelClass().getVideoLink(), attachment.getType(), attachment);

    }

    protected void setDurationVideo(long duration, QBMessageViewHolder holder) {
        ((VideoAttachHolder) holder).durationView.setText(Utils.formatTimeSecondsToMinutes(duration));
    }

    protected void displayContactInfo(PhoneContact contact, QBMessageViewHolder holder) {
        ((ContactAttachHolder) holder).userNumber.setText(contact.getName());
        displayAvatarImage(contact.getPhotoUri(), ((ContactAttachHolder) holder).userImage);
    }

    protected Uri getUriFromAttach(MessageModel attachment) {
        return Utils.getUriFromAttachPublicUrl(attachment);
    }

    protected void setDurationAudio(long duration, QBMessageViewHolder holder) {
        ((AudioAttachHolder) holder).durationView.setText(Utils.formatTimeSecondsToMinutes(duration));
    }

    private void showAudioView(QBPlaybackControlView playerView, Uri uri, int position) {
        initPlayerView(playerView, uri, position);
        if (isCurrentViewActive(position)) {
            Log.d(TAG, "showAudioView isCurrentViewActive");
            playerView.restoreState(getMediaManagerInstance().getExoPlayer());
        }
    }

    private void initPlayerView(QBPlaybackControlView playerView, Uri uri, int position) {
        playerView.releaseView();
        setViewPosition(playerView, position);
        playerView.initView(getAudioControllerInstance(), uri);
    }

    private boolean isCurrentViewActive(int position) {
        return activePlayerViewPosition == position;
    }

    private void setPlayerViewActivePosition(int activeViewPosition) {
        this.activePlayerViewPosition = activeViewPosition;
    }

    private void setViewPosition(QBPlaybackControlView view, int position) {
        if (playerViewHashMap == null) {
            playerViewHashMap = new WeakHashMap<>();
        }
        playerViewHashMap.put(view, position);
    }

    private int getPlayerViewPosition(QBPlaybackControlView view) {
        Integer position = playerViewHashMap.get(view);
        return position == null ? activePlayerViewPosition : position;
    }

    public void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder_user)
                .dontAnimate()
                .into(imageView);
    }

    private void showVideoThumbnail(final QBMessageViewHolder holder, String url, int position) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(url)
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((VideoAttachHolder) holder).attachImageView);
        ((VideoAttachHolder) holder).playIcon.setVisibility(View.VISIBLE);
    }

    public SingleMediaManager getMediaManagerInstance() {
        return mediaManager = mediaManager == null ? new SingleMediaManager(context) : mediaManager;
    }

    private AudioController getAudioControllerInstance() {
        return audioController = audioController == null ? new AudioController(getMediaManagerInstance(), getMediaControllerEventListenerInstance())
                : audioController;
    }

    private MediaControllerEventListener getMediaControllerEventListenerInstance() {
        return mediaControllerEventListener = mediaControllerEventListener == null ? new MediaControllerEventListener() : mediaControllerEventListener;
    }

    protected void showPhotoAttach(QBMessageViewHolder holder, int position) {
        String imageUrl = getImageUrl(position);
        showImageByURL(holder, imageUrl, position);
    }

    protected void showLocationAttach(QBMessageViewHolder holder, int position) {
        String locationUrl = getLocationUrl(position);
        showImageByURL(holder, locationUrl, position);
    }

    public String getImageUrl(int position) {
        MessageModel attachment = getQBAttach(position);
        if (attachment.getImageModel().getLocalImageURI() == null)
            return attachment.getImageModel().getImageURL();
        else
            return attachment.getImageModel().getLocalImageURI();
    }

    public String getLocationUrl(int position) {
        MessageModel attachment = getQBAttach(position);

        LocationUtils.BuilderParams params = LocationUtils.defaultUrlLocationParams(context);

        return LocationUtils.getRemoteUri(attachment.getLocation(), params);
    }

    protected MessageModel getQBAttach(int position) {
        MessageModel chatMessage = getItem(position);
        // return chatMessage.getAttachments().iterator().next();
        return chatMessage;
    }

    private void showImageByURL(QBMessageViewHolder holder, String url, int position) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(url)
                .listener(this.<String, GlideDrawable>getRequestListener(holder, position))
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((BaseImageAttachHolder) holder).attachImageView);
    }

    protected RequestListener getRequestListener(QBMessageViewHolder holder, int position) {
        return new ImageLoadListener<>((BaseImageAttachHolder) holder);
    }

    public String obtainAvatarUrl(int valueType, MessageModel chatMessage) {
        try {
            if (friendUSer != null) {
                return FirebaseDataSingleton.getInstance(context).getUser().phone_number.equals(chatMessage.getFrom()) ?
                        FirebaseDataSingleton.getInstance(context).getUser().userImage : friendUSer.userImage;

            } else if (FirebaseDataSingleton.getInstance(context).getUser().phone_number.equals(chatMessage.getFrom())) {
                return FirebaseDataSingleton.getInstance(context).getUser().userImage;
            } else if (FirebaseDataSingleton.getInstance(context).getFriendFromUserID(chatMessage.getFrom()) != null) {
                return FirebaseDataSingleton.getInstance(context).getFriendFromUserID(chatMessage.getFrom()).userImage;
            }
        } catch (NullPointerException ex) {
            return null;
        }
        return null;
    }

    public void saveFile(String imageNameToSave, String type, final MessageModel chatMessage) {
        LocaFileSaver localImageSaver = new LocaFileSaver(context, imageNameToSave, imageNameToSave, new LocaFileSaver.SaveCompletionInterface() {
            @Override
            public void onSaved(boolean result, String savedImagePath) {

                if (result) {
                    if (MessageModel.IMAGE_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                        if (chatMessage.getImageModel() != null) {
                            chatMessage.getImageModel().setLocalImageURI(savedImagePath);
                        }
                    } else if (MessageModel.AUDIO_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                        if (chatMessage.getAudioModelClass() != null) {
                            chatMessage.getAudioModelClass().setLocalAudioLink(savedImagePath);
                        }
                    } else if (MessageModel.VIDEO_TYPE.equalsIgnoreCase(chatMessage.getType())) {
                        if (chatMessage.getVideoModelClass() != null) {
                            chatMessage.getVideoModelClass().setLocalVideoLink(savedImagePath);
                        }
                    }
                    MessageDB.getInstance(context).updateMessage(chatMessage, friendUSer != null ? friendUSer.room_id : groupModel.getRoom_id());
                    // refresh gallery
                    try {
                        MediaScannerConnection.scanFile(context, new String[]{savedImagePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
//                                Toast.makeText(context, "onScanCompleted!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                    }

                } else {
                }
            }

        }, type);
        localImageSaver.execute();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull QBMessageViewHolder holder) {
        //  holder.itemView.clearAnimation();
    }

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position, boolean isRight) {
        // If the bound view wasn't previously displayed on screen, it's animated
//        if (position < lastPosition)
//        {
        Animation animation;
        if (isRight)
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        else
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
        //  }
    }

    private class QBItemAudioClickListener extends QBItemClickListenerFilter {
        private QBPlaybackControlView controlView;

        QBItemAudioClickListener(QBChatAttachClickListener chatAttachClickListener, MessageModel attachment, int position, QBPlaybackControlView controlView) {
            super(chatAttachClickListener, attachment, position);
            this.controlView = controlView;
        }

        @Override
        public void onClick(View view) {
            if (chatAttachClickListener != null) {
                super.onClick(view);
            }
            controlView.clickIconPlayPauseView();
        }
    }

    private class QBItemClickListenerFilter implements View.OnClickListener, View.OnLongClickListener {
        protected int position;
        protected MessageModel attachment;
        protected QBChatAttachClickListener chatAttachClickListener;

        QBItemClickListenerFilter(QBChatAttachClickListener qbChatAttachClickListener, MessageModel attachment, int position) {
            this.position = position;
            this.attachment = attachment;
            this.chatAttachClickListener = qbChatAttachClickListener;
        }

        @Override
        public void onClick(View view) {
            chatAttachClickListener.onLinkClicked(attachment, position);
        }

        @Override
        public boolean onLongClick(View v) {
            if (selected.isEmpty() && getItem(position).getType().equalsIgnoreCase(MessageModel.TEXT_TYPE)) {
                ((MainActivity) context).iconCopy.setVisibility(View.VISIBLE);
            } else {
                ((MainActivity) context).iconCopy.setVisibility(View.GONE);
            }
            selected.put(position, attachment);
            ((MainActivity) context).iconDelete.setVisibility(View.VISIBLE);
            ((MainActivity) context).iconForward.setVisibility(View.VISIBLE);
            ((MainActivity) context).title.setVisibility(View.GONE);
            notifyDataSetChanged();
            return false;
        }
    }

    private class MediaControllerEventListener implements MediaController.EventMediaController {

        @Override
        public void onPlayerInViewInit(QBPlaybackControlView view) {
            setPlayerViewActivePosition(getPlayerViewPosition(view));
        }
    }

}

class ImageLoadListener<M, P> implements RequestListener<M, P> {
    private BaseImageAttachHolder holder;

    protected ImageLoadListener(BaseImageAttachHolder holder) {
        this.holder = holder;
        holder.attachmentProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onException(Exception e, M model, Target<P> target, boolean isFirstResource) {
        Log.e("", "ImageLoadListener Exception= " + e);
        holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.attachmentProgressBar.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(P resource, M model, Target<P> target, boolean isFromMemoryCache, boolean isFirstResource) {
        holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.attachmentProgressBar.setVisibility(View.GONE);
        return false;
    }
}

abstract class QBMessageViewHolder extends RecyclerView.ViewHolder {
//    public ImageView avatar;
    public View bubbleFrame;

    public QBMessageViewHolder(Context context, View itemView) {
        super(itemView);
//        avatar = (ImageView) itemView.findViewById(R.id.msg_image_avatar);
        bubbleFrame = itemView.findViewById(R.id.msg_bubble_background);
    }
}

class TextMessageHolder extends QBMessageViewHolder {
    public View linkPreviewLayout;
    public TextView messageTextView;
    public TextView timeTextMessageTextView;
    public View msg_bubble_background;
    public MessageTextView topLayout;

    public TextMessageHolder(Context context, View itemView, @IdRes int msgId, @IdRes int timeId, @IdRes int linkPreviewLayoutId, @IdRes int backgroudID, @IdRes int backgroudId) {
        super(context, itemView);
        messageTextView = (TextView) itemView.findViewById(msgId);
        timeTextMessageTextView = (TextView) itemView.findViewById(timeId);
        linkPreviewLayout = itemView.findViewById(linkPreviewLayoutId);
        msg_bubble_background = itemView.findViewById(backgroudID);
        topLayout = itemView.findViewById(backgroudId);
    }
}

class BaseAttachHolder extends QBMessageViewHolder {
    public TextView attachTextTime;
    public ImageView signAttachView;

    public BaseAttachHolder(Context context, View itemView, @IdRes int timeId, @IdRes int signViewId) {
        super(context, itemView);
        attachTextTime = (TextView) itemView.findViewById(timeId);
        signAttachView = (ImageView) itemView.findViewById(signViewId);
    }
}

class BaseImageAttachHolder extends BaseAttachHolder {
    public ImageView attachImageView;
    public ProgressBar attachmentProgressBar;
    public LinearLayout topLayout;

    public BaseImageAttachHolder(Context context, View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId, @IdRes int topLayoutID) {
        super(context, itemView, timeId, signId);
        attachImageView = itemView.findViewById(attachId);
        attachmentProgressBar = itemView.findViewById(progressBarId);
        topLayout = itemView.findViewById(topLayoutID);
    }
}

class ImageAttachHolder extends BaseImageAttachHolder {

    public ImageAttachHolder(Context context, View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId, @IdRes int topLayoutID) {
        super(context, itemView, attachId, progressBarId, timeId, signId, topLayoutID);
    }
}

class AudioAttachHolder extends BaseAttachHolder {
    public QBPlaybackControlView playerView;
    public TextView durationView;
    public LinearLayout topLayout;

    public AudioAttachHolder(Context context, View itemView, @IdRes int attachId, @IdRes int durationId, @IdRes int timeId, @IdRes int signId, @IdRes int topLayoutId) {
        super(context, itemView, timeId, signId);
        playerView = itemView.findViewById(attachId);
        durationView = itemView.findViewById(durationId);
        topLayout = itemView.findViewById(topLayoutId);
    }
}

class VideoAttachHolder extends BaseImageAttachHolder {
    public ImageView playIcon;
    public TextView durationView;

    public VideoAttachHolder(Context context, View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int durationId, @IdRes int timeId, @IdRes int signId, @IdRes int playIconId, @IdRes int topLayoutID) {
        super(context, itemView, attachId, progressBarId, timeId, signId, topLayoutID);
        playIcon = (ImageView) itemView.findViewById(playIconId);
        durationView = (TextView) itemView.findViewById(durationId);
    }
}

class ContactAttachHolder extends BaseAttachHolder {
    public ImageView userImage;
    public TextView userNumber;
    public LinearLayout topLayout;

    public ContactAttachHolder(Context context, View itemView, @IdRes int imageView, @IdRes int Number, @IdRes int timeId, @IdRes int signId, @IdRes int backgroundID) {
        super(context, itemView, timeId, signId);
        userImage = itemView.findViewById(imageView);
        userNumber = itemView.findViewById(Number);
        topLayout = itemView.findViewById(backgroundID);
    }


}


