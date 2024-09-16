package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class JEventCard extends JAbstractCard {

private static final long serialVersionUID = 1L;
	
//	private String moneyDisplayed = "0";
	private String name = "Unspecified";
	private String logoName = "logo0";
	
	public JEventCard(String name, String logoName, int paintRotation) {
		this.name = name;
		this.logoName = logoName;
		
		switch(paintRotation) {
		case 0:
		case 2:
			paintCard();
		break;
		case 1:
		case 3: 
			paintSide();
		break;
	}
	}
	
	public void paintCard() {
		this.setPreferredSize(new Dimension(70, 120));
		this.setLayout(new GridBagLayout());
		GridBagConstraints styleCard = new GridBagConstraints();
		styleCard.gridx = 0;
		styleCard.gridy = 0;
		styleCard.fill = GridBagConstraints.BOTH;
		styleCard.anchor = GridBagConstraints.CENTER;
		styleCard.insets.bottom = 0;
		styleCard.insets.left = 0;
		styleCard.insets.top = 0;
		styleCard.weightx=1.0d;
		styleCard.weighty=1.0d;
		styleCard.insets.right = 0;
		
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));
		
		this.add(picLabel, styleCard);

		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
//		this.addMouseListener(new MouseAdapter() {
//	        @Override
//	        public void mousePressed(MouseEvent arg0) {
//	        	JOptionPane.showMessageDialog(null, "Clicked on company card " + name, "Operation successful", JOptionPane.PLAIN_MESSAGE);;
//	        }
//	    });
		
	}
	
	public void paintSide() {
		this.setPreferredSize(new Dimension(120, 70));
		this.setLayout(new GridBagLayout());
		GridBagConstraints styleCard = new GridBagConstraints();
		styleCard.gridx = 0;
		styleCard.gridy = 0;
		styleCard.fill = GridBagConstraints.BOTH;
		styleCard.anchor = GridBagConstraints.CENTER;
		styleCard.insets.bottom = 0;
		styleCard.insets.left = 0;
		styleCard.insets.top = 0;
		styleCard.weightx=1.0d;
		styleCard.weighty=1.0d;
		styleCard.insets.right = 0;
		
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));
		
		this.add(picLabel, styleCard);

		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
//		this.addMouseListener(new MouseAdapter() {
//	        @Override
//	        public void mousePressed(MouseEvent arg0) {
//	        	JOptionPane.showMessageDialog(null, "Clicked on company card " + name, "Operation successful", JOptionPane.PLAIN_MESSAGE);;
//	        }
//	    });
		
	}
	
}
