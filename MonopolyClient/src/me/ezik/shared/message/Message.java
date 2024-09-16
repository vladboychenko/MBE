package me.ezik.shared.message;

import java.io.Serializable;

public class Message implements Serializable {

	private MessageType type;
	private String text;
	private String authToken;
	private static final long serialVersionUID = 6969852L;
	
	public Message(MessageType type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public void setAuthToken(String token) {
		this.authToken = token;
	}
	
	public String getAuthToken() {
		return this.authToken;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setMsgType(MessageType type) {
		this.type = type;
	}
	
	public MessageType getMsgType() {
		return this.type;
	}
	
	
}
