package me.suncloud.marrymemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;

public class NetworkReceiver extends BroadcastReceiver {

    private NetworkInfoChangeListener changeListener;

    public NetworkReceiver() {
    }

    public void setChangeListener(NetworkInfoChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface NetworkInfoChangeListener {
        void networkInfoChange(NetworkInfo net);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo net = connectivityManager.getActiveNetworkInfo();
            if (changeListener != null) {
                changeListener.networkInfoChange(net);
            }
            RxBus.getDefault().post(new RxEvent(RxEvent.RxEventType.NETWORK_CHANGE,net));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}