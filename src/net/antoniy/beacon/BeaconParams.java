package net.antoniy.beacon;

import java.io.Serializable;

public interface BeaconParams {

	public int getSendInterval();

	public void setSendInterval(int sendInterval);

	public int getUdpPort();

	public void setUdpPort(int udpPort);

	public Class<? extends Serializable> getDataClazz();

	public void setDataClazz(Class<? extends Serializable> dataClazz);

	public Object getData();

	public void setData(Object data);

	public int getBeaconTimeout();

	public void setBeaconTimeout(int beaconTimeout);

	int getDataMaxSize();

	void setDataMaxSize(int dataMaxSize);
}
