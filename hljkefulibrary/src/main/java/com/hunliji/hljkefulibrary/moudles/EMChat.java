package com.hunliji.hljkefulibrary.moudles;

import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljkefulibrary.utils.KeFuHelper;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.model.EvaluationInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.helpdesk.model.ToCustomServiceInfo;
import com.hyphenate.helpdesk.model.TransferIndication;
import com.hyphenate.util.PathUtil;

import java.io.File;

/**
 * Created by wangtao on 2017/10/19.
 */

public class EMChat {

    public static final String UNKNOWN = "unknown";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String VOICE = "voice";
    public static final String TRACK = "track";
    public static final String ROBOT = "robot";
    public static final String TRANSFER_HINT = "transfer_hint";
    public static final String ENQUIRY = "enquiry";
    public static final String ENQUIRY_RESULT = "enquiry_result";
    public static final String MERCHANT = "merchant";
    public static final String EXTRA_VIEW = "extra_view";

    private Message message;
    private boolean showTime;

    private String enquiryDetail;
    private int enquirySummary;


    private EMTrack track;
    private Merchant merchant;
    private RobotMenuInfo menuInfo;
    private EvaluationInfo evaluationInfo;
    private ToCustomServiceInfo customServiceInfo;
    private View extraView;

    public EMChat(Message message) {
        this.message = message;
    }
    public EMChat(Message message,View extraView) {
        this.message = message;
        this.extraView=extraView;
    }

    public String getType() {
        if(extraView!=null){
            return EXTRA_VIEW;
        }
        switch (message.getType()) {
            case VOICE:
                return VOICE;
            case IMAGE:
                return IMAGE;
            case TXT:
                if (getRobotMenuInfo() != null) {
                    return ROBOT;
                } else if (getTransferToKefu()!=null) {
                    return TRANSFER_HINT;
                } else if (getEvaluationInfo() != null) {
                    return ENQUIRY;
                } else if (KeFuHelper.isEvalResult(message)) {
                    return ENQUIRY_RESULT;
                } else if (getTrack() != null) {
                    return TRACK;
                } else if (getMerchant() != null) {
                    return MERCHANT;
                } else
                    return TEXT;
            case CMD:
                return TEXT;
            default:
                return UNKNOWN;
        }
    }


    public String getContent() {
        if(message.getType()== Message.Type.CMD){
            return "转人工客服";
        }
        if (message.body() != null && message.body() instanceof EMTextMessageBody) {
            return ((EMTextMessageBody) message.body()).getMessage();
        }
        return null;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public boolean isShowTime() {
        return showTime;
    }


    public long getTime() {
        return message == null ? 0 : message.messageTime();
    }


    public boolean isSendError() {
        return message.status() == Message.Status.FAIL;
    }

    public boolean isSending() {
        return message.status() == Message.Status.INPROGRESS;
    }

    public boolean isReceive() {
        return message.direct() == Message.Direct.RECEIVE;
    }


    public String getImagePath() {
        if (message.body() == null || !(message.body() instanceof EMImageMessageBody)) {
            return null;
        }
        EMImageMessageBody imgBody = (EMImageMessageBody) message.body();
        if (!TextUtils.isEmpty(imgBody.getLocalUrl())) {
            String localUrl = imgBody.getLocalUrl();
            if (isReceive()) {
                String thumbImageName = localUrl.substring(localUrl.lastIndexOf("/") + 1,
                        localUrl.length());
                localUrl = PathUtil.getInstance()
                        .getImagePath() + "/" + "th" + thumbImageName;
            }
            File file = new File(localUrl);
            if (file.exists()) {
                return localUrl;
            }
        }
        return imgBody.getThumbnailUrl();
    }


    public int getWidth() {
        if (message.body() == null || !(message.body() instanceof EMImageMessageBody)) {
            return 0;
        }
        return ((EMImageMessageBody) message.body()).getWidth();
    }

    public int getHeight() {
        if (message.body() == null || !(message.body() instanceof EMImageMessageBody)) {
            return 0;
        }
        return ((EMImageMessageBody) message.body()).getHeight();
    }

    public String getVoicePath() {
        if (message.body() == null || !(message.body() instanceof EMVoiceMessageBody)) {
            return null;
        }
        return ((EMVoiceMessageBody) message.body()).getLocalUrl();
    }

    public int getVoiceLength() {
        if (message.body() == null || !(message.body() instanceof EMVoiceMessageBody)) {
            return 0;
        }
        return ((EMVoiceMessageBody) message.body()).getLength();
    }

    public String getRemoteUrl() {
        if (message.body() == null || !(message.body() instanceof EMFileMessageBody)) {
            return null;
        }
        return ((EMFileMessageBody) message.body()).getRemoteUrl();
    }

    public Message getMessage() {
        return message;
    }

    public String getEnquiryDetail() {
        return enquiryDetail;
    }

    public void setEnquiryDetail(String enquiryDetail) {
        this.enquiryDetail = enquiryDetail;
    }

    public int getEnquirySummary() {
        return enquirySummary;
    }

    public void setEnquirySummary(int enquirySummary) {
        this.enquirySummary = enquirySummary;
    }

    public EvaluationInfo getEvaluationInfo() {
        if (evaluationInfo == null) {
            evaluationInfo = MessageHelper.getEvalRequest(message);
        }
        return evaluationInfo;
    }

    public EMTrack getTrack() {
        if (track == null) {
            track = KeFuHelper.getTrack(message);
        }
        return track;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = KeFuHelper.getMerchant(message);
        }
        return merchant;
    }


    public RobotMenuInfo getRobotMenuInfo() {
        if (menuInfo == null) {
            menuInfo = MessageHelper.getRobotMenu(message);
        }
        return menuInfo;
    }

    public ToCustomServiceInfo getTransferToKefu() {
        if (customServiceInfo == null) {
            customServiceInfo = MessageHelper.getToCustomServiceInfo(message);
        }
        return customServiceInfo;
    }

    public View getExtraView() {
        return extraView;
    }
}
