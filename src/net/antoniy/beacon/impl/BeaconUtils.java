package net.antoniy.beacon.impl;

class BeaconUtils {
	public static int convertInet4AddrToInt(byte[] addr) {
		int addrInt = 0;
		
		byte[] reversedAddr = reverse(addr);
		for (int i = 0; i < reversedAddr.length; i++) {
			addrInt = (addrInt << 8) | (reversedAddr[i] & 0xFF);
		}
		
		return addrInt;
	}
	
	public static byte[] convertIntToInet4Addr(int addrInt) {
		byte[] addr = new byte[4];
		
		for (int i = 0; i < 4; i++) {
			addr[i] = (byte) ((addrInt >> i * 8) & 0xFF);
		}
		
		return addr;
	}
	
	public static byte[] reverse(byte[] array) {
		int limit = array.length / 2;
		byte[] reversedArray = new byte[array.length];
		
		for (int i = 0; i < limit; i++) {
			reversedArray[i] = array[array.length - i - 1];
			reversedArray[reversedArray.length - i - 1] = array[i];
		}
		
		return reversedArray;
	}
	
	//	private long generateRemoteDeviceHash(byte[] inet4addr, int port) {
	//	int addrInt = convertInet4AddrToInt(inet4addr);
	//	long hash = 0;
	//	
	//	hash = ((hash | addrInt) << 32) | port;
	//	
	//	return hash;
	//}
}
