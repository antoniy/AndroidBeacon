package net.antoniy.beacon;

import java.util.List;

import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.BeaconStoppedException;

public interface Beacon {
	
	public void startBeacon() throws BeaconException;

	public void stopBeacon();

	public void updateBeaconData(Object newData) throws BeaconException, BeaconStoppedException;

	public void addBeaconDeviceEventListener(BeaconDeviceEventListener listener);

	public List<DeviceInfo> getActiveDevices() throws BeaconStoppedException;

	boolean isBeaconRunning();

	void addBeaconEventListener(BeaconEventListener listener);
}
