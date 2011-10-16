package net.antoniy.test.broadcast;

import net.antoniy.beacon.Beacon;
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
    
        beacon = BeaconFactory.createBeacon(this);
    }
    
    @Override
    protected void onPause() {
    	beacon.stopBeacon();
    	
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	beacon.startBeacon();
    	
    	super.onResume();
    }
    
}