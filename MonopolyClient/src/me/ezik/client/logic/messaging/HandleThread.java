package me.ezik.client.logic.messaging;

import java.util.ArrayList;

import me.ezik.shared.message.Message;

public class HandleThread extends Thread {

	private Message msg;
	private ArrayList<MessageListener> msgListeners = new ArrayList<MessageListener>();
	
    public HandleThread(Message msg, ArrayList<MessageListener> msgListeners) {
      this.msg = msg;
      this.msgListeners = msgListeners;
    }

    @Override
    public void run() {
    	for (MessageListener msgListener : msgListeners) {
			msgListener.handle(msg);;
		}
    }
}
