package net.antoniy.beacon.impl;

import java.io.Serializable;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.exception.BeaconException;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;

class BeaconImpl implements Beacon {
	
	private static final String TAG = BeaconImpl.class.getSimpleName();
	
	private Context context;
	private BeaconAsyncTask beaconAsyncTask;
	private BeaconBroadcastReceiver broadcastReceiver;
	private String jsonData;
	private BeaconParams beaconParams; 
	
	public BeaconImpl(Context context, BeaconParams beaconParams) throws BeaconException {
		if(context == null || beaconParams == null) {
			throw new BeaconException("Error initializing the Beacon: null initialization parameters passed.");
		}
		
		if(beaconParams.getData() == null || beaconParams.getDataClazz() == null || beaconParams.getSendInterval() == 0 || beaconParams.getUdpPort() < 1024 || beaconParams.getBeaconTimeout() <= 0) {
			throw new BeaconException("BeaconParams properties can't be null, 0, negative and the udpPort should be greater than 1023.");
		}
		
		this.context = context;
		
		Object data = beaconParams.getData();
		Class<? extends Serializable> dataClazz = beaconParams.getDataClazz();
		
		if(!dataClazz.isInstance(data)) {
			throw new BeaconException("New data is not from the spcified type: " + dataClazz.getSimpleName());
		}
		
		this.jsonData = new Gson().toJson(data);
		this.beaconParams = beaconParams;
		
		broadcastReceiver = new BeaconBroadcastReceiver();
	}
	
	public void updateData(Object newData) throws BeaconException {
		Class<? extends Serializable> dataClazz = beaconParams.getDataClazz();
		
		if(!dataClazz.isInstance(newData)) {
			throw new BeaconException("New data is not from the spcified type: " + dataClazz.getSimpleName());
		}
		
		this.jsonData = new Gson().toJson(newData);
		this.beaconParams.setData(newData);
		
		// TODO: update in the async task
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
		if(beaconParams == null) {
			throw new BeaconException("Data initialization missing.");
		}
		
		context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(false);
    	}
    	
    	beaconAsyncTask = new BeaconAsyncTask(context, jsonData, beaconParams.getSendInterval(), beaconParams.getUdpPort());
    	beaconAsyncTask.execute();
    	
    	Log.i(TAG, "Beacon started!");
	}
}
