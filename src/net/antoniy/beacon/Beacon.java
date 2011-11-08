package net.antoniy.beacon;

import net.antoniy.beacon.exception.BeaconException;

public interface Beacon {
	
	public void startBeacon() throws BeaconException;

	public void stopBeacon();

	public void updateData(Object newData) throws BeaconException;
}
