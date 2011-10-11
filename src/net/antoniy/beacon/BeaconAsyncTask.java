package net.antoniy.beacon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.os.AsyncTask;

public class BeaconAsyncTask extends AsyncTask<Void, Void, Void> {

//	private InetAddress broadcast;
	private DatagramSocket socket = null;
	
	public BeaconAsyncTask() {
	}
	
//	public BeaconAsyncTask(InetAddress broadcast) {
//		this.broadcast = broadcast;
//	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		System.out.println("*** JOB STARTED!");
		
		
		try {
			socket = new DatagramSocket(10010);
//			socket.setBroadcast(true);
			socket.setSoTimeout(0);
			
			byte[] buf = new byte[1024];

			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			System.out.println("Channel: " + socket.getChannel());
			
			int numOfSockFails = 0;
			while(true) {
				if(numOfSockFails >= 3) {
					System.err.println("Socket I/O error.");
					break;
				}
				
				try {
					socket.receive(packet);
				} catch (IOException e) {
					numOfSockFails++;
				}
				
				if(!socket.isClosed()) {
					System.out.println("-----------------------------------");
					System.out.println("New Packet received: " + new String(packet.getData()).trim());
					System.out.println("Length: " + packet.getLength() + ", " + packet.getAddress() + ", " + packet.getPort());
					System.out.println("Socket: " + socket.isConnected() + ", " + socket.isBound() + ", " + socket.getReceiveBufferSize() + ", " + socket.getChannel());
					System.out.println("-----------------------------------");
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
//		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
//		    broadcast, DISCOVERY_PORT);
//		socket.send(packet);

		System.out.println("*** JOB FINISHED!");
		
		return null;
	}
	
	@Override
	protected void onCancelled() {
		if(socket != null) {
			socket.close();
		}
		super.onCancelled();
	}
	
//	public void cancel() {
//		this.canceled = true;
//	}

}
