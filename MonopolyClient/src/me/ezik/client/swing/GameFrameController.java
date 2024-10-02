package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
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
}
