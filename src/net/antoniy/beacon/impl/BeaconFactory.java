package net.antoniy.beacon.impl;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.InvalidBeaconParamsException;
import android.content.Context;

public abstract class BeaconFactory {
	
	public static Beacon createBeacon(Context context, BeaconParams beaconParams) throws BeaconException, InvalidBeaconParamsException {
		return new BeaconImpl(context, beaconParams);
	}
	
	public static BeaconParams createBeaconParams() {
		return new BeaconParamsImpl();
	}
}
