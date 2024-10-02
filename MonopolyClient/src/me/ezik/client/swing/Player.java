package me.ezik.client.swing;

import java.awt.Color;

public class Player {

	private String pfp;
	private Color playerColor;
	private String username;
	private int moneyAmount;
	private int queuePosition;
	
	public Player(String profilePicture, Color playerColor, String username, int moneyAmount, int queuePosition) {
		this.pfp = profilePicture;
		this.playerColor = playerColor;
		this.username = username;
		this.moneyAmount = moneyAmount;
		this.queuePosition = queuePosition;
	}

	public String getPfp() {
		return pfp;
	}
	
	public int getQueuePosition() {
		return queuePosition;
	}

	public Color getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(Color playerColor) {
		this.playerColor = playerColor;
	}

	public String getUsername() {
		return username;
	}

	public int getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(int moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	
}
