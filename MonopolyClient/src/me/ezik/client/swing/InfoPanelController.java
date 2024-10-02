package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class InfoPanelController {

	private GameFrameController window;
	private ArrayList<String> delayedMsgs = new ArrayList<String>();	
	
	private JSplitPane infoPanel;
	private JTextPane chat;
	private JPanel controlPanel;
    
    private JButton giveUp = new JButton("Give up");
	private JButton openTrade = new JButton("Trade");
    private JButton pray = new JButton("Бисмиллях");
	
	private int messageCount = 0;
    private final int MESSAGE_LIMIT = 150;

	public InfoPanelController(GameFrameController window) {
		this.window = window;
	}
	
	public void displayChatMessage(String text) {
	   this.append(text,  Color.black, new Color(225, 225, 225), false, false, false);
    }
   
    public void displaySystemChatMessage(String text) {
	   this.append(text,  Color.black, new Color(225, 225, 225), true, false, false);
    }
    
    public void displaySystemChatMessageAfterTrigger(String text) {
        delayedMsgs.add(text);
    }
	
	private void append(String text, Color fg, Color bg, boolean bold, boolean italic, boolean underline) {
        try {
        	text = formatMessage(text);
        	text += "\n";
            StyledDocument doc = chat.getStyledDocument();
            Style style = doc.addStyle("StyleName", null);
            StyleConstants.setForeground(style, fg);
            StyleConstants.setBackground(style, bg);
            StyleConstants.setBold(style, bold);
            StyleConstants.setItalic(style, italic);
            StyleConstants.setUnderline(style, underline);
            
            if (messageCount >= MESSAGE_LIMIT) {
                try {
                    int end = chat.getDocument().getDefaultRootElement().getElement(0).getEndOffset();
                    chat.getDocument().remove(0, end);
                    messageCount--;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            
            doc.insertString(doc.getLength(), text, style);
            messageCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        chat.setCaretPosition(chat.getDocument().getLength() - 1);
    }
	
	private String formatMessage(String text) {
		String result = "  ";
		String words[] = text.split("\\s++");
		for (String word : words) {
			int width = window.getWindowFontSize().stringWidth(word);
			if (chat.getWidth() < width - 55) {
				int splitIndex = word.length()/2;
				word = formatMessage(word.substring(1, splitIndex)) + " " + 
						formatMessage(word.substring(splitIndex));
			}
			result += word + " ";
		}	
		return result;
	}
	
	private void resetControlPanel() {
		controlPanel.removeAll();
		controlPanel = createControlPanel();
		
		controlPanel.repaint();
		controlPanel.revalidate();
	}
	
	private JPanel createChatPanel() {
		JPanel chatPanel = new JPanel();
		
		chatPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    chatPanel.setLayout(new GridBagLayout());
	    
	    chat = new JTextPane();
	    chat.setBackground(new Color(225, 225, 225)); 
	    chat.setBorder(BorderFactory.createLineBorder(Color.black));
	    chat.setEditable(false);
	    chat.setLayout(new GridBagLayout());
	    
	    JScrollPane scrollableChat = new JScrollPane(chat);
	    
	    GridBagConstraints styleChat = new GridBagConstraints();
	    styleChat.gridx = 0;
	    styleChat.gridy = 0;
	    styleChat.gridwidth = 2;
	    styleChat.weightx = 1.0d;
	    styleChat.weighty = 1.0;
	    styleChat.fill = GridBagConstraints.BOTH;
	    
	    chatPanel.add(scrollableChat, styleChat);
	    
	    JTextField text = new JTextField();
	    text.setPreferredSize(new Dimension(100, 30));
	    styleChat.gridy++;
	    styleChat.weighty = 0.0;
	    styleChat.weightx = 0.8;
	    styleChat.gridwidth = 1;
	    styleChat.anchor = GridBagConstraints.SOUTHWEST;
	    chatPanel.add(text, styleChat);
	    
	    JButton sendButton = new JButton("Send");
	    sendButton.addActionListener((e) -> {
	    	Program.serverListener.sendMsg(MessageType.SEND_MESSAGE, text.getText());
	    	text.setText(null); // TODO
	    });
	    sendButton.setPreferredSize(new Dimension(40, 30)); 
		   
	    styleChat.gridx++;
	    styleChat.weightx = 0.2; 
	    styleChat.anchor = GridBagConstraints.SOUTHEAST;
	    chatPanel.add(sendButton, styleChat);
	    
	    return chatPanel;
	}
	
	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
		
		controlPanel.setBackground(new Color(225, 225, 225));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    
		controlPanel.setLayout(new GridBagLayout());
	    
		openTrade = new JButton("TRADE");
		
		openTrade.addActionListener((e) -> {
			window.openTradePanel();
		});
	    
		controlPanel.removeAll();
		controlPanel.setLayout(new GridBagLayout());
		
		BufferedImage myPicture = null;
	    InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
	    try {
	        myPicture = ImageIO.read(imageStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    JLabel logoLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
		
	    GridBagConstraints tgbc = new GridBagConstraints();
	    tgbc.gridx = 0;
	    tgbc.gridy = 0;
	    tgbc.gridwidth = 3;
	    
	    controlPanel.add(logoLabel, tgbc);
	    tgbc.gridx = 0;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    controlPanel.add(openTrade, tgbc);
	    
	    pray.addActionListener((e) -> {
	    	if (new Random().nextInt(1000) < 50) {
	    		UtilityAPI.showMessage(":)");
	    	}
	    });
	    
	    tgbc.gridx = 1;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    controlPanel.add(pray, tgbc);
	    
	    giveUp.addActionListener((e) -> {
	    	giveUp.setVisible(false);
	    	Program.serverListener.sendMsg(MessageType.SURRENDER, null);
	    });
	    giveUp.setBackground(new Color(255, 102, 102));
	    giveUp.setBorder(BorderFactory.createLineBorder(Color.black, 3));
	    giveUp.setForeground(Color.white);
	    
	    tgbc.gridx = 2;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    giveUp.setVisible(true);
	    controlPanel.add(giveUp, tgbc);
	    
	    JLabel hint = new JLabel("<html>(press on any company to<br> get detailed info here)</html>");
	    tgbc.gridx = 0;
	    tgbc.gridy = 2;
	    tgbc.gridwidth = 3;
	    controlPanel.add(hint, tgbc);
	    
	    controlPanel.repaint();
	    controlPanel.revalidate();
	    
	    return controlPanel;
	}
	
	public JSplitPane createInfoPanelReference() {
	    JPanel chatPanel = createChatPanel();
	    controlPanel = createControlPanel();
	    
	    JSplitPane infoPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatPanel, controlPanel);
	    
	    infoPanel.setVisible(true);
	    infoPanel.setBorder(new EmptyBorder(0,0,0,0));
	    infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    infoPanel.setBackground(new Color(225, 225, 225));
	    
	    infoPanel.setDividerLocation(0.35d);
	    infoPanel.setDividerSize(0);
	    infoPanel.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	            infoPanel.setDividerLocation(0.35d);
	        }
	    });
	    
	    return infoPanel;
	}
	
	public void showCompanyInfo(String companyName, String monopolyName, String[] rentByStars, String currentRent,
			String currentCost, String ownerColor, String cardAmountForMonopoly, String isInvestable, int position,
			String[] upgradeData) {
		controlPanel.removeAll();
		controlPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints styleTitle = new GridBagConstraints();
		styleTitle.gridx = 0;
		styleTitle.gridy = 0;
		styleTitle.gridwidth = 4;
		styleTitle.anchor = GridBagConstraints.NORTH;
		styleTitle.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel titleText = new JLabel(companyName);
		titleText.setFont(new Font("Serif Bold", Font.BOLD, 22));
		titleText.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(titleText, styleTitle);
		
		JLabel subtitleText = new JLabel(monopolyName);
		subtitleText.setFont(new Font("Serif Bold", Font.BOLD, 16));
		subtitleText.setHorizontalAlignment(SwingConstants.CENTER);
		styleTitle.gridy++;
		styleTitle.insets.bottom = 35;
		controlPanel.add(subtitleText, styleTitle);
		
        if (window.isCompanyLayoted(position)) {
			JLabel layoutText = new JLabel("LAYOUTED!");
			subtitleText.setFont(new Font("Serif Bold", Font.BOLD, 16));
			subtitleText.setHorizontalAlignment(SwingConstants.CENTER);
			styleTitle.gridy++;
			styleTitle.insets.bottom = 35;
			controlPanel.add(layoutText, styleTitle);
		}
		
		styleTitle.insets.bottom = 0;

        JLabel label = new JLabel("Monopoly levels price:");
        styleTitle.gridx = 1;
        styleTitle.gridwidth = 1;
        styleTitle.gridy++;
        styleTitle.fill = GridBagConstraints.NONE;
        styleTitle.anchor = GridBagConstraints.NORTHWEST;
        styleTitle.insets.left = 20;
        controlPanel.add(label, styleTitle);
        for (int i = 0; i < rentByStars.length; i ++) {
        	JLabel lvl = new JLabel("Level " + (i+1) + ": " + rentByStars[i] + "UAH");
        	styleTitle.gridy++;
        	if (rentByStars[i].equals(currentRent)) {
                styleTitle.fill = GridBagConstraints.HORIZONTAL;
        		lvl.setText("<html><U> Level " + (i+1) + ": " + rentByStars[i] + "UAH</U></html>");
        	}
        	controlPanel.add(lvl, styleTitle);
        	styleTitle.fill = GridBagConstraints.NONE;
        }
   		styleTitle.weighty = 0.0d;
		styleTitle.gridheight = 2;
        styleTitle.gridwidth = 1;
        styleTitle.insets.left = 0;
        JLabel currentRentLabel = new JLabel("<html>Current rent: <br>" + currentRent + "UAH</html>");
        styleTitle.gridx = 0;
        styleTitle.gridy = 2;
        styleTitle.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(currentRentLabel, styleTitle);
        JLabel currentPrice = new JLabel("<html>Current price: <br>" + currentCost + "UAH</html>");
        styleTitle.gridy+=2;
        controlPanel.add(currentPrice, styleTitle);
        JLabel owner = new JLabel("<html>Owner: " + ownerColor + "</html>");
		styleTitle.gridheight = 1;
        styleTitle.gridy+=2;
        controlPanel.add(owner, styleTitle);
        JLabel cardAmount = new JLabel("<html>Competitors: " + cardAmountForMonopoly + "</html>");
        styleTitle.gridy++;
        controlPanel.add(cardAmount, styleTitle);
        JLabel buildable = new JLabel("<html>Investable: <br>" + isInvestable + "</html>");
		styleTitle.gridheight = 2;
        styleTitle.gridy++;
        controlPanel.add(buildable, styleTitle);
        JLabel obligation;
        if (window.isCompanyLayoted(position))
        	obligation = new JLabel("<html>DeLayout price: <br>" + Integer.parseInt(currentCost)/2+500 + "UAH</html>");
        else
        	obligation = new JLabel("<html>Layout price: <br>" + Integer.parseInt(currentCost)/2 + "UAH</html>");    
        styleTitle.gridy+=2;
        controlPanel.add(obligation, styleTitle);
        if (upgradeData != null) {
        	styleTitle.gridx++;
        	if (upgradeData.length == 1) {
               	JLabel upgradeCost = new JLabel("<html>Next upgrade cost: <br>" + upgradeData[0] + "</html>");
        		styleTitle.gridheight = 2;
                styleTitle.gridy++;
                controlPanel.add(upgradeCost, styleTitle);
        	} else {
        		if (upgradeData[0].equals("0")) {
        			JLabel upgradeCost = new JLabel("<html>Sell upgrade cost: <br>" + upgradeData[1] + "</html>");
            		styleTitle.gridheight = 2;
                    styleTitle.gridy++;
                    controlPanel.add(upgradeCost, styleTitle);
        		} else {
        			JLabel upgradeCost = new JLabel("<html>Next upgrade cost: <br>" + upgradeData[0] + "</html>");
            		styleTitle.gridheight = 2;
                    styleTitle.gridy++;
                    controlPanel.add(upgradeCost, styleTitle);
                    JLabel sellUpgradeCost = new JLabel("<html>Sell upgrade cost: <br>" + upgradeData[1] + "</html>");
            		styleTitle.gridy+=2;
            		controlPanel.add(sellUpgradeCost, styleTitle);
        		}
        	}
        	
            styleTitle.gridx--;
        }
    	styleTitle.fill = GridBagConstraints.NONE;
        JButton laybut = new JButton("Layout"); 
        laybut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.LAYOUT_CARD, position + "");
        });
        styleTitle.gridy+=2;
        styleTitle.anchor = GridBagConstraints.SOUTH;
        styleTitle.insets.left = 0;
        styleTitle.fill = GridBagConstraints.HORIZONTAL;
        styleTitle.weighty = 1.0d;
        styleTitle.insets.bottom = 50;
        controlPanel.add(laybut, styleTitle);
        JButton layBut = new JButton("DeLayout");
        layBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.DELAYOUT_CARD, position + "");
        });

        styleTitle.gridx++;
        controlPanel.add(layBut, styleTitle);
        styleTitle.gridy++;
        styleTitle.insets.bottom = 25;
        JButton investBut = new JButton("Invest");
        investBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.UPGRADE_CARD, position + "");
        });

        styleTitle.gridx = 0;
        styleTitle.weighty = 0.0d;
        controlPanel.add(investBut, styleTitle);
        JButton deinvestBut = new JButton("DeInvest");
        deinvestBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.SELL_UPGRADE, position + "");
        });

        styleTitle.gridx++;
        controlPanel.add(deinvestBut, styleTitle);
        JButton buybut = new JButton("Clear");
        buybut.addActionListener((e) -> {
        	resetControlPanel();
        });
        styleTitle.gridwidth = 2;
        styleTitle.gridx = 0;
        styleTitle.insets.bottom = 0;
        controlPanel.add(buybut, styleTitle);
        
        controlPanel.revalidate();
        controlPanel.repaint();
    }
	
	public void flushDelayedMessages() {
		for (String s : delayedMsgs) {
			append(s,  Color.black, new Color(225, 225, 225), true, false, false);
		}
		delayedMsgs.clear();
	}
	
	public void updateInfo() {
		infoPanel.revalidate();
		infoPanel.repaint();
	}
	
	public void hideControlButtons() {
		openTrade.setVisible(false);
		giveUp.setVisible(false);
	}
	
	public void reset() {
        delayedMsgs.clear();
        messageCount = 0;
	}
	
}
