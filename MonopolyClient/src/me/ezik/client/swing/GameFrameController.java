package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GameFrameController {

	private Window window;
	
	private HashMap<String, JPanel> panels = new HashMap<String, JPanel>();
	
    private BoardPanelController boardPanelController = new BoardPanelController(this);
    private InfoPanelController infoPanelController = new InfoPanelController(this);
    private PlayerPanelController playerPanelController = new PlayerPanelController(this);
 
	
	private String currentUser;
	private int moneyAmount = 15000;
    
	public GameFrameController(Window window, String username) {
		this.window = window;
		this.currentUser = username;
	}
	
	public void killPlayer(String username) {
		if (window.getWindowOwner().equals(username)) {
			boardPanelController.clearMenuPanel();
			infoPanelController.hideControlButtons();
			UtilityAPI.showMessage("You have been eliminated. You can still watch the game unfold.");
		}
		displaySystemChatMessage(username + " have been eliminated!");
		boardPanelController.removeChip(username);
		playerPanelController.handleBankrupt(username);
	}
	
	public void initialize(String[] queue) {
		
		window.getContentPane().removeAll();
		
		window.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(70, 70));
		panel.setBackground(new Color(225, 225, 225));	
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(70, 70));
		topPanel.setBackground(Color.GRAY);
		topPanel.setLayout(new GridBagLayout());
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		panels.put("top", topPanel);
		panels.put("center", panel);
		
		window.add(topPanel, BorderLayout.NORTH);
		window.add(panel, BorderLayout.CENTER);
		
		createPlayerInterface(queue);
		
		window.revalidate();
		window.repaint();
	}
	
	public void initializeBoard(String mapData) {
		
		if (playerPanelController.isInitialized())
			this.boardPanelController.initialize(mapData);	
		window.revalidate();
		window.repaint();
	}
	
	public FontMetrics getWindowFontSize() {
		return window.getGraphics().getFontMetrics();
	}
	
	public String getWindowOwner() {
		return currentUser;
	}
	
	public String getUserPfpByUsername(String username) {
		return window.getUserPfpByUsername(username);
	}
	
	public void createPlayerInterface(String[] queue) {
		JPanel playerPanel = playerPanelController.createPlayerPanel(queue);
		
		JPanel topPanel = panels.get("top");
		
		topPanel.add(playerPanel);
		
		topPanel.revalidate();
		topPanel.repaint();	
	}
	
	public void createGameInterface() {
		
		GridBagConstraints styleTest = new GridBagConstraints();
	    styleTest.gridx = 0;
	    styleTest.gridy = 0;
	    styleTest.anchor = GridBagConstraints.WEST;
	    styleTest.fill = GridBagConstraints.BOTH;
	    styleTest.weightx = 1.0;
	    styleTest.weighty = 1.0;
		
		JSplitPane info = infoPanelController.createInfoPanelReference();
		JScrollPane board = boardPanelController.createBoardReference();
		
	    JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, info, board);
	    
	    JPanel masterPanel = panels.get("center");
	    
	    masterPanel.add(pane, styleTest);
	    
	    pane.setDividerLocation(0.6d);
	    pane.setDividerSize(0);
	    pane.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	        	 pane.setDividerLocation(0.25d);
	        }
	    });
	    
	    masterPanel.revalidate();
	    masterPanel.repaint();
	}
	
	public void showCompanyInfo(String companyName, String monopolyName, String[] rentByStars, String currentRent,
			String currentCost, String ownerColor, String cardAmountForMonopoly, String isInvestable, int position,
			String[] upgradeData) {
		this.infoPanelController.showCompanyInfo(companyName, monopolyName, rentByStars, currentRent,
				currentCost, ownerColor, cardAmountForMonopoly, 
				isInvestable, position, upgradeData);
	}
	
	public void handleRoll(String data) {
		boardPanelController.stopContinuousRoll();
		boardPanelController.startTimedRoll(1500, data);
	}
	
	public void startTurn() {
		boardPanelController.handleStartTurn();
	}
	
	public void putToJail(String panelStyle) {
		boardPanelController.handleJail(panelStyle);
	}
	
	public void updateTurnTimer(Date deadline, String username) {
		boardPanelController.startTimer(deadline, username);
	}
	
	public void updateMoney(HashMap<String, Integer> moneyData) {
		for (Entry<String, Integer> pair : moneyData.entrySet()) {
			
			if (pair.getKey().equals(currentUser))
				moneyAmount = pair.getValue();
			
	        playerPanelController.updatePlayerMoney(pair.getKey(), pair.getValue());
		}
	}
	
	public void updateCard(int cardPosition, Color newColor, String newCost, int newStarAmount, boolean isLayouted) {
		boardPanelController.handleCardUpdate(cardPosition, newColor, newCost, newStarAmount, isLayouted);
	}
	
	public void updateChipPosition(String user, int x, int y) {
		boardPanelController.handleChipMovementChange(user, x, y);
	}
	
	public void showAuction(String companyName, String companyPrice) {
		boardPanelController.handleAuction(companyName, companyPrice);
	}
	
	public void updateLayoutCards(int[] positions) {
		boardPanelController.handleLayoutCards(positions);
	}
	
	public void displayTradeOffer(String nickname, int money1, Set<Integer> set1, int money2, Set<Integer> set2, 
    		String value1, String value2) {
		boardPanelController.handleTradeOffer(nickname, money1, set1, money2, set2, value1, value2);
	}
	
   public boolean isCompanyLayoted(int position) {
	  return this.boardPanelController.isCompanyLayoted(position);
   }
	
   public Set<String> getListOfCompetitorPlayers() {
	   return this.playerPanelController.getCompetitorPlayerNames();
   }
	
   public Set<String> getPlayerUsernames() {
	   return this.playerPanelController.getAllPlayerNames();
   }
	
   public int getMoneyAmount() {
   	   return moneyAmount;
   } 
	
   public Color getPlayerColorByUsername(String username) {
	   return this.playerPanelController.getPlayerColorByUsername(username);
   }
   
   public void flushDelayedMessages() {
	   this.infoPanelController.flushDelayedMessages();
   }
   
   public void openTradePanel() {
	   this.boardPanelController.openTradePanel();
   }
   
   public void displayChatMessage(String text) {
	   this.infoPanelController.displayChatMessage(text);
   }
   
   public void displaySystemChatMessage(String text) {
	   this.infoPanelController.displaySystemChatMessage(text);
   }
   
   public void displaySystemChatMessageAfterTrigger(String text) {
	   this.infoPanelController.displaySystemChatMessageAfterTrigger(text);
   }
   
   public void reset() {
	   
	   this.moneyAmount = 15000;
	   
	   this.boardPanelController.reset();
       this.infoPanelController.reset();
       this.playerPanelController.reset();
   } 
   
	public void hideMainPanel() {
		boardPanelController.clearMenuPanel();
	}
	
	public void hideTradePanel() {
		boardPanelController.clearTradePanel();
	}
	
	public void showMainPanel() {
		boardPanelController.showMainPanel();
	}
	
	public void createWinDialog(String nickname, Color color, String logoName) {
		this.createWinDialog(nickname, color, logoName, null);
	}
	
	public void createWinDialog(String nickname, Color color, String logoName, Runnable windowExtraAction) {
	    // Create a blocking JDialog
	    JDialog dialog = new JDialog(window, "Winner", true);
	    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dialog.setSize(400, 300);
	    dialog.setLocationRelativeTo(window);

	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());
	    panel.setBackground(Color.LIGHT_GRAY);
	    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	    // Load the logo image
	    JLabel logoLabel = new JLabel();
	    try (InputStream logoStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png")) {
	        BufferedImage logoImage = ImageIO.read(logoStream);
	        if (logoImage != null) {
	            Image scaledImage = logoImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	            logoLabel.setIcon(new ImageIcon(scaledImage));
	            logoLabel.setPreferredSize(new Dimension(100, 100));
	        } else {
	            logoLabel.setText("Logo not found");
	            logoLabel.setPreferredSize(new Dimension(100, 100));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        logoLabel.setText("Error loading logo");
	        logoLabel.setPreferredSize(new Dimension(100, 100));
	    }

	    // Create a panel for the logo and add a border
	    JPanel logoPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    logoPanel.add(logoLabel, gbc);
	    logoPanel.setBackground(Color.LIGHT_GRAY);
	    logoPanel.setPreferredSize(new Dimension(110, 110));

	    // Create the nickname label
	    JLabel nicknameLabel = new JLabel("The WINNER is " + nickname + "!");
	    nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    nicknameLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    nicknameLabel.setBorder(BorderFactory.createLineBorder(color, 5));

	    // Add components to the main panel
	    panel.add(logoPanel, BorderLayout.NORTH);
	    panel.add(nicknameLabel, BorderLayout.CENTER);

	    // Create and add a close button
	    JButton closeButton = new JButton("Close");
	    closeButton.setForeground(Color.WHITE);
	    closeButton.setBackground(Color.GRAY);
	    closeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    ActionListener closeAction = e -> {
	        dialog.dispose();
	        if (windowExtraAction != null) {
	        	windowExtraAction.run();
	        }
	    };
	    closeButton.addActionListener(closeAction);

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setBackground(Color.LIGHT_GRAY);
	    buttonPanel.add(closeButton);
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    panel.add(buttonPanel, BorderLayout.SOUTH);

	    dialog.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            closeAction.actionPerformed(null);
	        }
	    });

	    dialog.add(panel);
	    dialog.setVisible(true);
	}
}
