package net.antoniy.test.broadcast;

public class BroadcastData {
	private String deviceId;
	private String tcpHost;
	private String tcpPort;

	public BroadcastData() {
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getTcpHost() {
		return tcpHost;
	}

	public void setTcpHost(String tcpHost) {
		this.tcpHost = tcpHost;
	}

	public String getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}

}
