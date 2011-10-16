package net.antoniy.test.broadcast;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconException;
import net.antoniy.beacon.R;
import net.antoniy.beacon.impl.BeaconFactory;
import android.app.Activity;
import android.os.Bundle;

public class BeaconActivity extends Activity {
	private Beacon beacon;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BroadcastData data = new BroadcastData();
        data.setDeviceId("af69df6fdf589m");
        data.setTcpHost("192.168.1.1");
        data.setTcpPort("6754");
        
        beacon = BeaconFactory.createBeacon(this);
        beacon.initBeaconData(data, BroadcastData.class);
    }
    
    @Override
    protected void onPause() {
    	beacon.stopBeacon();
    	
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	try {
    		beacon.startBeacon();
	    } catch (BeaconException e) {
	    	e.printStackTrace();
	    }
    	
    	super.onResume();
    }
    
}