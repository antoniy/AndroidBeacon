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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

class BeaconThread extends Thread {
	
	private static final String TAG = BeaconThread.class.getSimpleName();

	private Context context;
	private DatagramSocket socket = null;
	private DatagramChannel udpChannel = null;
	private String data;
	private int sendInterval;
	private int udpPort;
	private int dataMaxSize;
	private Hashtable<Long, DeviceInfoImpl> devices;
	private boolean canceled = false;
	
	public BeaconThread(Context context, String data, int sendInterval, int udpPort, int maxDataSize) {
		this.context = context;
		this.data = data;
		this.sendInterval = sendInterval;
		this.udpPort = udpPort;
		this.dataMaxSize = maxDataSize;
		this.devices = new Hashtable<Long, DeviceInfoImpl>();
	}
	
	private boolean isWifiConnected() {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return wifiInfo.isConnected();
	}
	
	private int convertInet4AddrToInt(byte[] addr) {
		int addrInt = 0;
		
		byte[] reversedAddr = reverse(addr);
		for (int i = 0; i < reversedAddr.length; i++) {
			addrInt = (addrInt << 8) | (reversedAddr[i] & 0xFF);
		}
		
		return addrInt;
	}
	
	private byte[] reverse(byte[] array) {
		int limit = array.length / 2;
		byte[] reversedArray = new byte[array.length];
		
		for (int i = 0; i < limit; i++) {
			reversedArray[i] = array[array.length - i - 1];
			reversedArray[reversedArray.length - i - 1] = array[i];
		}
		
		return reversedArray;
	}
	
	private long generateRemoteDeviceHash(byte[] inet4addr, int port) {
		int addrInt = convertInet4AddrToInt(inet4addr);
		long hash = 0;
		
		hash = ((hash | addrInt) << 32) | port;
		
		return hash;
	}
	
	private byte[] getMyInetAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		return reverse(BigInteger.valueOf(wifiManager.getConnectionInfo().getIpAddress()).toByteArray());
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
//			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
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
							
							if(senderAddr != null && !Arrays.equals(senderAddr.getAddress().getAddress(), getMyInetAddress())) {
								String receivedData = new String(buffer.array()).trim();
								
								processBeaconPacket(senderAddr.getAddress().getAddress(), senderAddr.getPort(), receivedData);
								
								Log.i(TAG, "UDP[" + senderAddr.getHostName() + ":" + senderAddr.getPort() +"]: " + receivedData + ", " + data.getBytes().length);
							}

						}
						
					}
				}

				if(System.currentTimeMillis() - lastRun > sendInterval) {
					DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAddr, udpPort);
					socket.send(packet);
					
					lastRun = System.currentTimeMillis();
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(udpChannel != null) {
					udpChannel.socket().close();
					udpChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(socket != null) {
					socket.close();
				}
			}
			
		}
		
		Log.i(TAG, "*** JOB TERMINATED!");
	}
	
	private void processBeaconPacket(byte[] remoteInet4Addr, int remotePort, String data) {
		long deviceHash = generateRemoteDeviceHash(remoteInet4Addr, remotePort);
		
		if(devices.containsKey(deviceHash)) {
			DeviceInfoImpl deviceInfo = devices.get(deviceHash);
			deviceInfo.setTimestampLastSeen(System.currentTimeMillis());

			if(data != null && !data.equals(deviceInfo.getData())) {
				deviceInfo.setData(data);
				
				// TODO: Notify for updated device.
			}
		} else {
			long currentTimestamp = System.currentTimeMillis();
			DeviceInfoImpl deviceInfo = new DeviceInfoImpl();
			deviceInfo.setData(data);
			deviceInfo.setInet4addr(remoteInet4Addr);
			deviceInfo.setPort(remotePort);
			deviceInfo.setTimestampDiscovered(currentTimestamp);
			deviceInfo.setTimestampLastSeen(currentTimestamp);
			
			devices.put(deviceHash, deviceInfo);
			
			// TODO: Notify for new device.
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
	
	public void updateBeaconData(String data) {
		synchronized(this.data) {
			this.data = data;
		}
	}
	
	public void cancel() {
		this.canceled = true;
	}
	
}