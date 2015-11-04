package brunovol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;

	private JButton mEnable;
	private JPanel mContentPanel;

	private boolean mIsEnabled = false;

	private DatagramPacket dp;
	private DatagramSocket ds;

	private Thread mThread;

	public Gui() {
		super("Gui");
		mContentPanel = new JPanel();
		mContentPanel.setBackground(Color.GRAY);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		mEnable = new JButton("Enable");
		mEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!mIsEnabled) {
					System.out.println("mIsEnabled true");
					mThread = new Thread(new Runnable() {
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									mIsEnabled = true;
									runUdpServer();
								}
							});
						}
					});
					mThread.start();
				} else {
					System.out.println("mIsEnabled false");
					mIsEnabled = false;
					mThread.interrupt();
				}
				// JDialog d = new JDialog(Gui.this, "Hello", true);
				// d.setLocationRelativeTo(Gui.this);
				// d.setVisible(true);
			}
		});

		mContentPanel.add(mEnable);
		setContentPane(mContentPanel);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				mThread.interrupt();
				stopUdpServer();
				System.exit(0);
			}
		});
	}

	private void runUdpServer() {

		String lText;

		byte[] lMsg = new byte[5];

		dp = new DatagramPacket(lMsg, lMsg.length);
		ds = null;

		try {
			ds = new DatagramSocket(1025);
			// disable timeout for testing
			// ds.setSoTimeout(100000);
			while (mIsEnabled) {
				ds.receive(dp);
				lText = new String(lMsg, 0, dp.getLength());
				System.out.println("UDP packet received" + lText);
				Runtime.getRuntime().exec("amixer set Master 100");
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

	private void stopUdpServer() {
		ds.close();
	}
}
