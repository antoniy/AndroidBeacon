package net.antoniy.beacon.impl;

import net.antoniy.beacon.DeviceInfo;

interface BeaconInternalAccessor {
	public void onDeviceDiscovered(DeviceInfo deviceInfo);
	public void onDeviceUpdated(DeviceInfo deviceInfo);
	public void onDeviceRemoved(DeviceInfo deviceInfo);
	public void onBeaconThreadFinished();
}
