package com.hunliji.marrybiz.util;

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
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = connectivityManager.getActiveNetworkInfo();
		changeListener.networkInfoChange(net);
	}

	public interface NetworkInfoChangeListener {
		void networkInfoChange(NetworkInfo net);
	}
}