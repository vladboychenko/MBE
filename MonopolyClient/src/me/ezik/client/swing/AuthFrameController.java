package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class AuthFrameController {

	private Window window;
	
	private HashMap<String, JPanel> panels = new HashMap<String, JPanel>();
	
	private JTextField loginField;
	private JTextField passField;
	
	public AuthFrameController(Window window) {
		this.window = window;
	}
	
	public void initialize() {
		window.getContentPane().removeAll();
		
		window.setLayout(new BorderLayout());
		
		JPanel panel = UtilityAPI.createFramePanel(new Dimension(60,60), 
				new Color(225, 225, 225));
		JPanel topPanel = UtilityAPI.createFramePanel(new Dimension(60,60), 
				Color.gray);	
		JPanel leftPanel = UtilityAPI.createFramePanel(new Dimension(60,60), 
				new Color(175, 175, 175));
		JPanel rightPanel = UtilityAPI.createFramePanel(new Dimension(60,60), 
				new Color(175, 175, 175));
		JPanel bottomPanel = UtilityAPI.createFramePanel(new Dimension(60,60), 
				new Color(175, 175, 175));
		
		panels.put("top", topPanel);
		panels.put("center", panel);
		panels.put("left", leftPanel);
		panels.put("right", rightPanel);
		panels.put("bottom", bottomPanel);
		
		window.add(topPanel, BorderLayout.NORTH);
		window.add(panel, BorderLayout.CENTER);
		window.add(leftPanel, BorderLayout.WEST);
		window.add(rightPanel, BorderLayout.EAST);
		window.add(bottomPanel, BorderLayout.SOUTH);
		
		createAuthPanel();
		
		window.revalidate();
		window.repaint();
	}
	
	public void createAuthPanel() {
		
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel logoLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
      
		JLabel loginHint = new JLabel("Username");
		
		loginHint.setBorder(new EmptyBorder(0,10,0,0));
		loginHint.setFont( new Font("Times New Roman", Font.PLAIN, 18));
		
		loginField = new JTextField();
		
		loginField.setBorder(new EmptyBorder(0,10,0,0));
		loginField.setPreferredSize(new Dimension(300,50));
		
		JLabel passHint = new JLabel("Password");
		
		passHint.setBorder(new EmptyBorder(0,10,0,0));
		passHint.setFont( new Font("Times New Roman", Font.PLAIN, 18));
		
		
		passField = new JTextField();
		
		passField.setBorder(new EmptyBorder(0,10,0,0));
		passField.setPreferredSize(new Dimension(300,50));
		
		JButton authButton = new JButton();
		
		authButton.setPreferredSize(new Dimension(150,50));
		authButton.setVisible(true);
		authButton.setText("Connect");
		
		authButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!window.checkAndConnect(loginField.getText(), passField.getText()))
					return;
			if (!window.clientCheck(loginField.getText(), passField.getText()))
				return;
            window.sendMsg(MessageType.AUTH, loginField.getText() + 
            		" " + passField.getText());
		});
		
		JButton regButton = new JButton();
		
		regButton.setPreferredSize(new Dimension(150,50));
		regButton.setVisible(true);
		regButton.setText("Register");	
		
		regButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!window.checkAndConnect(loginField.getText(), passField.getText()))
					return;
			if (!window.clientCheck(loginField.getText(), passField.getText()))
				return;
            window.sendMsg(MessageType.REG, loginField.getText() + 
            		" " + passField.getText());
		}); 
		
		GridBagConstraints styleLogo = new GridBagConstraints();
        styleLogo.gridx = 0;
        styleLogo.gridy = 0;
        styleLogo.gridwidth = 3;
        
		GridBagConstraints styleLogin = new GridBagConstraints();
		styleLogin.gridx = 1;
		styleLogin.gridy = 2;
		styleLogin.gridwidth = 2;
		GridBagConstraints stylePassword = new GridBagConstraints();
		stylePassword.gridx = 1;
		stylePassword.gridy = 4;
		stylePassword.anchor = GridBagConstraints.WEST;
		stylePassword.gridwidth = 2;
		GridBagConstraints styleIP = new GridBagConstraints();
		styleIP.gridx = 1;
		styleIP.gridy = 6;
		styleIP.anchor = GridBagConstraints.WEST;
		styleIP.gridwidth = 2;
		GridBagConstraints styleLogLab = new GridBagConstraints();
		styleLogLab.gridx = 1;
		styleLogLab.gridy = 1;
		styleLogLab.anchor = GridBagConstraints.WEST;
		GridBagConstraints stylePassLab = new GridBagConstraints();
		stylePassLab.gridx = 1;
		stylePassLab.gridy = 3;
		stylePassLab.anchor = GridBagConstraints.WEST;
		GridBagConstraints styleIPLab = new GridBagConstraints();
		styleIPLab.gridx = 1;
		styleIPLab.gridy = 5;
		styleIPLab.anchor = GridBagConstraints.WEST;
		GridBagConstraints styleConnect = new GridBagConstraints();
		styleConnect.gridx = 1;
		styleConnect.gridy = 7;
		GridBagConstraints styleReg = new GridBagConstraints();
		styleReg.gridx = 2;
		styleReg.gridy = 7;
		styleReg.anchor = GridBagConstraints.WEST;
		
		JPanel center = panels.get("center");
		
		center.add(logoLabel, styleLogo); 
		center.add(loginHint, styleLogLab);
		center.add(loginField, styleLogin);
		center.add(passHint, stylePassLab);
		center.add(passField, stylePassword);
		center.add(authButton, styleConnect);
		center.add(regButton, styleReg);
	}
	
}
