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
	
	private String username = "Anon";
	
	private MenuFrameController mc = null;
	private GameFrameController gc = null;
	private AuthFrameController ac = null;

	public Window(String name) {
		super(name);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = new Double(screenSize.getWidth()).intValue();
		int height = new Double(screenSize.getHeight()).intValue();
		this.setSize(width, height);
	
		this.setVisible(true);
		this.setTitle("MBE: Monopoly By Ezik");
		this.setLayout(new BorderLayout());
		
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.setIconImage(myPicture);

		ac = new AuthFrameController(this);
		ac.initialize();
	}
	
	public String getWindowOwner() {
		return username;
	}
	
	public boolean checkAndConnect(String username, String password) {
		try {
			if (!clientCheck(username, password))
				return false;
			
			InetAddress address = InetAddress.getByName("focus-suddenly.gl.at.ply.gg");
			
	        Socket socket = new Socket(address, 27343);
	        Program.serverListener = new ServerCommunicator(socket);
	        Program.serverListener.addListener(this);
	        
	        this.username = username;
	        
	        return true;
		} catch (UnknownHostException ex) {
			UtilityAPI.showError("server not found. (" + ex.getMessage() + ").");
			return false;
		} catch (IOException ex) {
			UtilityAPI.showError("I/O exception (" + ex.getMessage() + ").");
			return false;
		}	
	}
	
	public boolean clientCheck(String username, String password) {
		if (username.isEmpty() || password.isEmpty() || username.contains(" ") || password.contains(" ")) {
			UtilityAPI.showError("username or password textfield is empty or contains whitetabs.");
			return false;
		}
		if (username.length() > 15 || password.length() > 15) {
			UtilityAPI.showError("username or password textfield are too big.");
			return false;
		}
		
		this.username = username;
		
		return true;
	}
	
    public String getUserPfpByUsername(String username) {
	   return mc.getUserPfp().get(username);
    }
    
    public void sendMsg(MessageType msgType, String content) {
    	Program.sendMsg(msgType, content);
    }
   
	@Override
	public void handle(Message msg) {
		switch(msg.getMsgType()) {
			case AUTH:
			case REG:
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
				gc.createWinDialog(winner, gc.getPlayerColorByUsername(winner), "logo" + mc.getUserPfp().get(winner),
					() -> {
							mc.initialize();
						}
					);
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
			default:
				System.out.println("unknown message");
			break;
		}
	}
	
}
