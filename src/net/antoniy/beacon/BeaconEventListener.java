package net.antoniy.beacon;

/**
 * This interface is used by listeners of beacon service events and defines
 * methods for handling the events.
 * 
 * @author Antoniy Chonkov
 */
public interface BeaconEventListener {
	/**
	 * This method is invoked when the beacon service stops.
	 */
	public void beaconStopped();
}
