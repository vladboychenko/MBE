package me.ezik.client.logic.messaging;

import me.ezik.shared.message.Message;

public interface MessageListener {

	public void handle(Message msg);
	
}
