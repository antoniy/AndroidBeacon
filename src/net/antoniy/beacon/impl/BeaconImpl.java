package net.antoniy.beacon.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconDeviceEventListener;
import net.antoniy.beacon.BeaconEventListener;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.DeviceInfo;
import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.BeaconStoppedException;
import net.antoniy.beacon.exception.InvalidBeaconParamsException;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;

class BeaconImpl implements Beacon, BeaconInternalAccessor, BeaconWifiEventListener {
	
	private static final String TAG = BeaconImpl.class.getSimpleName();
	
	private Context context;
	private BeaconThread beaconThread;
	private BeaconBroadcastReceiver broadcastReceiver;
	private String jsonData;
	private BeaconParams beaconParams; 
	private List<BeaconDeviceEventListener> beaconDeviceEventListeners;
	private List<BeaconEventListener> beaconEventListeners;
	private boolean beaconRunning = false;
	
	public BeaconImpl(Context context, BeaconParams beaconParams) throws BeaconException, InvalidBeaconParamsException {
		this.context = context;
		this.beaconParams = beaconParams;
		
		validateBeaconParams();
		
		Object data = beaconParams.getData();
		this.jsonData = new Gson().toJson(data);

		if(jsonData.getBytes().length > beaconParams.getDataMaxSize()) {
			throw new BeaconException("Generated json string from property 'data' of beaconParams has bigger size than the maximum permitted size in 'dataMaxSize' property of beaconParams.");
		}
		
		broadcastReceiver = new BeaconBroadcastReceiver(this);
		beaconDeviceEventListeners = new ArrayList<BeaconDeviceEventListener>();
		beaconEventListeners = new ArrayList<BeaconEventListener>();
	}
	
	private void validateBeaconParams() throws BeaconException, InvalidBeaconParamsException {
		if(context == null || beaconParams == null) {
			throw new BeaconException("Error initializing the Beacon: null initialization parameters passed.");
		}
		
		if(beaconParams.getData() == null) {
			throw new InvalidBeaconParamsException("Property 'data' can't be null.");
		}
		
		if(beaconParams.getDataClazz() == null) {
			throw new InvalidBeaconParamsException("Property 'dataClazz' can't be null.");
		}
		
		if(beaconParams.getSendInterval() <= 0) {
			throw new InvalidBeaconParamsException("Property 'sendInterval' should be greater than 0.");
		}
		
		if(beaconParams.getUdpPort() < 1024) {
			throw new InvalidBeaconParamsException("Property 'udpPort' should be greater than 1023.");
		}
		
		if(beaconParams.getBeaconTimeout() <= BeaconThread.CHECK_FOR_TIMEOUT_INTERVAL_IN_MILLIS) {
			throw new InvalidBeaconParamsException("Property 'beaconTimeout' should be greater than " + BeaconThread.CHECK_FOR_TIMEOUT_INTERVAL_IN_MILLIS + ".");
		}
		
		if(beaconParams.getDataMaxSize() <= 0 || beaconParams.getDataMaxSize() > 65535) {
			throw new InvalidBeaconParamsException("Property 'dataMaxSize' should be greater than 0 and less or equals to 65535 bytes.");
		}
		
		Object data = beaconParams.getData();
		Class<? extends Serializable> dataClazz = beaconParams.getDataClazz();
		
		if(!dataClazz.isInstance(data)) {
			throw new InvalidBeaconParamsException("Property 'data' should be from the spcified type: " + dataClazz.getSimpleName());
		}
	}
	
	public void updateBeaconData(Object newData) throws BeaconException, BeaconStoppedException {
		Class<? extends Serializable> dataClazz = beaconParams.getDataClazz();
		
		if(!dataClazz.isInstance(newData)) {
			throw new BeaconException("New data is not from the spcified type: " + dataClazz.getSimpleName());
		}
		
		this.jsonData = new Gson().toJson(newData);
		
		if(jsonData.getBytes().length > beaconParams.getDataMaxSize()) {
			throw new BeaconException("Generated json string from property 'data' of beaconParams has bigger size than the maximum permitted size in 'dataMaxSize' property of beaconParams.");
		}
		
		this.beaconParams.setData(newData);
		
		if(beaconThread == null) {
			throw new BeaconStoppedException("Beacon service is stopped.");
		}
		
		beaconThread.updateBeaconData(jsonData);
	}
	
	public void addBeaconDeviceEventListener(BeaconDeviceEventListener listener) {
		if(listener == null) {
			return;
		}
		
		beaconDeviceEventListeners.add(listener);
	}
	
	public void addBeaconEventListener(BeaconEventListener listener) {
		if(listener == null) {
			return;
		}
		
		beaconEventListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see net.antoniy.beacon.impl.Beacon#stopBeacon()
	 */
	public synchronized void stopBeacon() {
		context.unregisterReceiver(broadcastReceiver);
		
		if(beaconThread != null) {
    		beaconThread.cancel();
    		beaconThread = null;
    	} else {
    		fireBeaconStopped();
    	}
		
		beaconRunning = false;
		
		Log.i(TAG, "Beacon stopped!");
	}
	
	/* (non-Javadoc)
	 * @see net.antoniy.beacon.impl.Beacon#startBeacon()
	 */
	public synchronized void startBeacon() throws BeaconException {
		if(beaconParams == null) {
			throw new BeaconException("Data initialization missing.");
		}
		
		context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		if(beaconThread != null) {
    		beaconThread.cancel();
    	}
    	
    	beaconThread = new BeaconThread(
    			context, 
    			this, 
    			jsonData, 
    			beaconParams.getSendInterval(), 
    			beaconParams.getUdpPort(), 
    			beaconParams.getDataMaxSize(),
    			beaconParams.getBeaconTimeout());
    	beaconThread.start();
    	
    	beaconRunning = true;
    	
    	Log.i(TAG, "Beacon started!");
	}
	
	public List<DeviceInfo> getActiveDevices() throws BeaconStoppedException {
		if(beaconThread == null) {
			throw new BeaconStoppedException("Beacon service is stopped.");
		}
		
		return beaconThread.getAllDiscoveredDevices();
	}
	
	public boolean isBeaconRunning() {
		return beaconRunning;
	}

	public synchronized void onDeviceDiscovered(DeviceInfo deviceInfo) {
		for (BeaconDeviceEventListener listener : beaconDeviceEventListeners) {
			listener.discoveredBeaconDevice(deviceInfo);
		}
	}

	public synchronized void onDeviceUpdated(DeviceInfo deviceInfo) {
		for (BeaconDeviceEventListener listener : beaconDeviceEventListeners) {
			listener.updateBeaconDevice(deviceInfo);
		}
	}

	public synchronized void onDeviceRemoved(DeviceInfo deviceInfo) {
		for (BeaconDeviceEventListener listener : beaconDeviceEventListeners) {
			listener.removeBeaconDevice(deviceInfo);
		}
	}

	public void onWifiDisconnected() {
		
	}

	public void onWifiConnected() {
		if(!beaconRunning) {
			if(beaconThread != null) {
	    		beaconThread.cancel();
	    	}
	    	
	    	beaconThread = new BeaconThread(
	    			context, 
	    			this, 
	    			jsonData, 
	    			beaconParams.getSendInterval(), 
	    			beaconParams.getUdpPort(), 
	    			beaconParams.getDataMaxSize(),
	    			beaconParams.getBeaconTimeout());
	    	beaconThread.start();
	    	
	    	beaconRunning = true;
	    	
	    	Log.i(TAG, "Beacon started!");
		}
	}

	public void onBeaconThreadFinished() {
		beaconThread = null;
		this.beaconRunning = false;
		
		fireBeaconStopped();
	}
	
	private void fireBeaconStopped() {
		for (BeaconEventListener listener : beaconEventListeners) {
			listener.beaconStopped();
		}
	}
}
