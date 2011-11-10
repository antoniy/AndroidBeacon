package net.antoniy.beacon.exception;

/**
 * Exception class for general beacon service exceptions. 
 *
 * @author Antoniy Chonkov
 */
public class BeaconException extends Exception {

	private static final long serialVersionUID = 20111016L;

	public BeaconException(String message) {
		super(message);
	}
}
