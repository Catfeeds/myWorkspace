package com.hunliji.hljchatlibrary.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {
    private NetworkInfoChangeListener changeListener;

    public NetworkReceiver(NetworkInfoChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo net = connectivityManager.getActiveNetworkInfo();
            changeListener.networkInfoChange(net);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static interface NetworkInfoChangeListener {
        void networkInfoChange(NetworkInfo net);
    }
}