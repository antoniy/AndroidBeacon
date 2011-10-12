package net.antoniy.beacon;

import java.io.IOException;
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
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class BeaconAsyncTask extends AsyncTask<Void, Void, Void> {

	private final static int LOCAL_PORT = 10010;
	private final static long SEND_BEACON_INTERVAL = 1000L;
	private final static String SEND_DATA = "Hello, Beacon app!";
	
	private Context context;
	private DatagramSocket socket = null;
	private DatagramChannel udpChannel = null;
	
	public BeaconAsyncTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		System.out.println("*** JOB STARTED!");

		try {
			InetSocketAddress localPort = new InetSocketAddress(LOCAL_PORT);
			udpChannel = DatagramChannel.open();
			udpChannel.socket().bind(localPort);
			udpChannel.configureBlocking(false);
			
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			Selector selector = Selector.open();
			
			udpChannel.register(selector, SelectionKey.OP_READ);
			
			InetAddress broadcastAddr = getNetworkBroadcastAddr();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			long lastRun = System.currentTimeMillis();
			while(true) {
				if(isCancelled()) {
					break;
				}
				
				if(selector.selectNow() > 0) {
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					for(Iterator<SelectionKey> i = selectionKeys.iterator(); i.hasNext();) {
						SelectionKey key = i.next();
						
						i.remove();
						
						if(key.isReadable()) {
							buffer.clear();
							InetSocketAddress senderAddr = (InetSocketAddress) udpChannel.receive(buffer);
							
							String data = new String(buffer.array()).trim();
							System.out.println("UDP[" + senderAddr.getHostName() + ":" + senderAddr.getPort() +"]: " + data + ", " + data.getBytes().length);
						}
						
					}
				}

				if(System.currentTimeMillis() - lastRun > SEND_BEACON_INTERVAL) {
					DatagramPacket packet = new DatagramPacket(SEND_DATA.getBytes(), SEND_DATA.length(), broadcastAddr, LOCAL_PORT);
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
		
		return null;
	}
	
	private InetAddress getNetworkBroadcastAddr() {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		
		InetAddress broadcastAddr = null;
		try {
			broadcastAddr = Inet4Address.getByAddress(quads);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		System.out.println("Broadcast: " + broadcastAddr.getHostAddress());
		
		return broadcastAddr;
	}
	
	@Override
	protected void onCancelled() {
		System.out.println("*** JOB FINISHED!");
		
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
		
		super.onCancelled();
	}
	
}
