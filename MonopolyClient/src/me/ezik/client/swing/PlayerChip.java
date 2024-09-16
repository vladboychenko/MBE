package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PlayerChip extends JPanel {

	private static final long serialVersionUID = 1L;
	private int[] position = {0, 0};
	private Color color = Color.red;
	private String username = "Anon";
	private int[] offset = new int[2];
	
	public PlayerChip(Color c, String username) {
		color = c;
		this.username = username;
		this.setBackground(c);
		this.setPreferredSize(new Dimension(30, 30));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
	}
	
	public void setOffset(int[] array) {
		offset[0] = array[0];
		offset[1] = array[1];
	}
	
	public void setOffset(int x, int y) {
		offset[0] = x;
		offset[1] = y;
	}
	
	public int[] getOffset() {
		return offset;
	}
	
	public int[] getPosition() {
		return position;
	}
	
	public void moveTo(int x, int y) {
		position[0] = x;
		position[1] = y;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String name) {
		this.username = name;
	}
	
}
