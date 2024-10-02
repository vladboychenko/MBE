package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class TradePanelController {

	private boolean isTrade = false;
	private MoneyPanel leftPanel;
	private MoneyPanel rightPanel;
	private JComboBox<String> nicknameComboBox;
	private Set<Integer> receiverTradePosition = new HashSet<Integer>();
	private Set<Integer> senderTradePosition = new HashSet<Integer>();
	private BoardPanelController boardController;
	
	public TradePanelController(BoardPanelController boardController) {
		this.boardController = boardController;
	}
	
	public boolean isTrading() {
		return isTrade;
	}
	
	public void reset() {
		receiverTradePosition.clear();
		senderTradePosition.clear();
        if (nicknameComboBox != null)
            nicknameComboBox.removeAllItems();
	}
	
	public JPanel createTradeOffer(String nickname, int money1, Set<JCompanyCard> set1, int money2, Set<JCompanyCard> set2, String value1, String value2) {
		boardController.hideMainPanel();
        
	    JPanel tradePanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();

	    JLabel tradeLabel = new JLabel("TRADE", SwingConstants.CENTER);
	    tradeLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(3, 10, 3, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    tradePanel.add(tradeLabel, gbc);

	    JLabel receiverLabel = new JLabel("Sender: " + nickname);
	    receiverLabel.setForeground(Color.BLACK);
	    gbc.gridy = 1;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.EAST;
	    tradePanel.add(receiverLabel, gbc);
	    
	    leftPanel = MoneyPanel.createMoneyPanel("SENDER", money1, value1);
	    rightPanel = MoneyPanel.createMoneyPanel("YOU", money2, value2);

	    if (set1 != null)
	    	for (JCompanyCard jcc : set1) {
	    		leftPanel.addItem(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	    	}
	    
	    if (set2 != null)
	    	for (JCompanyCard jcc : set2) {
	    		rightPanel.addItem(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	    	}
	    
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 0.5;
	    gbc.weighty = 0.5;
	    tradePanel.add(leftPanel.getPanel(), gbc);

	    gbc.gridx = 1;
	    tradePanel.add(rightPanel.getPanel(), gbc);
		
	    JPanel buttonsPanel = new JPanel();
	    JButton acceptButton = new JButton("ACCEPT");
	    acceptButton.setBackground(Color.GREEN);
	    acceptButton.addActionListener( (e) -> {
	    	tradePanel.setVisible(false);
	    	boardController.clearTradePanel();
	    	Program.serverListener.sendMsg(MessageType.TRADE_ACCEPT, null);
	    });
	   
	    JButton declineButton = new JButton("DECLINE");
	    declineButton.setContentAreaFilled(false);
        declineButton.setOpaque(false);
        declineButton.setForeground(Color.RED);
        declineButton.setBorder(new LineBorder(Color.RED));
	    declineButton.addActionListener( (e) -> {
	    	tradePanel.setVisible(false);
	    	boardController.clearTradePanel();
	    	Program.serverListener.sendMsg(MessageType.TRADE_DECLINE, null);
	    });
	    buttonsPanel.add(acceptButton);
	    buttonsPanel.add(declineButton);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.weightx = 0;
	    gbc.weighty = 0;
	    tradePanel.add(buttonsPanel, gbc);

	    tradePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
	    return tradePanel;
	}
	
	public void handleNewCompany(JCompanyCard jcc) {
		Color cl = boardController.getPlayerColorByUsername(boardController.getWindowOwner());
        if (cl != null && cl.equals(jcc.getCurrentOwnerColor())) {
            if (!senderTradePosition.add(jcc.getPosition())) {
            	UtilityAPI.showError("You already added this company to your list."); 
            	 return;
            }
	        	leftPanel.addItem(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	        	leftPanel.addValue(jcc.getCost());
            leftPanel.updateItems();
            return;
        }
        Color cl1 = boardController.getPlayerColorByUsername((String) nicknameComboBox.getSelectedItem());
        if (cl1 != null && cl1.equals(jcc.getCurrentOwnerColor())) {
        	 if (!receiverTradePosition.add(jcc.getPosition())) {
        		 UtilityAPI.showError("You already added this company to receiver list."); 
            	 return;
            }
        	rightPanel.addItem(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
        	rightPanel.addValue(jcc.getCost());
        	rightPanel.updateItems();
            return;
        }
        UtilityAPI.showError("This company does not belong to you or the receiver.");
	}
	
	public JPanel createTradeInterface() {
		boardController.hideMainPanel();
	    isTrade = true;
	    JPanel tradePanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();

	    JLabel tradeLabel = new JLabel("TRADE", SwingConstants.CENTER);
	    tradeLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(3, 10, 3, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    tradePanel.add(tradeLabel, gbc);

	    JLabel receiverLabel = new JLabel("Receiver:");
	    receiverLabel.setForeground(Color.BLACK);
	    gbc.gridy = 1;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.EAST;
	    tradePanel.add(receiverLabel, gbc);

	    Set<String> setString = boardController.getListOfPlayers();
	    String[] nicknamesString = setString.toArray(new String[0]);
	    nicknameComboBox = new JComboBox<>(nicknamesString);
	    
	    gbc.gridx = 1;
	    gbc.anchor = GridBagConstraints.WEST;
	    tradePanel.add(nicknameComboBox, gbc);
	    
	    leftPanel = MoneyPanel.createMoneyPanel("YOU");
	    rightPanel = MoneyPanel.createMoneyPanel("RECEIVER");

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 0.5;
	    gbc.weighty = 0.5;
	    tradePanel.add(leftPanel.getPanel(), gbc);

	    gbc.gridx = 1;
	    tradePanel.add(rightPanel.getPanel(), gbc);

	    JPanel buttonsPanel = new JPanel();
	    JButton confirmButton = new JButton("CONFIRM");
	    confirmButton.setBackground(Color.GREEN);
	    confirmButton.addActionListener( (e) -> {
	    	String receiverList = "";
	    	
	    	for (int rec : receiverTradePosition) {
	    		receiverList += "_" + rec;
	    	}
	    	if (receiverList.length() > 1)
	    		receiverList = receiverList.substring(1);
	    	else
	    		receiverList = "_";
	    	
	    	String senderList = "";
	    	
	    	for (int sen : senderTradePosition) {
	    		senderList += "_" + sen;
	    	}
	    	if (senderList.length() > 1)
	    		senderList = senderList.substring(1);
	    	else
	    		senderList = "_";
	    	
	    	tradePanel.setVisible(false);
	    	boardController.hideMainPanel();
	    	isTrade = false;
	    	senderTradePosition.clear();
	    	receiverTradePosition.clear();
	    	Program.serverListener.sendMsg(MessageType.TRADE_OFFER, nicknameComboBox.getSelectedItem() + " " + leftPanel.getTradeMoneyAmount() + " " + 
	    			senderList + " " + rightPanel.getTradeMoneyAmount() + " " + receiverList);
	    });
	    JButton cancelButton = new JButton("CANCEL");
	    cancelButton.addActionListener((e) -> {
	    	isTrade = false;
	    	senderTradePosition.clear();
	    	receiverTradePosition.clear();
	    	boardController.clearTradePanel(); //
	    	boardController.showMainPanel();
	    });
	    buttonsPanel.add(confirmButton);
	    buttonsPanel.add(cancelButton);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.weightx = 0;
	    gbc.weighty = 0;
	    tradePanel.add(buttonsPanel, gbc);

	    tradePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

	    return tradePanel;
	}

	private JPanel createItemPanel(String logoName, String companyName, String value, int position) {
	    JPanel itemPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();

	    BufferedImage myPicture = null;
	    InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
	    try {
	        myPicture = ImageIO.read(imageStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));

	    JLabel nameLabel = new JLabel(companyName, SwingConstants.CENTER);
	    JLabel valueLabel = new JLabel("Value: " + value, SwingConstants.CENTER);

	    JButton deleteButton = new JButton("X");
	    deleteButton.setForeground(Color.RED);
	    deleteButton.addActionListener(e ->  {
	    	itemPanel.getParent().remove(itemPanel);
	    	receiverTradePosition.remove(position);
	    	senderTradePosition.remove(position);
	    	boardController.updateBoard();
	    });

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.insets = new Insets(5, 5, 5, 5);
	    gbc.anchor = GridBagConstraints.WEST;
	    itemPanel.add(picLabel, gbc);

	    JPanel textPanel = new JPanel();
	    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
	    textPanel.add(nameLabel);
	    textPanel.add(valueLabel);

	    gbc.gridx = 1;
	    gbc.anchor = GridBagConstraints.CENTER;
	    itemPanel.add(textPanel, gbc);

	    gbc.gridx = 2;
	    gbc.anchor = GridBagConstraints.EAST;
	    itemPanel.add(deleteButton, gbc);

	    return itemPanel;
	}
	
}
