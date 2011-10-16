package net.antoniy.beacon.impl;

import net.antoniy.beacon.Beacon;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

class BeaconImpl implements Beacon {
	
	private static final String TAG = "Beacon";
	
	private Context context;
	private BeaconAsyncTask beaconAsyncTask;
	private BeaconBroadcastReceiver broadcastReceiver;
	
	public BeaconImpl(Context context) {
		this.context = context;
		broadcastReceiver = new BeaconBroadcastReceiver();
	}
	
	public void stopBeacon() {
		context.unregisterReceiver(broadcastReceiver);
		
		if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(false);
    		beaconAsyncTask = null;
    	}
		
		Log.i(TAG, "Beacon stopped!");
	}
	
	public void startBeacon() {
		context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(false);
    	}
    	
    	beaconAsyncTask = new BeaconAsyncTask(context);
    	beaconAsyncTask.execute();
    	
    	Log.i(TAG, "Beacon started!");
	}
}
