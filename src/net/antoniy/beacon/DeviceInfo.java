package net.antoniy.beacon;

/**
 * Instances of this type holds information about a discovered device on the
 * network.
 * 
 * @author Antoniy Chonkov
 */
public interface DeviceInfo {

	/**
	 * Retrieve the IPv4 address of the device.
	 * 
	 * @return the bytes of the IPv4 address of the device.
	 */
	public byte[] getInet4addr();

	/**
	 * The broadcasting meta data of the device.
	 * 
	 * @return the data this device broadcasts.
	 */
	public String getData();

	/**
	 * The hash for this device - that is currently the IPv4 of the device,
	 * encoded as integer.
	 * 
	 * @return the device hash - sort of ID.
	 */
	public int getHash();

}
