package net.antoniy.beacon;

/**
 * This interface is used by listeners of device related events and defines
 * methods for handling the events.
 * 
 * @author Antoniy Chonkov
 */
public interface BeaconDeviceEventListener {
	/**
	 * This method is responsible for handling the event when new device is
	 * discovered in the network.
	 * 
	 * @param deviceInfo
	 *            {@link DeviceInfo} instance that holds device specific
	 *            information.
	 * @see DeviceInfo
	 */
	public void discoveredBeaconDevice(DeviceInfo deviceInfo);

	/**
	 * This method is responsible for handling the event when device change the
	 * meta data it broadcasts.
	 * 
	 * @param deviceInfo
	 *            {@link DeviceInfo} instance that holds device specific
	 *            information.
	 */
	public void updateBeaconDevice(DeviceInfo deviceInfo);

	/**
	 * This method is responsible for handling the event when the device hasn't
	 * send anything for a while and it's timeout has reached. The device is
	 * considered not active and it's removed from the list of active devices.
	 * 
	 * @param deviceInfo
	 *            {@link DeviceInfo} instance that holds device specific
	 *            information.
	 */
	public void removeBeaconDevice(DeviceInfo deviceInfo);
}
