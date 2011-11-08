package net.antoniy.beacon.impl;

import net.antoniy.beacon.DeviceInfo;

public class DeviceInfoImpl implements DeviceInfo {
	private byte[] inet4addr;
	private int port;
	private String data;
	private long timestampDiscovered;
	private long timestampLastSeen;
	
	public DeviceInfoImpl() {
	}

	public byte[] getInet4addr() {
		return inet4addr;
	}

	public void setInet4addr(byte[] inet4addr) {
		this.inet4addr = inet4addr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
	
}
