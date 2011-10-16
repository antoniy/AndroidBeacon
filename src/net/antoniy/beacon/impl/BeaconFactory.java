package net.antoniy.beacon.impl;

import net.antoniy.beacon.Beacon;
import android.content.Context;

public abstract class BeaconFactory {
	public static Beacon createBeacon(Context context) {
		return new BeaconImpl(context);
	}
}
