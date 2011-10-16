package net.antoniy.beacon;

public interface Beacon {
	public void startBeacon() throws BeaconException;
	public void stopBeacon();
	public void initBeaconData(Object data, Class<?> dataClass);
}
