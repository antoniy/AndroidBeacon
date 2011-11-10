package net.antoniy.beacon.exception;

/**
 * Exception class for beacon service stopped state. 
 *
 * @author Antoniy Chonkov
 */
public class BeaconStoppedException extends Exception {

	private static final long serialVersionUID = 20111016L;

	public BeaconStoppedException(String message) {
		super(message);
	}
}
