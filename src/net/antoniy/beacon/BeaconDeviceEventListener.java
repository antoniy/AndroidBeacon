package net.antoniy.beacon;

public interface BeaconDeviceEventListener {
	public void discoveredBeaconDevice(DeviceInfo deviceInfo);
	public void updateBeaconDevice(DeviceInfo deviceInfo);
}
