package net.antoniy.beacon.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.antoniy.beacon.DeviceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

class BeaconThread extends Thread {
	
	private static final String TAG = BeaconThread.class.getSimpleName();
	public static final int CHECK_FOR_TIMEOUT_INTERVAL_IN_MILLIS = 3000;
	
	private Context context;
	private BeaconInternalAccessor beaconInternalAccessor;
	private DatagramSocket socket = null;
	private DatagramChannel udpChannel = null;
	private String data;
	private int sendInterval;
	private int udpPort;
	private int dataMaxSize;
	private Hashtable<Integer, DeviceInfoImpl> devices;
	private boolean canceled = false;
	private int beaconTimeout;
	
	public BeaconThread(Context context, BeaconInternalAccessor beaconInternalAccessor, String data, int sendInterval, int udpPort, int maxDataSize, int beaconTimeout) {
		this.context = context;
		this.beaconInternalAccessor = beaconInternalAccessor;
		this.data = data;
		this.sendInterval = sendInterval;
		this.udpPort = udpPort;
		this.dataMaxSize = maxDataSize;
		this.devices = new Hashtable<Integer, DeviceInfoImpl>();
		this.beaconTimeout = beaconTimeout;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "*** JOB STARTED!");

		try {
			InetSocketAddress localPort = new InetSocketAddress(udpPort);
			udpChannel = DatagramChannel.open();
			udpChannel.socket().bind(localPort);
			udpChannel.configureBlocking(false);
			
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			Selector selector = Selector.open();
			
			udpChannel.register(selector, SelectionKey.OP_READ);
			
			InetAddress broadcastAddr = getNetworkBroadcastAddr();
			ByteBuffer buffer = null;
			
			long lastRun = System.currentTimeMillis();
			long startTimeForTimeoutChecking = System.currentTimeMillis();
			
			while(true) {
				if(canceled || !isWifiConnected()) {
					break;
				}
				
				if(selector.selectNow() > 0) {
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					for(Iterator<SelectionKey> i = selectionKeys.iterator(); i.hasNext();) {
						SelectionKey key = i.next();
						
						i.remove();
						
						if(key.isReadable()) {
							buffer = ByteBuffer.allocate(dataMaxSize);
							InetSocketAddress senderAddr = (InetSocketAddress) udpChannel.receive(buffer);
							
							byte[] myInetAddr = getMyInetAddress();
							if(senderAddr != null && myInetAddr != null && !Arrays.equals(senderAddr.getAddress().getAddress(), myInetAddr)) {
								String receivedData = new String(buffer.array()).trim();
								
								processBeaconPacket(senderAddr.getAddress().getAddress(), receivedData);
								
								Log.i(TAG, "UDP[" + senderAddr.getHostName() + "]: " + receivedData + ", " + data.getBytes().length);
							}
						}
					}
				}

				if(System.currentTimeMillis() - lastRun > sendInterval) {
					DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAddr, udpPort);
					socket.send(packet);
					
					lastRun = System.currentTimeMillis();
				}
				
				if(System.currentTimeMillis() - startTimeForTimeoutChecking > CHECK_FOR_TIMEOUT_INTERVAL_IN_MILLIS && devices.size() > 0) {
					checkForTimeoutDevices();
				}
			}
		} catch (SocketException e1) {
			Log.w(TAG, e1);
		} catch (IOException e1) {
			Log.w(TAG, e1);
		} finally {
			try {
				if(udpChannel != null) {
					udpChannel.socket().close();
					udpChannel.close();
				}
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				if(socket != null) {
					socket.close();
				}
			}
		}
		
		beaconInternalAccessor.onBeaconThreadFinished();
		Log.i(TAG, "*** JOB TERMINATED!");
	}
	
	private boolean isWifiConnected() {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	
		return wifiInfo.isConnected();
	}
	
	private byte[] getMyInetAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
		int addr = wifiManager.getConnectionInfo().getIpAddress();
		if(addr == 0) {
			return null;
		}
		
		return BeaconUtils.reverse(BigInteger.valueOf(addr).toByteArray());
	}
	
	private void checkForTimeoutDevices() {
		Collection<DeviceInfoImpl> activeDevices = devices.values();
		
		for(Iterator<DeviceInfoImpl> iter = activeDevices.iterator(); iter.hasNext();) {
			DeviceInfoImpl deviceInfo = iter.next();
			
			if(System.currentTimeMillis() - deviceInfo.getTimestampLastSeen() > beaconTimeout) {
				devices.remove(deviceInfo.getHash());
				beaconInternalAccessor.onDeviceRemoved(deviceInfo);
			}
		}
	}
	
	private void processBeaconPacket(byte[] remoteInet4Addr, String data) {
		int deviceHash = BeaconUtils.convertInet4AddrToInt(remoteInet4Addr);
		
		if(devices.containsKey(deviceHash)) {
			DeviceInfoImpl deviceInfo = devices.get(deviceHash);
			deviceInfo.setTimestampLastSeen(System.currentTimeMillis());

			if(data != null && !data.equals(deviceInfo.getData())) {
				deviceInfo.setData(data);
				beaconInternalAccessor.onDeviceUpdated(deviceInfo);
			}
		} else {
			long currentTimestamp = System.currentTimeMillis();
			DeviceInfoImpl deviceInfo = new DeviceInfoImpl();
			deviceInfo.setData(data);
			deviceInfo.setTimestampDiscovered(currentTimestamp);
			deviceInfo.setTimestampLastSeen(currentTimestamp);
			deviceInfo.setHash(deviceHash);
			
			devices.put(deviceHash, deviceInfo);
			
			beaconInternalAccessor.onDeviceDiscovered(deviceInfo);
		}
	}
	
	private InetAddress getNetworkBroadcastAddr() {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}
		
		InetAddress broadcastAddr = null;
		try {
			broadcastAddr = Inet4Address.getByAddress(quads);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Log.i(TAG, "Broadcast: " + broadcastAddr.getHostAddress());
		
		return broadcastAddr;
	}
	
	public List<DeviceInfo> getAllDiscoveredDevices() {
		List<DeviceInfo> discoveredDevices = new ArrayList<DeviceInfo>();
		
		DeviceInfo tmpDeviceInfo = null;
		synchronized (devices) {
			for (DeviceInfoImpl deviceInfo : devices.values()) {
				try {
					tmpDeviceInfo = (DeviceInfo) deviceInfo.clone();
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, e.getMessage());
				}
				
				if(tmpDeviceInfo != null) {
					discoveredDevices.add(tmpDeviceInfo);
				}
			}
		}
		
		return discoveredDevices;
	}
	
	public void updateBeaconData(String data) {
		synchronized(this.data) {
			this.data = data;
		}
	}
	
	public void cancel() {
		this.canceled = true;
	}
	
}
