package net.antoniy.beacon;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class BeaconActivity extends Activity {
	
	private BeaconAsyncTask beaconAsyncTask;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		
		InetAddress broadcastAddr = null;
		try {
			broadcastAddr = Inet4Address.getByAddress(quads);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Packing List");
		alertDialog.setMessage("Address: " + broadcastAddr.getHostAddress());
		alertDialog.show();
    }
    
    @Override
    protected void onPause() {
    	if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(true);
    	}
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	if(beaconAsyncTask != null) {
    		beaconAsyncTask.cancel(true);
    	}
    	
    	beaconAsyncTask = new BeaconAsyncTask();
    	beaconAsyncTask.execute();
    	super.onResume();
    }
    
}