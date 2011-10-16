package net.antoniy.beacon.impl;

import com.google.gson.Gson;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconException;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

class BeaconImpl implements Beacon {
	
	private static final String TAG = "Beacon";
	
	private Context context;
	private BeaconAsyncTask beaconAsyncTask;
	private BeaconBroadcastReceiver broadcastReceiver;
	private String data;
	private Class<?> dataClass;
	
	public BeaconImpl(Context context) {
		this.context = context;
		broadcastReceiver = new BeaconBroadcastReceiver();
	}
	
	/* (non-Javadoc)
	 * @see net.antoniy.beacon.impl.Beacon#initBeaconData(java.lang.Object, java.lang.Class)
	 */
	public void initBeaconData(Object data, Class<?> dataClass) {
		this.data = new Gson().toJson(data);
		this.dataClass = dataClass;
	}
	
	/* (non-Javadoc)
	 * @see net.antoniy.beacon.impl.Beacon#stopBeacon()
	 */
	public void stopBeacon() {
		context.unregisterReceiver(broadcastReceiver);
		
		if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(false);
    		beaconAsyncTask = null;
    	}
		
		Log.i(TAG, "Beacon stopped!");
	}
	
	/* (non-Javadoc)
	 * @see net.antoniy.beacon.impl.Beacon#startBeacon()
	 */
	public void startBeacon() throws BeaconException {
		if(data == null || dataClass == null) {
			throw new BeaconException("Data initialization missing.");
		}
		
		context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(false);
    	}
    	
    	beaconAsyncTask = new BeaconAsyncTask(context, data);
    	beaconAsyncTask.execute();
    	
    	Log.i(TAG, "Beacon started!");
	}
}
