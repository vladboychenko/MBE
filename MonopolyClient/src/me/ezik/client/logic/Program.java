package me.ezik.client.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import me.ezik.client.logic.messaging.ServerCommunicator;
import me.ezik.client.swing.Window;

public class Program {
	
	public static ServerCommunicator serverListener = null;
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(() -> {
		    new Window("Mono");
		});

	}
	
	
}
