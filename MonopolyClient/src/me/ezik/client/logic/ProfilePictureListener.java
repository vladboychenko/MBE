package me.ezik.client.logic;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import me.ezik.client.swing.Window;

public class ProfilePictureListener extends MouseAdapter {

	private JLabel pictureLabel;
	private static PictureChangeListener listener;
	
	public ProfilePictureListener(JLabel label, PictureChangeListener listener) {
		super();
		pictureLabel = label;
		this.listener = listener;
	}
	
	public void mouseClicked(MouseEvent e)  
    {  
       listener.handleChange(pictureLabel);
    }
	
	
	
}
