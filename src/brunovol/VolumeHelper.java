package brunovol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolumeHelper {

	private static VolumeHelper sVolumeHelper;

	private VolumeHelper() {

	}

	public static VolumeHelper get() {
		if (sVolumeHelper == null) {
			sVolumeHelper = new VolumeHelper();
		}
		return sVolumeHelper;
	}

	public int getCurrentVolume() {

		Runtime rt = Runtime.getRuntime();
		String[] commands = { "amixer", "get", "Master" };
		Process proc = null;

		try {
			proc = rt.exec(commands);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((s = stdInput.readLine()) != null) {
				sb.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int volumePercentage = parseVolumePercentage(sb.toString());
		if (volumePercentage != -1) {
			System.out.println("parsed volume percentage: " + volumePercentage);
			return volumePercentage;
		} else {
			try {
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return volumePercentage;
	}

	private int parseVolumePercentage(String output) {

		System.out.println(output);
		String pattern = "[0-9]+%";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(output);

		if (m.find()) {
			String volumePercentage = m.group(0);
			System.out.println("percentage match: " + volumePercentage);
			String volPercentageTrimmed = volumePercentage.substring(0, volumePercentage.length() - 1);
			return Integer.parseInt(volPercentageTrimmed);
		}
		System.out.println("no match");
		return -1;
	}

	public void toggleMuteVolume() {

		Runtime rt = Runtime.getRuntime();
		String[] commands = { "amixer", "-q", "-D", "pulse", "sset", "Master", "toggle" };
		Process proc = null;

		try {
			proc = rt.exec(commands);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((s = stdInput.readLine()) != null) {
				sb.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isMuted(String output) {

		boolean muted = output.contains("[off]");
		if (muted) {
			System.out.println("MUTED");
		} else {
			System.out.println("NOT MUTED");
		}
		return muted;
	}
}
