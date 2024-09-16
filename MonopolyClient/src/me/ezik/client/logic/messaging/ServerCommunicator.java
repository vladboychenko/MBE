package me.ezik.client.logic.messaging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import me.ezik.shared.message.Message;
import me.ezik.shared.message.MessageType;

public class ServerCommunicator extends Thread {

	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String authToken = null;
	private ArrayList<MessageListener> msgListeners = new ArrayList<MessageListener>();
	
	
	public ServerCommunicator(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());
			new Thread(new Runnable() {
				@Override
				public void run() {
						Timer timer = new Timer();
						timer.scheduleAtFixedRate(new TimerTask() {
							@Override
							public void run() {
								try {
									output.writeObject("ping");
									output.writeObject(new Message(MessageType.KEEP_ALIVE, "ping"));
									output.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}	
							}
						}
						, 2000, 30 * 1000);					
				}
				
			}).start();;
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		listenForMessages();
		// TODO auth
	}
	
	public void addListener(MessageListener msgListener) {
		this.msgListeners.add(msgListener);
	}
	
	public boolean sendMsg(MessageType msgType, String text) {
		Message msgToSend = new Message(msgType, text);
		if (authToken != null) {
			msgToSend.setAuthToken(authToken);
		} else {
			msgToSend.setAuthToken("0");
		}
		try {
			output.writeObject(msgToSend);
			return true;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public void setAuthToken(String token) {
		this.authToken = token;
	}
	
	public void listenForMessages() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
//				   	socket.setSoTimeout(30 * 60 * 1000);
//				   	socket.setKeepAlive(true);
//				   	System.out.println(" " + socket.getSoTimeout());
				   	
					while (socket.isConnected()) {
						Object obj = input.readObject();

						if (obj instanceof Message) {
		                    Message newMsg = (Message) obj;
							SwingUtilities.invokeLater(new HandleThread(newMsg, msgListeners));
	                	} 						
					} 
				} catch (SocketException se) {
		            // Connection reset or closed by the client
		            System.out.println("Client: Connection reset by server");
		            
		        } catch (ClassNotFoundException | IOException ex) {
		            // Other exceptions
		            System.out.println("Client: Error in serverlistener thread; Can't receive message.... " + ex.getMessage());
		            ex.printStackTrace();
		        } finally {
		            try {
		            	input.close();
		                output.close();
		                socket.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
					
			}
		}).start();;
	}
	
}
