package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.Timer;
import javax.swing.UIManager;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.stream.util.EventReaderDelegate;

import me.ezik.client.logic.PictureChangeListener;
import me.ezik.client.logic.ProfilePictureListener;
import me.ezik.client.logic.Program;
import me.ezik.client.logic.Setting;
import me.ezik.client.logic.messaging.MessageListener;
import me.ezik.client.logic.messaging.ServerCommunicator;
import me.ezik.shared.message.Message;
import me.ezik.shared.message.MessageType;

public class Window extends JFrame implements MessageListener {

	private JPanel sidePanels[] = new JPanel[5];
	private String sides[] = {BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.WEST, BorderLayout.EAST, BorderLayout.CENTER};
	private JLabel labels[] = new JLabel[5];
	private JTextField textField[] = new JTextField[5];
	private JButton authButton = new JButton();
	private JButton regButton = new JButton();
	
	private String username = "Anon";
	private int id = 1;
	
	private MenuFrameController mc = null;
	private GameFrameController gc = null;
	
	public void debugSetPanelBackground() {
		sidePanels[0].setBackground(Color.GRAY);
		sidePanels[0].setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanels[1].setBackground(new Color(225, 225, 225));
		sidePanels[1].setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanels[2].setBackground(new Color(175, 175, 175));
		sidePanels[2].setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanels[3].setBackground(new Color(175, 175, 175));
		sidePanels[3].setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanels[4].setBackground(new Color(225, 225, 225));
		sidePanels[4].setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public Window(String name) {
		super(name);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = new Double(screenSize.getWidth()).intValue();
		int height = new Double(screenSize.getHeight()).intValue();
		this.setSize(width, height);
		//this.setSize(1080,690);
		this.setVisible(true);
		this.setTitle("MBE: Monopoly By Ezik");
		this.setLayout(new BorderLayout());
		
		for (int i = 0; i < 5; i++) {
			sidePanels[i] = new JPanel();
			labels[i] = new JLabel();
			textField[i] = new JTextField();
			sidePanels[i].setPreferredSize(new Dimension(60, 60));//150
			sidePanels[i].setLayout(new BorderLayout());
			textField[i].setBorder(new EmptyBorder(0,10,0,0));
			textField[i].setPreferredSize(new Dimension(300,50));
			labels[i].setBorder(new EmptyBorder(0,10,0,0));
			labels[i].setFont( new Font("Times New Roman", Font.PLAIN, 18));
		}
		for (int i = 0; i < 5; i++) {
			if (i != 1)
				this.add(sidePanels[i], sides[i]);
		}
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.setIconImage(myPicture);
		debugSetPanelBackground();
		authButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!checkAndConnect())
					return;
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				UtilityAPI.showError("username or password textfield is empty or contains whitetabs.");
				return;
			}
            Program.serverListener.sendMsg(MessageType.AUTH, textField[0].getText() + " " + textField[1].getText());
		});
		regButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!checkAndConnect())
					return;
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				UtilityAPI.showError("username or password textfield is empty or contains whitetabs.");
				return;
			}
			Program.serverListener.sendMsg(MessageType.REG, textField[0].getText() + " " + textField[1].getText());
		}); 
		initCenterPanel();
	}
	
	public String getWindowOwner() {
		return username;
	}
	
	public boolean checkAndConnect() {
		try {
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				UtilityAPI.showError("username or password textfield is empty or contains whitetabs.");
				return false;
			}
			if (textField[0].getText().length() > 15 || textField[1].getText().length() > 15) {
				UtilityAPI.showError("username or password textfield are too big.");
				return false;
			} 
			InetAddress address = InetAddress.getByName("focus-suddenly.gl.at.ply.gg");
	        Socket socket = new Socket(address, 27343);
	        Program.serverListener = new ServerCommunicator(socket);
	        Program.serverListener.addListener(this);
	        return true;
			} catch (UnknownHostException ex) {
				UtilityAPI.showError("server not found. (" + ex.getMessage() + ").");
				return false;
			} catch (IOException ex) {
				UtilityAPI.showError("I/O exception (" + ex.getMessage() + ").");
				return false;
		}	
	}

	public void createWinDialog(String nickname, Color color, String logoName) {
	    // Create a blocking JDialog
	    JDialog dialog = new JDialog(this, "Winner", true);
	    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dialog.setSize(400, 300);
	    dialog.setLocationRelativeTo(this);

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
	        mc.initialize();
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
	
	public void initAuthCenterComponents() {
		textField[0].setVisible(true);
		textField[1].setVisible(true);
		textField[2].setVisible(false);
		labels[0].setVisible(false);
		labels[0].setFont(new Font("Serif Bold", Font.BOLD, 28));
		labels[0].setForeground(Color.red);
		labels[0].setBorder(new EmptyBorder(0,10,0,0));
		sidePanels[1].add(labels[0]);
		
		labels[1].setText("Username");
		labels[1].setVisible(true);
		labels[2].setText("Password");
		labels[2].setVisible(true);
		labels[3].setText("Server IP");
		labels[3].setVisible(false); // T
		authButton.setPreferredSize(new Dimension(150,50));
		authButton.setVisible(true);
		authButton.setText("Connect");
		regButton.setPreferredSize(new Dimension(150,50));
		regButton.setVisible(true);
		regButton.setText("Register");	
	}
	
	public void initCenterPanel() {
		initAuthCenterComponents();
		
		 BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel logoLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
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
		
		sidePanels[4].setLayout(new GridBagLayout());
        sidePanels[4].add(logoLabel, styleLogo);
		sidePanels[4].add(textField[0], styleLogin);
		sidePanels[4].add(textField[1], stylePassword);
		sidePanels[4].add(textField[2], styleIP);
		sidePanels[4].add(labels[1], styleLogLab);
		sidePanels[4].add(labels[2], stylePassLab);
		sidePanels[4].add(labels[3], styleIPLab);
		sidePanels[4].add(authButton, styleConnect);
		sidePanels[4].add(regButton, styleReg);
	}
	
    public String getUserPfpByUsername(String username) {
	   return mc.getUserPfp().get(username);
    }
   
	@Override
	public void handle(Message msg) {
		switch(msg.getMsgType()) {
			case AUTH:
				username = textField[0].getText();
				Program.serverListener.setAuthToken(msg.getAuthToken());;	
				int profilePictureId = Integer.parseInt(msg.getText()); 
				
				mc = new MenuFrameController(this);
				mc.initialize(profilePictureId);
				
				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
	        break;
			case REG:
				this.username = textField[0].getText();
				Program.serverListener.setAuthToken(msg.getAuthToken());

				int pId = Integer.parseInt(msg.getText()); 
				
				mc = new MenuFrameController(this);			
				mc.initialize(pId);
				
				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
	        break;
			case ERROR:
				UtilityAPI.showError(msg.getText());
			break;
			case SEARCH_GAMES:
				try {
					mc.refreshGameList(msg.getText());
				} catch(Exception ex) {
					UtilityAPI.showError(ex.toString() + ex.getStackTrace()[0].toString());
				}
	        break;
			case GET_GAME:
				mc.showGameInfo(msg.getText());
			break;
			case SAVE_AS_DEFAULT:
			case SAVE_SETTINGS:
				UtilityAPI.showMessage(msg.getText());
			break;
			case CHANGE_PFP:
				int newId = Integer.parseInt(msg.getText());
				
				mc.redrawTopPanel(newId);
			break;
			case START_GAME:
				String queue[] = msg.getText().split("\\s++");
				
				gc = new GameFrameController(this, username);
				gc.initialize(queue);
				
				Program.serverListener.sendMsg(MessageType.LOAD_MAP, "");
			break;
			case LOAD_MAP:
				gc.createGameInterface();
				gc.initializeBoard(msg.getText());
			break;
			case GET_COMPANY:
				String gotCompanyData[] = msg.getText().split("\\s++");
				
				String returnedCompanyName = gotCompanyData[0].replace("_", " ");
				String monopolyName = gotCompanyData[1];
		        String[] rentByStarAmount = gotCompanyData[2].split("_");
		        String currentRent = gotCompanyData[3];
		        String currentPrice = gotCompanyData[4];
		        String ownerColor = gotCompanyData[5];
		        String cardAmountForMonopoly = gotCompanyData[6];
		        String isInvestable = gotCompanyData[7];
		        int position = Integer.parseInt(gotCompanyData[8]);
		        
		        String[] upgradeData = null;
		        
		        if (gotCompanyData.length == 10)
		        	upgradeData = gotCompanyData[9].split("_");
		        
				gc.showCompanyInfo(returnedCompanyName, monopolyName, rentByStarAmount, currentRent, currentPrice, 
						ownerColor, cardAmountForMonopoly, isInvestable, position, upgradeData);
			break;
			case SEND_MESSAGE:
				gc.displayChatMessage(msg.getText());
			break;
			case SEND_SYSTEM_MESSAGE:
				gc.displaySystemChatMessage(msg.getText());
			break;
			case SEND_DELAYED_MESSAGE:
				gc.displaySystemChatMessageAfterTrigger(msg.getText());
			break;
			case ROLL:
				String data = msg.getText();
				
				gc.handleRoll(data);
			break;
			case START_TURN:
				gc.startTurn();
			break;
			case JAIL_START:
				String panelStyle = msg.getText();
				gc.putToJail(panelStyle);
			break;
			case UPDATE_TIMER:
				String message = msg.getText();
			    String[] deadlineString = message.split("\\s++"); 
			    
			    String user = deadlineString[0];
			    String unparsedDate = deadlineString[1];
			    
			    if (unparsedDate == null || unparsedDate.isEmpty()) {
			    	UtilityAPI.showMessage("Unable to read date.");
			    	return;
			    }
		        try {
		            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		            Date parsedDate = dateFormat.parse(deadlineString[1]);
		            gc.updateTurnTimer(parsedDate, user);	
		        } catch (ParseException e) {
		        	UtilityAPI.showMessage("Unable to parse date.");
		            e.printStackTrace();
		        }
			break;
			case TIMEOUT:	
				String killedPlayer = msg.getText();	
				gc.killPlayer(killedPlayer);
			break;
			case UPDATE_MONEY:
			    String moneyData[] = msg.getText().split("\\s++");
			    HashMap<String, Integer> money = new HashMap<String, Integer>(); 
			    for (int i = 0; i < moneyData.length; i += 2) {
			    	money.put(moneyData[i], Integer.parseInt(moneyData[i+1]));
			    }
			    
		    	gc.updateMoney(money);
		    break;
			case UPDATE_CARD:
				String cardData[] = msg.getText().split("\\s++");
				int cardPosition = Integer.parseInt(cardData[0]);
				Color color = UtilityAPI.getColor(cardData[1]);
				String cost = cardData[2];
				int starAmount = Integer.parseInt(cardData[3]);
				if (starAmount != 0)
					starAmount--;
				boolean isLayouted = Boolean.parseBoolean(cardData[4]);
				
				gc.updateCard(cardPosition, color, cost, starAmount, isLayouted);
			break;
			case AUCTION:
				String[] auctionData = msg.getText().split("\\s++");
				String companyName = auctionData[0].replace("_", " ");
				String companyPrice = auctionData[1];
				
				gc.showAuction(companyName, companyPrice);
			break;
			case UPDATE_CHIP:
				String[] chipData = msg.getText().split("\\s++");
				
				String username = chipData[0];
				
				int[] clientPosition = new int[2];
				clientPosition[0] = Integer.parseInt(chipData[1]);
				clientPosition[1] = Integer.parseInt(chipData[2]);
				
				gc.updateChipPosition(username, clientPosition[0], clientPosition[1]);
			break;
			case DRAW_LAYOUT:
				String cards[] = msg.getText().split("\\s++");
				int positions[] = new int[cards.length];
				for (int pos = 0; pos < cards.length; pos++) {
					positions[pos] = Integer.parseInt(cards[pos]);
				}
				gc.updateLayoutCards(positions);
			break;
			case TRADE_OFFER:
				String[] parts = msg.getText().split(" ");
		        String nickname = parts[0];

		        int money1 = Integer.parseInt(parts[1]);
		        Set<Integer> set1 = UtilityAPI.parseSet(parts[2]);
		        
		        int money2 = Integer.parseInt(parts[3]);
		        Set<Integer> set2 = UtilityAPI.parseSet(parts[4]);
		        
		        String value1 = parts[5];
		        String value2 = parts[6];
		        
		        gc.displayTradeOffer(nickname, money1, set1, money2, set2, value1, value2);;
			break;
			case WIN:
				String winner = msg.getText();
				createWinDialog(winner, gc.getPlayerColorByUsername(winner), "logo" + mc.getUserPfp().get(winner));
			break;
			case CLEAR_TRADE:
				gc.hideTradePanel();
			break;
			case CLEAR_MENU:
				gc.hideMainPanel();
			break;
			case ROLLBACK_PANEL:
				gc.showMainPanel();
			break;
			case KEEP_ALIVE:
				System.out.println("pong");
			break;
		}
	}
	
}
