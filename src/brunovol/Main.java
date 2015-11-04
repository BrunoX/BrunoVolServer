package brunovol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {

	public static void main(String args[]) {

		runUdpServer();
	}

	private static void runUdpServer() {

		DatagramPacket dp;
		DatagramSocket ds;
		String lText;

		byte[] lMsg = new byte[7];

		dp = new DatagramPacket(lMsg, lMsg.length);
		ds = null;

		try {
			ds = new DatagramSocket(1025);
			while (true) {
				ds.receive(dp);
				lText = new String(lMsg, 0, dp.getLength());
				System.out.println("UDP packet received" + lText);

				int currentVolume = VolumeHelper.get().getCurrentVolume();
				int newVolume = 0;
				if (lText.matches("volup")) {
					newVolume = currentVolume + 4;
					System.out.println("percentage: " + newVolume + "%");
					Runtime.getRuntime().exec("amixer set Master " + newVolume + "%");
				} else if (lText.matches("voldown")) {
					newVolume = currentVolume - 4;
					System.out.println("percentage: " + newVolume + "%");
					Runtime.getRuntime().exec("amixer set Master " + newVolume + "%");
				}else if(lText.matches("mute")){
					System.out.println("mute");
					VolumeHelper.get().toggleMuteVolume();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ds != null) {

				ds.close();
			}
		}
	}
}
