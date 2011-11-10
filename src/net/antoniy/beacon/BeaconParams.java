package net.antoniy.beacon;

import java.io.Serializable;

/**
 * {@link BeaconParams} is used to store initialization parameters for the
 * {@link Beacon} service.
 * 
 * @author Antoniy Chonkov
 */
public interface BeaconParams {

	/**
	 * Retrieve beacon service broadcast UDP packets interval.
	 * 
	 * @return the broadcast interval in milliseconds.
	 */
	public int getSendInterval();

	/**
	 * Set the beacon service broadcast UDP packets interval.
	 * 
	 * The value should be > 0.
	 * 
	 * @param sendInterval
	 *            the broadcast interval in milliseconds.
	 */
	public void setSendInterval(int sendInterval);

	/**
	 * Retrieve the UDP port used for the broadcasting.
	 * 
	 * @return the broadcast UDP port number.
	 */
	public int getUdpPort();

	/**
	 * Set the UDP broadcast port.
	 * 
	 * The port should be > 1023.
	 * 
	 * @param udpPort
	 *            the broadcast UDP port number.
	 */
	public void setUdpPort(int udpPort);

	/**
	 * Retrieves the broadcast meta data {@link Class} instance.
	 * 
	 * @return the {@link Class} instance for the broadcast meta data.
	 */
	public Class<? extends Serializable> getDataClazz();

	/**
	 * Set the broadcast meta data {@link Class} instance that's going be used
	 * for validation.
	 * 
	 * The {@link Class} should implement {@link Serializable}.
	 * 
	 * @param dataClazz
	 *            the {@link Class} of the meta data.
	 */
	public void setDataClazz(Class<? extends Serializable> dataClazz);

	/**
	 * Retrieve the object that is used to generate broadcast meta data.
	 * 
	 * @return the object for the broadcast meta data.
	 */
	public Object getData();

	/**
	 * Set the broadcast meta data object.
	 * 
	 * This object will be validated against the
	 * {@link BeaconParams#setDataClazz(Class) dataClazz} and will be used to
	 * generate JSON meta data for broadcasting.
	 * 
	 * @param data
	 *            the object that holds the data for broadcasting.
	 */
	public void setData(Object data);

	/**
	 * Retrieve beacon service timeout value. This value indicates how much time
	 * should pass before an active device that doesn't broadcast UDP datagrams
	 * should be considered inactive.
	 * 
	 * @return the timeout value in milliseconds.
	 */
	public int getBeaconTimeout();

	/**
	 * Set the beacon service timeout value. This value indicates how much time
	 * should pass before an active device that doesn't broadcast UDP datagrams
	 * should be considered inactive.
	 * 
	 * @param beaconTimeout
	 *            the timeout value in milliseconds.
	 */
	public void setBeaconTimeout(int beaconTimeout);

	/**
	 * Retrieves the max size of the broadcasting meta data.
	 * 
	 * @return the max size of the meta data that is broadcasting.
	 */
	public int getDataMaxSize();

	/**
	 * Set maximum size of the broadcast meta data. The generated JSON meta data
	 * will be validated against this value. Should be > 0 and < 65535. The
	 * default value is optimal size to use with UDP datagrams.
	 * 
	 * <p>
	 * <b>Default:</b> 512 bytes
	 * </p>
	 * 
	 * @param dataMaxSize
	 *            the max size of the meta data in bytes.
	 */
	public void setDataMaxSize(int dataMaxSize);
}
