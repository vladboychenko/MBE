package me.ezik.client.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import me.ezik.client.logic.messaging.ServerCommunicator;
import me.ezik.client.swing.Window;
import me.ezik.shared.message.MessageType;

public class Program {
	
	private static Window frame = null;
	public static ServerCommunicator serverListener = null;
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(() -> {
		    frame = new Window("Mono");
		});

	}
	
	public static Window getGUI() {
		return frame;
	}
	
	public static void sendMsg(MessageType msgType, String text) {
		serverListener.sendMsg(msgType, text);
	}
	
//	public static void clearMainPanel() {
//		frame.clearMenuPanel();
//	}
//	
//	public static boolean isEnoughMoney(int money) {
//		return frame.getMoneyAmount() < money;
//	}
//	
//	public static int getMapWidth() {
//		return frame.getPreferredMapWidth();
//	}
//	
}
