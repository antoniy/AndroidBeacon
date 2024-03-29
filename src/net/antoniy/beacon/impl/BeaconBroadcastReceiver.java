package net.antoniy.beacon.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

class BeaconBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = BeaconBroadcastReceiver.class.getSimpleName();
	
	private BeaconWifiEventListener beaconWifiEventListener;
	
	public BeaconBroadcastReceiver(BeaconWifiEventListener beaconWifiEventListener) {
		this.beaconWifiEventListener = beaconWifiEventListener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
				beaconWifiEventListener.onWifiConnected();
				Log.i(TAG, "WiFi is active!");
			} else {
				beaconWifiEventListener.onWifiDisconnected();
				Log.i(TAG, "WiFi is NOT active!");
			}
		}
	}

}
