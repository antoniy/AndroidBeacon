package net.antoniy.beacon;

import java.util.List;

import net.antoniy.beacon.exception.BeaconException;
import net.antoniy.beacon.exception.BeaconStoppedException;
import net.antoniy.beacon.impl.BeaconFactory;

/**
 * {@link Beacon} provides a service for discovering devices in LAN network.
 * 
 * It broadcasts UDP datagrams to all devices in the network with preconfigured
 * meta data. Use {@link BeaconFactory} to create Beacon instance and
 * {@link BeaconParams} for initializing the beacon service.
 * 
 * @see BeaconFactory
 * @see BeaconParams
 * 
 * @author Antoniy Chonkov
 */
public interface Beacon {

	/**
	 * Start beacon service.
	 * 
	 * The service executes in separate thread and it does not block the
	 * execution of the it's started from. When service is running
	 * {@link Beacon#isBeaconRunning()} will return <b>true</b>.
	 */
	public void startBeacon();

	/**
	 * Stop beacon service.
	 * 
	 * When service is stopped {@link Beacon#isBeaconRunning()} will return
	 * <b>false</b>.
	 */
	public void stopBeacon();

	/**
	 * Update beacon service meta data. That's the data the service broadcasts
	 * to other devices in the network.
	 * 
	 * When updating it the new data object must be of the same type as the data
	 * class specified in the {@link BeaconParams}.
	 * 
	 * @param newData
	 *            The new data object.
	 * @throws BeaconException
	 *             when the data is not from the specified in the
	 *             {@link BeaconParams} type or the size of the gson serialized
	 *             data is greater than the maximum UDP packet size specified.
	 */
	public void updateBeaconData(Object newData) throws BeaconException;

	/**
	 * Add listener for the beacon service device events.
	 * 
	 * Provide {@link BeaconDeviceEventListener} instance it will receive the
	 * device related events:
	 * <ul>
	 * <li>
	 * <i>New device discovered</i> - New device start broadcasting UDP
	 * datagrams in the network. Method
	 * {@link BeaconDeviceEventListener#discoveredBeaconDevice(DeviceInfo)
	 * discoveredBeaconDevice(DeviceInfo)} is invoked.</li>
	 * <li>
	 * <i>Device updated</i> - A device changed the meta data it broadcasts.
	 * Method {@link BeaconDeviceEventListener#updateBeaconDevice(DeviceInfo)
	 * updateBeaconDevice(DeviceInfo)} is invoked.</li>
	 * <li>
	 * <i>Device removed</i> - A device hasn't been broadcasting for more time
	 * than the specified timeout. Method
	 * {@link BeaconDeviceEventListener#removeBeaconDevice(DeviceInfo)
	 * removeBeaconDevice(DeviceInfo)} is invoked.</li>
	 * </ul>
	 * 
	 * @param listener
	 *            is the {@link BeaconDeviceEventListener} instance that will
	 *            handle the device events.
	 * @see BeaconDeviceEventListener
	 */
	public void addBeaconDeviceEventListener(BeaconDeviceEventListener listener);

	/**
	 * Retrieve the list of currently active devices in the network.
	 * 
	 * Active is considered a device that keeps broadcasting UDP datagrams in
	 * the network and does that frequently enough so the timeout, provided by
	 * the developer, is not reached.
	 * 
	 * @return a list of {@link DeviceInfo} instances.
	 * @see DeviceInfo
	 * @throws BeaconStoppedException
	 */
	public List<DeviceInfo> getActiveDevices() throws BeaconStoppedException;

	/**
	 * Check if the beacon service is running or not.
	 * 
	 * @return true if the beacon service is running, false - otherwise.
	 */
	public boolean isBeaconRunning();

	/**
	 * Add listener for beacon service events.
	 * 
	 * Provide {@link BeaconEventListener} instance to handle beacon service
	 * events.
	 * 
	 * @param listener
	 *            The {@link BeaconEventListener} instance that will handle the
	 *            beacon service events.
	 * @see BeaconEventListener
	 */
	public void addBeaconEventListener(BeaconEventListener listener);
}
