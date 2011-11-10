package net.antoniy.beacon.impl;

import net.antoniy.beacon.Beacon;
import net.antoniy.beacon.BeaconParams;
import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.InvalidBeaconParamsException;
import android.content.Context;

/**
 * This class is used to create instances needed for the beacon service.
 *
 * @author Antoniy Chonkov
 */
public abstract class BeaconFactory {
	
	/**
	 * Create new {@link Beacon} instance.
	 * 
	 * @param context A {@link Context} instance in which the beacon service will execute.
	 * @param beaconParams Instance of {@link BeaconParams} needed to initialize the beacon service.
	 * @return the created {@link Beacon} instance.
	 * @throws BeaconException when there is a problem with the parameters.
	 * @throws InvalidBeaconParamsException when there is a problem with {@link BeaconParams} attributes.
	 */
	public static Beacon createBeacon(Context context, BeaconParams beaconParams) throws BeaconException, InvalidBeaconParamsException {
		return new BeaconImpl(context, beaconParams);
	}
	
	/**
	 * Create new {@link BeaconParams} instance.
	 * 
	 * @return the new {@link BeaconParams} instance.
	 */
	public static BeaconParams createBeaconParams() {
		return new BeaconParamsImpl();
	}
}
