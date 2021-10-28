package com.ntok.chatmodule.utils;

import com.ntok.chatmodule.model.QBLinkPreview;

import java.util.HashMap;
import java.util.Map;

public class QBLinkPreviewCashService {

    private static QBLinkPreviewCashService INSTANCE;

    private Map<String, QBLinkPreview> linkPreviewsHolder = new HashMap<>();

    private QBLinkPreviewCashService() {
    }

    public static QBLinkPreviewCashService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QBLinkPreviewCashService();
        }

        return INSTANCE;
    }

//    public void getLinkPreview(final String url, String token, boolean forceLoad, final QBEntityCallback<QBLinkPreview> callback) {
//
//        if (!forceLoad) {
//            QBLinkPreview linkPreview = linkPreviewsHolder.get(url);
//            if (linkPreview != null && callback != null){
//                callback.onSuccess(linkPreview, new Bundle());
//            } else {
//                getLinkPreview(url, token, true, callback);
//            }
//
//            return;
//        }
//
//        QBRestRequestExecutor.getLinkPreview(url, token).performAsync(new QBEntityCallback<QBLinkPreview>() {
//            @Override
//            public void onSuccess(QBLinkPreview linkPreview, Bundle bundle) {
//                linkPreviewsHolder.put(url, linkPreview);
//                if (callback != null) {
//                    callback.onSuccess(linkPreview, bundle);
//                }
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//                callback.onError(e);
//            }
//        });
//    }
//
//    public QBLinkPreview getLinkPreviewSync(final String url, String token, boolean forceLoad) throws QBResponseException {
//        QBLinkPreview result;
//
//        if (!forceLoad) {
//            result = linkPreviewsHolder.get(url);
//            return result == null ? getLinkPreviewSync(url, token, true) : result;
//        }
//
//        result = QBRestRequestExecutor.getLinkPreview(url, token).perform();
//        linkPreviewsHolder.put(url, result);
//
//        return result;
//    }
}
