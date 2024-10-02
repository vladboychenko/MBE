package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PlayerPanelController {

	private GameFrameController window;
	private HashMap<String, Player> playerData = new HashMap<String, Player>();
	
	private final Color[] colorArray = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK}; 
	
	private JLabel[] picLabels;
	private JLabel[] nameLabels; // TD
	
	private final int STARTING_MONEY = 15000;
	
	public PlayerPanelController(GameFrameController window) {
		this.window = window;
	}
	
	public Set<String> getCompetitorPlayerNames() {
		Set<String> setString = new HashSet<>(playerData.keySet());
		setString.remove(window.getWindowOwner());
	    return setString;
	}
	
	public Set<String> getAllPlayerNames() {
		return playerData.keySet();
	}
	
	public Color getPlayerColorByUsername(String username) {
		Player p = playerData.get(username);
		return p.getPlayerColor();
	}
	
	public boolean isInitialized() {
		return !playerData.isEmpty();
	}
	
	public void handleBankrupt(String username) {
		
		Player p = playerData.get(username);
		int i = p.getQueuePosition();
		
		picLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));;
		nameLabels[i].setText("<html>" + username + "<br> bankrupt </html>");;
		
		picLabels[i].repaint();
		nameLabels[i].repaint();
		picLabels[i].revalidate();
		nameLabels[i].revalidate();
	}
	
	public void updatePlayerMoney(String username, int money) {
		
		Player p = playerData.get(username);
		int i = p.getQueuePosition();
		p.setMoneyAmount(money);
		
		nameLabels[i].setText("<html>" + username + "<br> Money:" + p.getMoneyAmount() + "</html>");
        nameLabels[i].repaint();
        nameLabels[i].revalidate();
	}
	
	public JPanel createPlayerPanel(String[] queue) {
		
		JPanel playerPanel = new JPanel();
	
		playerPanel.setBackground(Color.GRAY);
		
        GridBagConstraints stylePfp = new GridBagConstraints();
        stylePfp.gridx = 0;
        stylePfp.gridy = 0;
        stylePfp.anchor = GridBagConstraints.WEST;
        stylePfp.insets.top = 5;
        stylePfp.insets.bottom = 5;
        stylePfp.insets.left = 5;
        stylePfp.insets.right = 5;
        stylePfp.gridheight = 2;
        
        GridBagConstraints styleUserInfo = new GridBagConstraints();
        styleUserInfo.gridx = 1;
        styleUserInfo.gridy = 0;
        styleUserInfo.anchor = GridBagConstraints.WEST;
        styleUserInfo.insets.top = 2;
        styleUserInfo.insets.bottom = 2;
        styleUserInfo.insets.left = 5;
        styleUserInfo.insets.right = 5;
        
		int i = 0;
		picLabels = new JLabel[queue.length];
		nameLabels = new JLabel[queue.length];
		
		for (String name : queue) {
			 try {
				 	String userPfp = window.getUserPfpByUsername(name);
				 
			        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + userPfp + ".png");
			        BufferedImage myPicture = ImageIO.read(imageStream);
			        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			        picLabel.setBorder(BorderFactory.createLineBorder(colorArray[i], 4)); 
			        picLabels[i] = picLabel;
			        
			        JLabel userName = new JLabel("<html>" + name + "<br> Money:" + STARTING_MONEY + "</html>");
			        userName.setFont(new Font("Times New Roman", Font.PLAIN, 18));
			        userName.setForeground(Color.BLACK);
			        userName.setBorder(new EmptyBorder(0,0,0,0));
			        
			        nameLabels[i] = userName;
			        
			        Player player = new Player(userPfp, colorArray[i], name, STARTING_MONEY, i);
			        
			        playerData.put(name, player);
			        
			        playerPanel.add(picLabel, stylePfp);
			        playerPanel.add(userName, styleUserInfo);
			        stylePfp.gridx+=2;
			        styleUserInfo.gridx+=2;
			        
					
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			 i++;
		}
		
		return playerPanel;

	}
	
	public void reset() {
		playerData.clear();
	}
}
