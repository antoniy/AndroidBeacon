package net.antoniy.beacon.impl;

import java.io.Serializable;

import net.antoniy.beacon.BeaconParams;

class BeaconParamsImpl implements BeaconParams {
	private int sendInterval;
	private int udpPort;
	private Class<? extends Serializable> dataClazz;
	private Object data;
	private int beaconTimeout;
	private int dataMaxSize = 512;
	
	public BeaconParamsImpl() {
	}

	public int getSendInterval() {
		return sendInterval;
	}

	public void setSendInterval(int sendInterval) {
		this.sendInterval = sendInterval;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public Class<? extends Serializable> getDataClazz() {
		return dataClazz;
	}

	public void setDataClazz(Class<? extends Serializable> dataClazz) {
		this.dataClazz = dataClazz;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getBeaconTimeout() {
		return beaconTimeout;
	}

	public void setBeaconTimeout(int beaconTimeout) {
		this.beaconTimeout = beaconTimeout;
	}

	public int getDataMaxSize() {
		return dataMaxSize;
	}

	public void setDataMaxSize(int dataMaxSize) {
		this.dataMaxSize = dataMaxSize;
	}
	
}
