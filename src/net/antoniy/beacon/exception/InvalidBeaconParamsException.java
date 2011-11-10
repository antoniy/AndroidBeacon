package net.antoniy.beacon.exception;

import net.antoniy.beacon.BeaconParams;

/**
 * Exception class for problems with {@link BeaconParams} attributes. 
 *
 * @author Antoniy Chonkov
 */
public class InvalidBeaconParamsException extends Exception {

	private static final long serialVersionUID = 20111016L;

	public InvalidBeaconParamsException(String message) {
		super(message);
	}
}
