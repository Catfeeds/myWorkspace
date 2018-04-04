package me.suncloud.marrymemo.task;

import me.suncloud.marrymemo.model.ReturnStatus;

/**
 * Created by Suncloud on 2015/8/8.
 */
public interface StatusRequestListener {

    void onRequestCompleted(Object object, ReturnStatus returnStatus);

    void onRequestFailed(ReturnStatus returnStatus, boolean network);
}
