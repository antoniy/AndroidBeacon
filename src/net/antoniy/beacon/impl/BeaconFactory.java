package net.antoniy.beacon.impl;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.exception.BeaconException;
import android.content.Context;

public abstract class BeaconFactory {
	
	public static Beacon createBeacon(Context context, BeaconParams beaconParams) throws BeaconException {
		return new BeaconImpl(context, beaconParams);
	}
	
	public static BeaconParams createBeaconParams() {
		return new BeaconParamsImpl();
	}
}
