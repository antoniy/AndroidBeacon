package net.antoniy.beacon.impl;

import net.antoniy.beacon.DeviceInfo;

public class DeviceInfoImpl implements DeviceInfo, Cloneable {
	private String data;
	private long timestampDiscovered;
	private long timestampLastSeen;
	private int hash;
	
	public DeviceInfoImpl() {
	}

	public byte[] getInet4addr() {
		return BeaconUtils.convertIntToInet4Addr(hash);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getTimestampDiscovered() {
		return timestampDiscovered;
	}

	public void setTimestampDiscovered(long timestampDiscovered) {
		this.timestampDiscovered = timestampDiscovered;
	}

	public long getTimestampLastSeen() {
		return timestampLastSeen;
	}

	public void setTimestampLastSeen(long timestampLastSeen) {
		this.timestampLastSeen = timestampLastSeen;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
