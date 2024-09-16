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

public class Window extends JFrame implements MessageListener, PictureChangeListener {

	private JPanel sidePanels[] = new JPanel[5];
	private String sides[] = {BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.WEST, BorderLayout.EAST, BorderLayout.CENTER};
	private JLabel labels[] = new JLabel[5];
	private JTextField textField[] = new JTextField[5];
	private JButton authButton = new JButton();
	private JButton regButton = new JButton();
	private String username = "Anon";
	
	private JPanel centerPanel;
	private JList<String> gameList;
	private ArrayList<Setting> settings = new ArrayList<Setting>();
	private DefaultListModel<String> listModel;
	private int defaultPfp = 1;
	// TODO failsafe if crash; transactions? started game check on all menu messages
	private String currentlyViewedGameName = null;
	public static JLabel selectedPicture = null;
	private ArrayList<String> userData = new ArrayList<String>();
	private HashMap<String, String> userPfp = new HashMap<String, String>();
	public static final Color[] colorArray = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK}; 
	private HashMap<String, Color> playerColor = new HashMap<String, Color>();
	
	public PlayerChip[] playerColors;
	private int moneyAmount = 15000;
	
	private ArrayList<String> delayedMsgs = new ArrayList<String>();
	
	private int messageCount = 0;
    private final int MESSAGE_LIMIT = 150;
    
    private final int ROLL_WAITTIME = 1500;
    
    private class ChipMovement {
    	private PlayerChip chip;
    	private int[] movement;
    	
    	public ChipMovement(PlayerChip chip, int xMovement, int yMovement) {
    		this.chip = chip;
    		movement = new int[2];
    		movement[0] = xMovement;
    		movement[1] = yMovement;
    	}
    	
    	public PlayerChip getChip() {
    		return chip;
    	}
    	
    	public int[] getMovement() {
    		return movement;
    	}
    }
    
    private ChipMovement alteredMovement = null;
    JButton giveUp = new JButton("Give up");
	
	public void initSettings() {
		JTextField smallTextField = new JTextField();
		smallTextField.setPreferredSize(new Dimension(35,30));
		JTextField smallTextField1 = new JTextField();
		smallTextField1.setPreferredSize(new Dimension(35,30));
		String[] modes = {"classic"};
		JComboBox<String> modeBox = new JComboBox<String>(modes);
		smallTextField.setFont(new Font("Serif Bold", Font.PLAIN, 22));
		smallTextField1.setFont(new Font("Serif Bold", Font.PLAIN, 22));
		settings.add(new Setting("Max players", smallTextField, "Max amount of players to be able to connect to the game."));
		settings.add(new Setting("Mode", modeBox, "Gamemode with preset amount of changes. For example:\n classic has no major changes from original monopoly."));
		settings.add(new Setting("Trade coefficient", smallTextField1, "Max multiplier ratio between presented value in trades. "
				+ "\nSetting to value lesser than 1 will turn it off. Example: with trade coefficient"
				+ "\n2.0 player cannot offer more than 2000 value, if his trade"
				+ "\npartner only offers 1000 in value."));;
	}
	
	public boolean initTextField(JComponent comp, String value) {
		if (comp instanceof JTextField) {
			((JTextField) comp).setText(value);
			return true;
		}
		
		return false;
	}
	
	public Window(String name) {
		super(name);
		initSettings();
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
//		try {
//		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
		debugSetPanelBackground();
		authButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!checkAndConnect())
					return;
//			if (textField[2].getText().split(":").length != 2) {
//				showError("server IP textfield must contain host name and port.", "101");
//				return;
//			}
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				showError("username or password textfield is empty or contains whitetabs.");
				return;
			}
            Program.serverListener.sendMsg(MessageType.AUTH, textField[0].getText() + " " + textField[1].getText());
		});
		regButton.addActionListener(e -> {
			if (Program.serverListener == null)
				if (!checkAndConnect())
					return;
//			if (textField[2].getText().split(":").length != 2) {
//				showError("server IP textfield must contain host name and port.", "101");
//				return;
//			}
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				showError("username or password textfield is empty or contains whitetabs.");
				return;
			}
			Program.serverListener.sendMsg(MessageType.REG, textField[0].getText() + " " + textField[1].getText());
		}); 
		initCenterPanel();
	}
	
	public boolean checkAndConnect() {
		try {
//			if (textField[2].getText().split(":").length != 2) {
//			showError("server IP textfield must contain host name and port.", "101");
//			return false;
//			}
			if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[0].getText().contains(" ") || textField[1].getText().contains(" ")) {
				showError("username or password textfield is empty or contains whitetabs.");
				return false;
			}
			if (textField[0].getText().length() > 15 || textField[1].getText().length() > 15) {
				showError("username or password textfield are too big.");
				return false;
			} // 22.ip.gl.ply.gg:27343
	        //InetAddress address = InetAddress.getByName(textField[2].getText().split(":")[0]); Socket socket = new Socket(address, Integer.parseInt(textField[2].getText().split(":")[1]));
			//InetAddress address = InetAddress.getByName("22.ip.gl.ply.gg");27343 147.185.221.22:27343
			InetAddress address = InetAddress.getByName("focus-suddenly.gl.at.ply.gg ");
	        Socket socket = new Socket(address, 27343);
	        Program.serverListener = new ServerCommunicator(socket);
	        Program.serverListener.addListener(this);
	        return true;
			} catch (UnknownHostException ex) {
				showError("server not found. (" + ex.getMessage() + ").");
				return false;
			} catch (IOException ex) {
				showError("I/O exception (" + ex.getMessage() + ").");
				return false;
		}	
	}
	
	JPanel pfpPanel = new JPanel();
	
	public void initCenterPanelMenu() {
	    currentlyViewedGameName = null;
	    centerPanel.removeAll();

	    pfpPanel = new JPanel();
	    pfpPanel.setLayout(new GridBagLayout());
	    
	    GridBagLayout gbl = new GridBagLayout();
	    gbl.rowWeights = new double[]{0.05, 0.05, 0.6, 0.3};
	    
	    centerPanel.setLayout(gbl);

	    GridBagConstraints styleAva = new GridBagConstraints();
	    styleAva.gridx = 0;
	    styleAva.gridy = 0;
	    styleAva.anchor = GridBagConstraints.NORTH;
	    styleAva.insets.top = 25;
	    styleAva.insets.bottom = 15;
	    styleAva.insets.left = 7;
	    styleAva.insets.right = 7;

	    this.revalidate();
	    this.repaint();

	    centerPanel.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	            adjustLogoLayout(styleAva);
	        }
	    });

	    int logoWidth = 100;
	    int containerWidth = centerPanel.getWidth();
	    int numLogosPerRow = containerWidth / (logoWidth + styleAva.insets.left + styleAva.insets.right);

	    if (numLogosPerRow == 0) {
	        numLogosPerRow = 1;
	    }
	    int logoAmount = 14;

	    for (int i = 1; i < logoAmount; i++) {
	        if (i == 13) {
	            styleAva.weightx = 1.0d;
	        }
	        BufferedImage myPicture = null;
	        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + i + ".png");
	        try {
	            myPicture = ImageIO.read(imageStream);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(logoWidth, 100, Image.SCALE_DEFAULT)));
	        picLabel.putClientProperty("index", i);
	        picLabel.addMouseListener(new ProfilePictureListener(picLabel, this));
	        if (i == defaultPfp) {
	            selectedPicture = picLabel;
	            picLabel.setBorder(BorderFactory.createLineBorder(Color.cyan, 7));
	        }
	        pfpPanel.add(picLabel, styleAva);

	        if (styleAva.gridx >= numLogosPerRow - 1) {
	            styleAva.insets.top = 10;
	            styleAva.gridx = 0;
	            styleAva.gridy++;
	        } else {
	            styleAva.gridx++;
	        }
	    }

	    // Create JScrollPane and add pfpPanel to it
	    JScrollPane scrollPane = new JScrollPane(pfpPanel);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    BufferedImage myPicture = null;
	    InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/applogo.png");
	    try {
	        myPicture = ImageIO.read(imageStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    JLabel logoLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
	    logoLabel.setBorder(BorderFactory.createBevelBorder(5));
	    GridBagConstraints styleLogo = new GridBagConstraints();
	    styleLogo.gridx = 0;
	    styleLogo.gridy = 0;
	    styleLogo.weightx = 1.0d;
	    styleLogo.anchor = GridBagConstraints.NORTH;

	    JLabel textLabel = new JLabel("Choose your profile picture:");
	    textLabel.setFont(new Font("Serif Bold", Font.BOLD, 30));
	    GridBagConstraints styleText = new GridBagConstraints();
	    styleText.gridx = 0;
	    styleText.gridy = 1;
	    styleText.weightx = 1.0d;
	    styleText.anchor = GridBagConstraints.NORTH;

	    GridBagConstraints stylePfp = new GridBagConstraints();
	    stylePfp.gridx = 0;
	    stylePfp.gridy = 2;
	//    stylePfp.gridwidth = 2;
	    stylePfp.weightx = 1.0d;
	    stylePfp.fill = GridBagConstraints.BOTH;
	    stylePfp.anchor = GridBagConstraints.NORTH;

	    JButton save = new JButton("Change profile picture");
	    save.setPreferredSize(new Dimension(150, 50));
	    save.addActionListener((e) -> Program.serverListener.sendMsg(MessageType.CHANGE_PFP, Integer.toString((int) selectedPicture.getClientProperty("index"))));
	    
	    GridBagConstraints styleSave = new GridBagConstraints();
	    styleSave.gridx = 0;
	    styleSave.gridy = 3;
	    styleSave.insets.left = 10;
	    styleSave.anchor = GridBagConstraints.WEST;
	    styleSave.weightx = 1.0d;

	    centerPanel.add(textLabel, styleText);
	    centerPanel.add(logoLabel, styleLogo);
	    centerPanel.add(scrollPane, stylePfp); // Add JScrollPane to centerPanel
	    centerPanel.add(save, styleSave);

	    centerPanel.revalidate();
	    centerPanel.repaint();
	}

	private void adjustLogoLayout(GridBagConstraints styleAva) {
	    if (currentlyViewedGameName != null)
	        return;

	    this.revalidate();
	    this.repaint();

	    int componentWidth = centerPanel.getWidth();
	    int logoWidth = 100; // Adjust this value based on your logo size
	    int numLogosPerRow = componentWidth / (logoWidth + 14); // 14 for left and right insets (7 each)

	    if (numLogosPerRow == 0) {
	        numLogosPerRow = 1; // At least one logo per row
	    }

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(25, 7, 15, 7); // Setting insets for each component
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.anchor = GridBagConstraints.NORTH;

	    Component[] components = pfpPanel.getComponents();
	    for (int i = 0; i < components.length; i++) {
	        if (components[i] instanceof JLabel) {
	            gbc.gridx = i % numLogosPerRow;
	            gbc.gridy = i / numLogosPerRow;

	            pfpPanel.setComponentZOrder(components[i], 0); // Bring logos to the front
	            pfpPanel.remove(components[i]);
	            pfpPanel.add(components[i], gbc);
	        } else if (components[i] instanceof JButton) {
	            gbc.gridx = 0;
	            gbc.weighty = 1.0d;
	            gbc.weightx = 1.0d;
	            gbc.gridy++;
	            gbc.anchor = GridBagConstraints.SOUTH;
	            pfpPanel.add(components[i], gbc);
	        }
	    }

	    pfpPanel.revalidate();
	    pfpPanel.repaint();
	}


	
	public void initMainLeftComponents() {
			
			JButton createGame = new JButton("+");
	    	createGame.addActionListener(e -> {
	    		String name = JOptionPane.showInputDialog(this, "Enter name of the game:", "DefaultGameName");
	    		if (name != null && !name.isEmpty() && name.matches("\\S+")) {
	    			Program.serverListener.sendMsg(MessageType.CREATE_GAME, name);
	    		} else showError("Name should not contain whitespaces.");
	      	});
	    	createGame.setPreferredSize(new Dimension(50,50));
	    	createGame.setFont(new Font("Serif Bold", Font.BOLD, 25));
	    	createGame.setBackground(new Color(175, 175, 175));
	    	createGame.setBackground(new Color(85, 85, 85));
	    	createGame.setForeground(new Color(175, 175, 175));
	    	JButton refresh = new JButton("↻");
	    	refresh.addActionListener(e -> {
	    		Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
	 	    });		
	    	refresh.setPreferredSize(new Dimension(50,50));
	    	refresh.setFont(new Font("Serif Bold", Font.BOLD, 15));
	    	refresh.setBackground(new Color(85, 85, 85));
	    	refresh.setForeground(new Color(175, 175, 175));
	        sidePanels[2].setLayout(new GridBagLayout()); 
	        JButton home = new JButton("⌂");
	        home.addActionListener(e -> {
	        	initCenterPanelMenu();
	        });
	        home.setPreferredSize(new Dimension(50,50));
	        home.setFont(new Font("Serif Bold", Font.BOLD, 15));
	        home.setBackground(new Color(85, 85, 85));
	        home.setForeground(new Color(175, 175, 175));
	       
	        GridBagConstraints styleCreate = new GridBagConstraints();
	        styleCreate.gridx = 0;
	        styleCreate.gridy = 0;
	        styleCreate.anchor = GridBagConstraints.NORTH;
	        styleCreate.insets.top = 15;
	        GridBagConstraints styleRefresh = new GridBagConstraints();
	        styleRefresh.gridx = 0;
	        styleRefresh.gridy = 1;
	        styleRefresh.anchor = GridBagConstraints.NORTH;
	        styleRefresh.insets.top = 8;
	        GridBagConstraints styleHome = new GridBagConstraints();
	        styleHome.gridx = 0;
	        styleHome.gridy = 2;
	        styleHome.weighty = 1.0d;
	        styleHome.anchor = GridBagConstraints.NORTH;
	        styleHome.insets.top = 8;
	        sidePanels[2].add(createGame, styleCreate);
	        sidePanels[2].add(refresh, styleRefresh);
	        sidePanels[2].add(home, styleHome);
	}
	
	public void initMainTopComponents(int pfpId) {
	    try {
	        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + pfpId + ".png");
	        BufferedImage myPicture = ImageIO.read(imageStream);
	        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));

	        sidePanels[0].setLayout(new FlowLayout(FlowLayout.LEFT)); 
	        sidePanels[0].add(picLabel);
	        labels[0].setVisible(true);
	        labels[0].setFont(new Font("Times New Roman", Font.PLAIN, 18));
	        labels[0].setForeground(Color.BLACK);
	        labels[0].setText(username);// "<html>" +  " " + "<br/>Money:" + "15000" + "</html>"
	        labels[0].setBorder(new EmptyBorder(0,0,0,0));
	        sidePanels[0].add(labels[0]);
	        
	        GridBagConstraints styleLogin = new GridBagConstraints();
			styleLogin.gridx = 4;
			styleLogin.gridy = 2;
			styleLogin.gridwidth = 2;
			sidePanels[0].revalidate();
			sidePanels[0].repaint();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	JLabel[] picLabels;
	JLabel[] nameLabels; // TD
	
	public void initGameTopComponents(String linedQueue, int money) {
		String[] queue = linedQueue.split("\\s++");
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
		playerColors = new PlayerChip[queue.length];
		picLabels = new JLabel[queue.length];
		nameLabels = new JLabel[queue.length];
        sidePanels[0].setLayout(new GridBagLayout());
		for (String name : queue) {
			 try {
			        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + userPfp.get(name) + ".png");
			        BufferedImage myPicture = ImageIO.read(imageStream);
			        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			        picLabel.setBorder(BorderFactory.createLineBorder(colorArray[i], 4)); 
			        picLabels[i] = picLabel;
			        playerColors[i] = new PlayerChip(colorArray[i], name);
			        playerColor.put(name, colorArray[i]);
			        JLabel userName = new JLabel("<html>" + name + "<br> Money:" + money + "</html>");
			        userName.setFont(new Font("Times New Roman", Font.PLAIN, 18));
			        userName.setForeground(Color.BLACK);
			        userName.setBorder(new EmptyBorder(0,0,0,0));
			        
			        nameLabels[i] = userName;
			        
			        sidePanels[0].add(picLabel, stylePfp);
			        sidePanels[0].add(userName, styleUserInfo);
			        stylePfp.gridx+=2;
			        styleUserInfo.gridx+=2;
			        
					
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			 i++;
		}
		sidePanels[0].revalidate();
		sidePanels[0].repaint();
	}
	
	public void showGameInfo(String gameData) {
		if (gameData == null) {
			centerPanel.removeAll();
			initCenterPanelMenu();
			centerPanel.repaint();
			centerPanel.revalidate();
			return;
		}
		centerPanel.setLayout(new GridLayout(1,0));
		JPanel userPanel = new JPanel();
		userPanel.setBackground(new Color(225, 225, 225));
		userPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel settingPanel = new JPanel();
		settingPanel.setBackground(new Color(225, 225, 225));
		settingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanels[4].repaint();
		sidePanels[4].revalidate();
		
		userPanel.setLayout(new GridBagLayout());
		String[] data = gameData.split("\\s++");
		boolean isHost = Boolean.parseBoolean(data[0]);
		JLabel gameName = new JLabel();
		currentlyViewedGameName = data[1];
		gameName.setText(data[1]);
		gameName.setFont(new Font("Serif Bold", Font.BOLD, 28));
		GridBagConstraints styleName = new GridBagConstraints();
		styleName.gridx = 0;
		styleName.gridy = 0;
		styleName.gridwidth = 3;
		styleName.anchor = GridBagConstraints.NORTH;
		
		userData = new ArrayList<String>();
		userPfp = new HashMap<String, String>();
		int i = 0;
		while (!data[i].equals("settings")) {
			if (data[i].equals("player")) {
				userData.add(data[i+1]);
				userPfp.put(data[i+1], data[i+2]);
				i = i + 3;
			} else i++;
		}
		final int settingsIndex = i;
		GridBagConstraints stylePic = new GridBagConstraints();
		stylePic.gridx = 0;
		stylePic.gridy = 1;
		stylePic.anchor = GridBagConstraints.NORTH;
		stylePic.insets.top = 50;
		
		GridBagConstraints styleUsername = new GridBagConstraints();
		styleUsername.gridx = 1;
		styleUsername.gridy = 1;
		styleUsername.anchor = GridBagConstraints.WEST;
		styleUsername.insets.top = 50+15;
		styleUsername.insets.left = 15;
		userPanel.add(gameName, styleName);
		GridBagConstraints styleKick = new GridBagConstraints();
		styleKick.gridx = 2;
		styleKick.gridy = 1;
		styleKick.anchor = GridBagConstraints.WEST;
		styleKick.insets.top = 50;
		styleKick.insets.left = 15;
		for (int j = 0; j < Integer.parseInt(data[settingsIndex+1]); j++) {
			JLabel userName = new JLabel();
			userName.setFont(new Font("Serif Bold", Font.PLAIN, 22));
			userName.setForeground(Color.BLACK);
			JLabel picLabel = new JLabel("Can`t parse image");
			if (j < userData.size()) {
				userName.setText(userData.get(j));
				InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + userPfp.get(userData.get(j)) + ".png");
			    BufferedImage myPicture = null;
			    int picSize = 62;
				try {
					myPicture = ImageIO.read(imageStream);
					picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(picSize, picSize, Image.SCALE_DEFAULT)));
				} catch (IOException e) {	
					e.printStackTrace();
				}
				if (isHost) {
					if (!this.username.equals(userData.get(j))) {
					JButton kickButton = new JButton("Kick");
					kickButton.putClientProperty("index", j);
					kickButton.addActionListener(e -> {
						Program.serverListener.sendMsg(MessageType.KICK, userData.get((Integer) kickButton.getClientProperty("index")));
					});
					userPanel.add(kickButton, styleKick);
					}
					styleKick.gridy++;
				}
			}
			else {
				userName.setText("Empty");
				InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo0.png");
			    BufferedImage myPicture = null;
			    int picSize = 62;
				try {
					myPicture = ImageIO.read(imageStream);
					picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(picSize, picSize, Image.SCALE_DEFAULT)));
				} catch (IOException e) {	
					e.printStackTrace();
				}
			}
				
			userPanel.add(picLabel, stylePic);
			userPanel.add(userName, styleUsername);
			stylePic.gridy++;
			styleUsername.gridy++;
			stylePic.insets.top = 10;
			styleKick.insets.top = 10;
			styleUsername.insets.top = 25;
		}
		stylePic.weighty = 1.0d;
		stylePic.anchor = GridBagConstraints.SOUTH;
		stylePic.insets.top = 0;
		stylePic.insets.bottom = 15;
		styleUsername.weighty = 1.0;
		styleUsername.anchor = GridBagConstraints.SOUTHWEST;
		styleUsername.insets.top = 0;
		styleUsername.insets.bottom = 15;
		JButton connect = new JButton("Join");
		JButton disconnect = new JButton("Leave");
		connect.setPreferredSize(new Dimension(100, 50));
		disconnect.setPreferredSize(new Dimension(100, 50));
		connect.addActionListener((e) -> Program.serverListener.sendMsg(MessageType.JOIN_GAME, currentlyViewedGameName));
		disconnect.addActionListener((e) -> {Program.serverListener.sendMsg(MessageType.LEAVE_GAME, currentlyViewedGameName);});
		
		
		userPanel.add(connect, stylePic);
		userPanel.add(disconnect, styleUsername);
		
		if (isHost) {
			JButton start = new JButton("Start");
			start.setPreferredSize(new Dimension(100, 50));
			start.addActionListener((e) -> {
				Program.serverListener.sendMsg(MessageType.START_GAME, "");
			});
			styleKick.gridy = stylePic.gridy;
			styleKick.weighty = 1.0;
			styleKick.anchor = GridBagConstraints.SOUTHWEST;
			styleKick.insets.top = 0;
			styleKick.insets.bottom = 15;
			userPanel.add(start, styleKick);
		}
		
		centerPanel.add(userPanel);
		
		settingPanel.setLayout(new GridBagLayout());
		JLabel settingsLabel = new JLabel();
		settingsLabel.setText("Settings");
		settingsLabel.setFont(new Font("Serif Bold", Font.BOLD, 28));
		
		GridBagConstraints styleSetName = new GridBagConstraints();
		styleSetName.gridx = 0;
		styleSetName.gridy = 0;
		styleSetName.gridwidth = 2;
		styleSetName.anchor = GridBagConstraints.NORTH;
//		styleSetName.weighty = 1;
		
		settingPanel.add(settingsLabel, styleSetName);
		
		GridBagConstraints styleSetting = new GridBagConstraints();
		styleSetting.gridx = 0;
		styleSetting.gridy = 1;
		styleSetting.anchor = GridBagConstraints.WEST;
		styleSetting.insets.top = 50+15;
		styleSetting.insets.left = 15;
		GridBagConstraints styleHelp = new GridBagConstraints();
		styleHelp.gridx = 1;
		styleHelp.gridy = 1;
		styleHelp.anchor = GridBagConstraints.EAST;
		styleHelp.insets.top = 50+15;
		styleHelp.insets.left = 2;
		//showError(gameData + settingsIndex);
		if (!isHost) {
			for (int j = 0; j < data.length - settingsIndex-1; j++) {
				if (j + 1 >= data.length - settingsIndex-1) {
					styleSetting.weighty = 1.0d;
					styleSetting.anchor = GridBagConstraints.NORTHEAST;
					styleHelp.weighty = 1;
					styleHelp.anchor = GridBagConstraints.NORTHWEST;
				}
				String text;
				if (j!=1) 
					text = settings.get(j).getName() + ": " + data[settingsIndex+j+1];
				else switch(Integer.parseInt(data[settingsIndex+j+1])) {
					case 0:
						text = settings.get(j).getName() + ": classic";
					break;
					default:
						text = settings.get(j).getName() + ": unknown";
					break;
				}
				JLabel setting = new JLabel(text);
				
				setting.setFont(new Font("Serif Bold", Font.PLAIN, 22));
				JButton helpButton = new JButton("?");
				helpButton.setName(Integer.toString(j));
				helpButton.addActionListener(e -> {
					JOptionPane.showMessageDialog(this, settings.get(Integer.parseInt(helpButton.getName())).getHelpInfo(),"Help", JOptionPane.INFORMATION_MESSAGE);
				});
				settingPanel.add(setting, styleSetting);
				settingPanel.add(helpButton, styleHelp);
				styleHelp.gridy++;
				styleSetting.gridy++;
				styleSetting.insets.top = 15;
				styleHelp.insets.top = 15;
			} // TODO EXCEPTION HANDLINGмне 
		} else {
			styleHelp.gridx++;
			GridBagConstraints styleComp = new GridBagConstraints();
			styleComp.gridx = 1;
			styleComp.gridy = 1;
			styleComp.anchor = GridBagConstraints.WEST;
			styleComp.insets.top = 50+15;
			styleComp.insets.left = 2;
			styleSetting.anchor = GridBagConstraints.EAST;
			for (int j = 0; j < data.length - settingsIndex-1; j++) {
				if (j + 1 >= data.length - settingsIndex-1) {
					styleHelp.weighty = 1;
					styleHelp.anchor = GridBagConstraints.NORTHWEST;
					styleSetting.anchor = GridBagConstraints.NORTHWEST;
					styleComp.anchor = GridBagConstraints.NORTHWEST;
				} 
				JLabel setting = new JLabel(settings.get(j).getName() + ": ");
				setting.setFont(new Font("Serif Bold", Font.PLAIN, 22));
				JComponent component = settings.get(j).getComponent();
				initTextField(component, data[settingsIndex+j+1]);
				JButton helpButton = new JButton("?");
				helpButton.setName(Integer.toString(j));
				helpButton.addActionListener(e -> {
					JOptionPane.showMessageDialog(this, settings.get(Integer.parseInt(helpButton.getName())).getHelpInfo(),"Help", JOptionPane.INFORMATION_MESSAGE);
				});
				settingPanel.add(setting, styleSetting);
				settingPanel.add(component, styleComp);
				settingPanel.add(helpButton, styleHelp);
				styleSetting.gridy++;
				styleComp.gridy++;
				styleHelp.gridy++;
				styleSetting.insets.top = 15;
				styleComp.insets.top = 15;
				styleHelp.insets.top = 15;
			} 
			JButton save = new JButton("Save");
			JButton saveDefault = new JButton("<html> Save as </br> default </html>");
			save.setPreferredSize(new Dimension(100, 50));
			saveDefault.setPreferredSize(new Dimension(100, 50));
			save.addActionListener((e) -> {
				String mp = ((JTextField) settings.get(0).getComponent()).getText();
				String tk = ((JTextField) settings.get(2).getComponent()).getText(); 
				if (mp != null && !mp.isEmpty() && mp.matches("\\S+") && tk != null && !tk.isEmpty() && tk.matches("\\S+")) {
		    		Program.serverListener.sendMsg(MessageType.SAVE_SETTINGS, mp + " " + ((JComboBox<String>) settings.get(1).getComponent()).getSelectedIndex() + " " + tk);
		    	} else {
		    		showError("Settings can`t contain whitespaces.");
		    	}
			});
			saveDefault.addActionListener((e) -> {
				String mp = ((JTextField) settings.get(0).getComponent()).getText();
				String tk = ((JTextField) settings.get(2).getComponent()).getText(); 
				if (mp != null && !mp.isEmpty() && mp.matches("\\S+") && tk != null && !tk.isEmpty() && tk.matches("\\S+")) {
		    		Program.serverListener.sendMsg(MessageType.SAVE_SETTINGS, mp + " " + ((JComboBox<String>) settings.get(1).getComponent()).getSelectedIndex() + " " + tk);
		    	} else {
		    		showError("Settings can`t contain whitespaces.");
		    	}
			});
			
			styleSetting.weighty = 1.0d;
			styleComp.weighty = 1;
			styleSetting.anchor = GridBagConstraints.SOUTHEAST;
			styleComp.anchor = GridBagConstraints.SOUTHWEST;
			styleComp.insets.top = 0;
			styleComp.insets.bottom = 15;
			styleComp.insets.left = 5;
			styleSetting.insets.top = 0;
			styleSetting.insets.bottom = 15;
			styleSetting.insets.right = 5;
			
			settingPanel.add(save, styleSetting);
			settingPanel.add(saveDefault, styleComp);
		}// TODO buttons for interactiong
		centerPanel.add(settingPanel);
//		removeFlag = true;

		centerPanel.repaint();
		centerPanel.revalidate();
	}
	
	public void refreshGameList(String games) {
		String words[] = games.split("\\s++");
		if (listModel != null)
			listModel.removeAllElements(); // TODO
		else listModel = new DefaultListModel<String>();
	    for (int i = 0; i < words.length; i += 5) {
	        if (words[i].equals("game")) {
	            String gameMode = "unknown";
	            switch (Integer.parseInt(words[i + 3])) {
	                case 0:
	                    gameMode = "classic";
	                break;
	            }
	            listModel.addElement("<html> Game: " + words[i + 1] + "; Players: " + words[i+2] + ";<br/> Gamemode: " + gameMode + "; Max players: " + words[i + 4] + ";");
	        }
	    }
	}

	public void initMainCenterComponents() {
		sidePanels[4].setLayout(new GridBagLayout());
	    listModel = new DefaultListModel<String>();
	    sidePanels[4].removeAll();
	    gameList = new JList<String>(listModel);
	    gameList.setFont(new Font("Times New Roman", Font.PLAIN, 18));
	    gameList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                	String testingValue = gameList.getSelectedValue();
                	String selectedValue = null;
                	if (testingValue == null)
                		return;
                	else { selectedValue = testingValue.toString();}
                	int startIndex = selectedValue.indexOf("Game: ") + 6;
                	int endIndex = selectedValue.indexOf("; Players:");
                	if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                	    String gameName = selectedValue.substring(startIndex, endIndex);
                	    Program.serverListener.sendMsg(MessageType.GET_GAME, gameName);
                	} else {
                	    showError("For some reason I can`t find selected game`s name.");
                	}
                }
            }
        });
	    gameList.setBackground(new Color(240, 240, 240));
	    gameList.repaint();
	    JScrollPane test = new JScrollPane(gameList);
	    test.setVisible(true);
	    test.setBorder(new EmptyBorder(0,0,0,0));
	    test.setBorder(BorderFactory.createLineBorder(Color.black));
	    test.setBackground(new Color(225, 225, 225));

	    GridBagConstraints styleTest = new GridBagConstraints();
	    styleTest.gridx = 0;
	    styleTest.gridy = 0;
	    styleTest.anchor = GridBagConstraints.WEST;
	    styleTest.fill = GridBagConstraints.BOTH;
	    styleTest.weightx = 1.0;
	    styleTest.weighty = 1.0;

	    centerPanel = new JPanel();
	    centerPanel.setBackground(new Color(225, 225, 225));
	    centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, test, centerPanel);
	    sidePanels[4].add(pane, styleTest);
	    pane.setDividerLocation(0.6d);
	    pane.setDividerSize(0);
	    pane.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	        	 pane.setDividerLocation(0.25d);
	        }
	    });
	    sidePanels[4].revalidate();
	    sidePanels[4].repaint();
	}

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
	
	public void showError(String message, String code) {
	    showDialog(message, "Error code: " + code, JOptionPane.WARNING_MESSAGE);
	}

	
	public void showMessage(String message) {
	    showDialog(message, "Operation successful", JOptionPane.PLAIN_MESSAGE);
	}

	
	public void showError(String message) {
	    showError(message, "unknown");
	}

	private void showDialog(String message, String title, int messageType) {
	    JDialog dialog = new JDialog(this, title, Dialog.ModalityType.MODELESS);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setSize(400, 200);
	    dialog.setLocationRelativeTo(this);

	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());

	    JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
	    messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    
	    if (messageType == JOptionPane.WARNING_MESSAGE) {
	        messageLabel.setForeground(Color.RED);
	    } else if (messageType == JOptionPane.PLAIN_MESSAGE) {
	        messageLabel.setForeground(Color.BLACK);
	    }
	    
	    panel.add(messageLabel, BorderLayout.CENTER);

	    JButton closeButton = new JButton("Close");
	    closeButton.addActionListener(e -> dialog.dispose());
	    closeButton.setForeground(Color.WHITE);
	    closeButton.setBackground(Color.GRAY);
	    closeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(closeButton);
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    
	    panel.add(buttonPanel, BorderLayout.SOUTH);
	    panel.setBackground(Color.LIGHT_GRAY);
	    
	    dialog.add(panel);
	    dialog.setVisible(true);
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
	        for (JPanel p : this.sidePanels) {
	            p.removeAll();
	            Window.this.remove(p);
	        }

	        Window.this.setLayout(new BorderLayout());
	        for (int i = 0; i < 5; i++) {
	            sidePanels[i] = new JPanel();
	            labels[i] = new JLabel();
	            textField[i] = new JTextField();
	            sidePanels[i].setPreferredSize(new Dimension(60, 60));//150
	            sidePanels[i].setLayout(new BorderLayout());
	            textField[i].setBorder(new EmptyBorder(0,10,0,0));
	            textField[i].setPreferredSize(new Dimension(300,50));
	            labels[i].setBorder(new EmptyBorder(0,10,0,0));
	            labels[i].setFont(new Font("Times New Roman", Font.PLAIN, 18));
	        }
	        for (int i = 0; i < 5; i++) {
	            if (i != 1)
	                Window.this.add(sidePanels[i], sides[i]);
	        }

	        Window.this.playerColor.clear();
	        Window.this.map.clear();
	        Window.this.globalMapPanel.removeAll();
	        Window.this.playerColors = null;
	        Window.this.userPfp.clear();
	        Window.this.receiverTradePosition.clear();
	        Window.this.senderTradePosition.clear();
	        Window.this.swingTimer.stop();
	        Window.this.moneyAmount = 15000;
	        delayedMsgs.clear();
	        if (nicknameComboBox != null)
	            Window.this.nicknameComboBox.removeAllItems();
	        messageCount = 0;
	        
	        
	        debugSetPanelBackground();

	        initMainLeftComponents();
	        initMainTopComponents(defaultPfp);
	        initMainCenterComponents();
	        initCenterPanelMenu();
	        Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");

	        Window.this.revalidate();
	        Window.this.repaint();
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
	
	boolean isAuction = false;
	
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
	
	public JSplitPane infoPanel;
	JButton roll = new JButton("Roll dice");
	JLabel timerLabel = new JLabel("Initializing timer...");
	JButton openTrade;
	
	@Override
	public void handle(Message msg) {
		switch(msg.getMsgType()) {
			case AUTH:
				this.username = textField[0].getText();
				Program.serverListener.setAuthToken(msg.getAuthToken());
				initMainLeftComponents();
				defaultPfp = Integer.parseInt(msg.getText());
				initMainTopComponents(defaultPfp);
				initMainCenterComponents();
				initCenterPanelMenu();
				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
	        break;
			case REG:
				this.username = textField[0].getText();
				Program.serverListener.setAuthToken(msg.getAuthToken());
				initMainLeftComponents();
				defaultPfp = Integer.parseInt(msg.getText());
				initMainTopComponents(defaultPfp);
				initMainCenterComponents();
				initCenterPanelMenu();
				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
	        break;
			case ERROR:
				showError(msg.getText());
			break;
			case SEARCH_GAMES:
				try {
					refreshGameList(msg.getText());
				} catch(Exception ex) {
					showError(ex.toString() + ex.getStackTrace()[0].toString());
				}
	        break;
			case GET_GAME:
				centerPanel.removeAll();
				showGameInfo(msg.getText());
			break;
			case SAVE_AS_DEFAULT:
			case SAVE_SETTINGS:
				showMessage(msg.getText());
			break;
			case CHANGE_PFP:
				sidePanels[0].removeAll();
				defaultPfp = Integer.parseInt(msg.getText());
				initMainTopComponents(Integer.parseInt(msg.getText()));
			break;
			case START_GAME:
				this.remove(sidePanels[2]);
				this.remove(sidePanels[3]);
				sidePanels[4].removeAll();
				sidePanels[0].removeAll();
				sidePanels[0].setPreferredSize(new Dimension(70, 70));
				initGameTopComponents(msg.getText(), 15000);
				Program.serverListener.sendMsg(MessageType.LOAD_MAP, "");
			break;
			case LOAD_MAP:
				this.remove(sidePanels[2]);
				this.remove(sidePanels[3]);
				sidePanels[4].removeAll();
				globalMapPanel = initGameComponents();
				try {
					displayMap(msg.getText(), globalMapPanel);
				} catch (OutOfMemoryError ex) {
					showError("Java could not allocate enough memory to load map. Increase to JVM memory heap amount recommended.");
				}
				
				this.revalidate();
				this.repaint();
			break;
			case GET_COMPANY:
				initCompanyInfoPanel(infoP, msg.getText());
			break;
			case SEND_MESSAGE:
				append(msg.getText(),  Color.black, new Color(225, 225, 225), false, false, false);
			break;
			case SEND_SYSTEM_MESSAGE:
				append(msg.getText(),  Color.black, new Color(225, 225, 225), true, false, false);
			break;
			case SEND_DELAYED_MESSAGE:
				delayedMsgs.add(msg.getText());
			break;
			case ROLL:
				stopContinuousRoll();
				startTimedRoll(1500, msg.getText());
			break;
			case START_TURN:
				timerLabel.setVisible(true);
				roll.setVisible(true);
				isAuction = false;
				if (tradePanel != null)
					tradePanel.setVisible(false);
				createRollMenuPanel(map.size());
			break;
			case JAIL_START:
				String panelStyle = msg.getText();
				switch (panelStyle) {
					case "1":
						ActionListener ac = new ActionListener() {
						@Override
						    public void actionPerformed(ActionEvent e) {
						    	if (moneyAmount >= 1000) {
						    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
									clearMenuPanel();
						    	} else {
						    		showError("Not enough money.");
						    	}
								
						  	}
						};
						ActionListener ac1 = new ActionListener() {
						    @Override
						    public void actionPerformed(ActionEvent e) {
								Program.serverListener.sendMsg(MessageType.ROLL, null);
								clearMenuPanel();
							  
						  	}
						};
						createTwoButtonPanel("<html>You`ve got to jail. You can pay or try to <br>roll for double to quit.</html>", "Pay (1000k UAH)",
								"Roll for double", ac, ac1);
					break;
					case "2":
						ActionListener payListener = new ActionListener() {
						    @Override
						    public void actionPerformed(ActionEvent e) {
						    	if (moneyAmount >= 1000) {
						    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
									clearMenuPanel();
						    	} else {
						    		showError("Not enough money.");
						    	}
						  	}
						};
						createPayMenuPanel("<html>You`ve exhausted all your attempts to get out of <br> jail and is forced to pay. </html>", "Pay (1000k UAH)", payListener);
					break;
					default:
					showError("Error parsing jail panel style...");
					break;
				}
			break;
			case UPDATE_TIMER:
				String message = msg.getText();
			    String[] deadlineString = message.split("\\s++"); 
			    if (deadlineString[1] != null && !deadlineString[1].isEmpty()) {
			        try {
			            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			            Date deadline = dateFormat.parse(deadlineString[1]);
			            try {
			            	startTimer(deadline, deadlineString[0]);
			            } catch (Exception ex) {
			            	Program.serverListener.sendMsg(MessageType.DEBUG, "Exception: " + ex.getMessage());
			            }
			            
			        } catch (ParseException e) {
			            e.printStackTrace();
			        }
			    }
			break;
			case TIMEOUT:
				if (username.equals(msg.getText())) {
					clearMenuPanel();
					openTrade.setVisible(false);
					giveUp.setVisible(false);
					showMessage("You have been eliminated by time. You can still watch the game unfold.");
				}
				append(msg.getText() + " have been eliminated!",  Color.black, new Color(225, 225, 225), true, false, false);
				int i = 0;
				for (PlayerChip chip : playerColors) {
					if (chip.getUsername().equals(msg.getText())) {
						globalMapPanel.remove(chip);
						picLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));;
						nameLabels[i].setText("<html>" + msg.getText() + "<br> bankrupt </html>");;
						picLabels[i].repaint();
						nameLabels[i].repaint();
						globalMapPanel.repaint();
						picLabels[i].revalidate();
						nameLabels[i].revalidate();
						globalMapPanel.revalidate();
						break;
					}
					i++;
				}
			break;
			case UPDATE_MONEY:
				Program.serverListener.sendMsg(MessageType.DEBUG, msg.getText());
			    String data[] = msg.getText().split("\\s++");
			    for (int z = 0; z < data.length; z += 2) {
			        if (username.equals(data[z])) {
			            moneyAmount = Integer.parseInt(data[z + 1]);
			        }
			        int j = 0;
			        for (PlayerChip chip : playerColors) {
			            if (chip.getUsername().equals(data[z])) {
			                nameLabels[j].setText("<html>" + data[z] + "<br> Money:" + data[z + 1] + "</html>");
			                nameLabels[j].repaint();
			                nameLabels[j].revalidate();
			                break;
			            }
			            j++;
			        }
			    }
		    break;
			case UPDATE_CARD:
				String cardData[] = msg.getText().split("\\s++");
				JAbstractCard card = map.get(Integer.parseInt(cardData[0]));
				((JCompanyCard) card).changeOwner(Window.getColor(cardData[1]));
				((JCompanyCard) card).changeCost(cardData[2]);
				int starAmount = Integer.parseInt(cardData[3]);
				if (starAmount != 0)
					starAmount--;
				((JCompanyCard) card).changeStarAmount(starAmount);
				((JCompanyCard) card).setLayouted(Boolean.parseBoolean(cardData[4]));
				((JCompanyCard) card).repaintCard();
			break;
			case AUCTION:
				String[] auctionData = msg.getText().split("\\s++");
				isAuction = true;
				createTwoButtonPanel("<html>Do you want to take part in auction for <br> company " + auctionData[0].replace("_", " ") + 
						"</html>", "Ramp up price " + auctionData[1], "Decline", 
						new ActionListener() {
						    @Override
						    public void actionPerformed(ActionEvent e) {
						    	if (moneyAmount >= Integer.parseInt(auctionData[1])) {
									Program.serverListener.sendMsg(MessageType.AUCTION_YES, null);
									isAuction = false;
									clearMenuPanel();
						    	} else {
						    		showError("You don`r have enough money...");
						    	}
						  	}
						}, 
						new ActionListener() {
					    @Override
					    public void actionPerformed(ActionEvent e) {
							Program.serverListener.sendMsg(MessageType.AUCTION_NO, null);
							clearMenuPanel();
							isAuction = false;
					  	}
				});
			break;
			case UPDATE_CHIP:
				String[] chipData = msg.getText().split("\\s++");
				
				int[] clientPosition = new int[2];
				clientPosition[0] = Integer.parseInt(chipData[1]);
				clientPosition[1] = Integer.parseInt(chipData[2]);
				for (PlayerChip chip : playerColors) {
					if (chip.getUsername().equals(chipData[0])) {
						alteredMovement = new ChipMovement(chip, clientPosition[0], clientPosition[1]);
						break;
					}
				}
			break;
			case DRAW_LAYOUT:
				String cards[] = msg.getText().split("\\s++");
				ArrayList<JCompanyCard> cds = new ArrayList<JCompanyCard>();
				for (String st : cards) {
					cds.add((JCompanyCard) map.get(Integer.parseInt(st)));
				}
				for (JCompanyCard target : cds) {
					if (target.isLayouted()) {
						target.decreaseLayoutTimer();
					} else
						target.setLayouted(true);
					target.repaintCard();
				}
			break;
			case TRADE_OFFER:
				createTradeOffer(msg.getText());
			break;
			case WIN:
				String username = msg.getText();
				createWinDialog(username, playerColor.get(username), "logo" + userPfp.get(username));
			break;
			case CLEAR_TRADE:
				tradePanel.setVisible(false);
			break;
			case CLEAR_MENU:
				clearMenuPanel();
			break;
			case ROLLBACK_PANEL:
				bg.setVisible(true);
			break;
			case KEEP_ALIVE:
				System.out.println("pong");
			break;
		}
	}
	
	public void flushDelayedMessages() {
		for (String s : delayedMsgs) {
			append(s,  Color.black, new Color(225, 225, 225), true, false, false);
		}
		delayedMsgs.clear();
	}

	Timer swingTimer;
    private long timeLeft;
	
   private void startTimer(Date deadline, String username) {
        if (swingTimer != null) {
            swingTimer.stop();
        }
		 String currentThread = Thread.currentThread().getName();
        timeLeft = deadline.getTime() - System.currentTimeMillis();
        ActionListener ac;
        if (timeLeft < 0) {
            final String warning; 
        	timeLeft = 90 * 1000;
        	warning = "<br>Alert: timezone is set incorrectly due to what<br> timer could be unreliable!";
            ac = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timeLeft -= 1000;
                    if (timeLeft <= 0) {
                        swingTimer.stop();
                    } else {
                        updateTimerDisplay(timeLeft, username, warning);
                    }
                }
            };
        } else {
            ac = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timeLeft -= 1000;
                    if (timeLeft <= 0) {
                        swingTimer.stop();
                    } else {
                        updateTimerDisplay(timeLeft, username, "");
                    }
                }
            };
        }
    
        swingTimer = new Timer(1000, ac);
        swingTimer.start();
    }

   private void updateTimerDisplay(long timeLeft, String username, String warning) {
	   EventQueue.invokeLater(() -> {
		   	long secondsLeft = timeLeft / 1000;
	        timerLabel.setText("<html>Time left for user " + username + ": " + secondsLeft + " seconds" + warning + "</html>");
	        timerLabel.setVisible(true);
	        timerLabel.revalidate();
	        timerLabel.repaint();
	        this.repaint();
	        this.revalidate();
    	});
    }
	
	int menuPanelStatus = 0;    
	    
	JLayeredPane globalMapPanel;
	
	static Color getColor(String col) {
		Color color = Color.white;
	    switch (col.toLowerCase()) {
	    case "black":
	        color = Color.BLACK;
	        break;
	    case "blue":
	        color = Color.BLUE;
	        break;
	    case "cyan":
	        color = Color.CYAN;
	        break;
	    case "darkgray":
	        color = Color.DARK_GRAY;
	        break;
	    case "gray":
	        color = Color.GRAY;
	        break;
	    case "green":
	        color = Color.GREEN;
	        break;
	    case "yellow":
	        color = Color.YELLOW;
	        break;
	    case "lightgray":
	        color = Color.LIGHT_GRAY;
	        break;
	    case "magenta":
	        color = Color.MAGENTA;
	        break;
	    case "orange":
	        color = Color.ORANGE;
	        break;
	    case "pink":
	        color = Color.PINK;
	        break;
	    case "red":
	        color = Color.RED;
	        break;
	    case "white":
	        color = Color.WHITE;
	        break;
	   }
	    return color;
	}
	

	@Override
	public void handleChange(JLabel newPicture) {
		if (selectedPicture != null)
			selectedPicture.setBorder(null);
		selectedPicture = newPicture;
		newPicture.setBorder(BorderFactory.createLineBorder(Color.cyan, 7));
	}
	
	JTextPane chat;
	JPanel infoP;
	/*
	 * @author 
	 */
	
	public void append(String text, Color fg, Color bg, boolean bold, boolean italic, boolean underline) {
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
	
	public String formatMessage(String text) {
		String result = "  ";
		String words[] = text.split("\\s++");
		for (String word : words) {
			int width = this.getGraphics().getFontMetrics().stringWidth(word);
			if (chat.getWidth() < width - 55) {
				int splitIndex = word.length()/2;
				word = formatMessage(word.substring(1, splitIndex)) + " " + 
						formatMessage(word.substring(splitIndex));
			}
			result += word + " ";
		}	
		return result;
	}

	public JPanel initInfoPanel() {
	    JPanel chatPanel = new JPanel();
	    JPanel companyPanel = new JPanel();
	    
	    chatPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    chatPanel.setLayout(new GridBagLayout());
	    
	    chat = new JTextPane();
	    chat.setBackground(new Color(225, 225, 225)); 
	    chat.setBorder(BorderFactory.createLineBorder(Color.black));
	    chat.setEditable(false);
	    chat.setLayout(new GridBagLayout());
	    
	    JScrollPane scrollableChat = new JScrollPane(chat);
	   // scrollableChat.setPreferredSize(new Dimension(150,150));
	    
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
	//    styleChat.weighty = 1.0; // 10% of remaining space
	    styleChat.anchor = GridBagConstraints.SOUTHEAST;
	//    styleChat.fill = GridBagConstraints.HORIZONTAL;
	    chatPanel.add(sendButton, styleChat);
	    
	    companyPanel.setBackground(new Color(225, 225, 225));
	    companyPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    companyPanel.setLayout(new GridBagLayout());
	    
		openTrade = new JButton("TRADE");
		GridBagConstraints styleTrade = new GridBagConstraints();
		styleTrade.gridx = 1; // TODO
		styleTrade.gridy = 4;
		styleTrade.anchor = GridBagConstraints.CENTER;
		styleTrade.gridwidth = (map.size()/4)-1;
		
		openTrade.addActionListener((e) -> {
			if (isTrade == false && bg.isVisible() && !isAuction) {
				createTradeInterface();
			}
			else
				showError("It`s not your turn.");
		});
	    
	    initControlPanel(companyPanel);
	    
	    infoPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatPanel, companyPanel);
	    
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
	    
	//    append("ezik: hi", Color.black, new Color(225, 225, 225), false, false, false);
	    
	    infoPanel.repaint();
	    infoPanel.revalidate();
	    return companyPanel;
	}
	
	public void initControlPanel(JPanel info) {
		info.removeAll();
		info.setLayout(new GridBagLayout());
		
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
	    
	    info.add(logoLabel, tgbc);
	    tgbc.gridx = 0;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    info.add(openTrade, tgbc);
		
	    JButton pray = new JButton("Бисмиллях");
	    
	    pray.addActionListener((e) -> {
	    	if (new Random().nextInt(1000) < 50) {
	    		showMessage(":)");
	    	}
	    });
	    
	    tgbc.gridx = 1;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    info.add(pray, tgbc);
	    
	    giveUp.addActionListener((e) -> {
	    	giveUp.setVisible(false);
	    	Program.serverListener.sendMsg(MessageType.SURRENDER, null);
	    });
	    
	    tgbc.gridx = 2;
	    tgbc.gridy = 1;
	    tgbc.gridwidth = 1;
	    
	    giveUp.setVisible(true);
	    info.add(giveUp, tgbc);
	    
	    JLabel hint = new JLabel("<html>(press on any company to<br> get detailed info here)</html>");
	    tgbc.gridx = 0;
	    tgbc.gridy = 2;
	    tgbc.gridwidth = 3;
	    info.add(hint, tgbc);
	    
	    info.repaint();
	    info.revalidate();
	}
	
	public void initCompanyInfoPanel(JPanel info, String serverData) {
		info.removeAll();
		String data[] = serverData.split("\\s++");
		JLabel titleText = new JLabel(data[0].replace("_", " "));
		titleText.setFont(new Font("Serif Bold", Font.BOLD, 22));
		titleText.setHorizontalAlignment(SwingConstants.CENTER);
		info.setLayout(new GridBagLayout());
		String position = data[8];
		
		GridBagConstraints styleTitle = new GridBagConstraints();
		styleTitle.gridx = 0;
		styleTitle.gridy = 0;
		styleTitle.gridwidth = 4;
		styleTitle.anchor = GridBagConstraints.NORTH;
		styleTitle.fill = GridBagConstraints.HORIZONTAL;
		
		info.add(titleText, styleTitle);
		
		JLabel subtitleText = new JLabel(data[1]);
		subtitleText.setFont(new Font("Serif Bold", Font.BOLD, 16));
		subtitleText.setHorizontalAlignment(SwingConstants.CENTER);
		styleTitle.gridy++;
		styleTitle.insets.bottom = 35;
		info.add(subtitleText, styleTitle);
		
        if (((JCompanyCard) map.get(Integer.parseInt(position))).isLayouted()) {
			JLabel layoutText = new JLabel("LAYOUTED!");
			subtitleText.setFont(new Font("Serif Bold", Font.BOLD, 16));
			subtitleText.setHorizontalAlignment(SwingConstants.CENTER);
			styleTitle.gridy++;
			styleTitle.insets.bottom = 35;
			info.add(layoutText, styleTitle);
		}
		
		styleTitle.insets.bottom = 0;
        String[] rentString = data[2].split("_");

        JLabel label = new JLabel("Monopoly levels price:");
        styleTitle.gridx = 1;
        styleTitle.gridwidth = 1;
        styleTitle.gridy++;
        styleTitle.fill = GridBagConstraints.NONE;
        styleTitle.anchor = GridBagConstraints.NORTHWEST;
        styleTitle.insets.left = 20;
        info.add(label, styleTitle);
        for (int i = 0; i < rentString.length; i ++) {
        	JLabel lvl = new JLabel("Level " + (i+1) + ": " + rentString[i] + "UAH");
        	styleTitle.gridy++;
        	if (rentString[i].equals(data[3])) {
                styleTitle.fill = GridBagConstraints.HORIZONTAL;
        		lvl.setText("<html><U> Level " + (i+1) + ": " + rentString[i] + "UAH</U></html>");
        	}
        	info.add(lvl, styleTitle);
        	styleTitle.fill = GridBagConstraints.NONE;
        }
   		styleTitle.weighty = 0.0d;
		styleTitle.gridheight = 2;
        styleTitle.gridwidth = 1;
        styleTitle.insets.left = 0;
        JLabel currentRent = new JLabel("<html>Current rent: <br>" + data[3] + "UAH</html>");
        styleTitle.gridx = 0;
        styleTitle.gridy = 2;
        styleTitle.fill = GridBagConstraints.HORIZONTAL;
        info.add(currentRent, styleTitle);
        JLabel currentPrice = new JLabel("<html>Current price: <br>" + data[4] + "UAH</html>");
        styleTitle.gridy+=2;
        info.add(currentPrice, styleTitle);
        JLabel owner = new JLabel("<html>Owner: " + data[5] + "</html>");
		styleTitle.gridheight = 1;
        styleTitle.gridy+=2;
        info.add(owner, styleTitle);
        JLabel cardAmount = new JLabel("<html>Competitors: " + data[6] + "</html>");
        styleTitle.gridy++;
        info.add(cardAmount, styleTitle);
        JLabel buildable = new JLabel("<html>Investable: <br>" + data[7] + "</html>");
		styleTitle.gridheight = 2;
        styleTitle.gridy++;
        info.add(buildable, styleTitle);
        JLabel obligation;
        if (((JCompanyCard) map.get(Integer.parseInt(position))).isLayouted())
        	obligation = new JLabel("<html>DeLayout price: <br>" + Integer.parseInt(data[4])/2+500 + "UAH</html>");
        else
        	obligation = new JLabel("<html>Layout price: <br>" + Integer.parseInt(data[4])/2 + "UAH</html>");    
        styleTitle.gridy+=2;
        info.add(obligation, styleTitle);
        if (data.length == 10) {
        	styleTitle.gridx++;
        	String values[] = data[9].split("_");
        	if (values.length == 1) {
               	JLabel upgradeCost = new JLabel("<html>Next upgrade cost: <br>" + values[0] + "</html>");
        		styleTitle.gridheight = 2;
                styleTitle.gridy++;
                info.add(upgradeCost, styleTitle);
        	} else {
        		if (values[0].equals("0")) {
        			JLabel upgradeCost = new JLabel("<html>Sell upgrade cost: <br>" + values[1] + "</html>");
            		styleTitle.gridheight = 2;
                    styleTitle.gridy++;
                    info.add(upgradeCost, styleTitle);
        		} else {
        			JLabel upgradeCost = new JLabel("<html>Next upgrade cost: <br>" + values[0] + "</html>");
            		styleTitle.gridheight = 2;
                    styleTitle.gridy++;
                    info.add(upgradeCost, styleTitle);
                    JLabel sellUpgradeCost = new JLabel("<html>Sell upgrade cost: <br>" + values[1] + "</html>");
            		styleTitle.gridy+=2;
                    info.add(sellUpgradeCost, styleTitle);
        		}
        	}
        	
            styleTitle.gridx--;
        }
    	styleTitle.fill = GridBagConstraints.NONE;
        JButton laybut = new JButton("Layout"); 
        laybut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.LAYOUT_CARD, position);
        });
        styleTitle.gridy+=2;
        styleTitle.anchor = GridBagConstraints.SOUTH;
        styleTitle.insets.left = 0;
        styleTitle.fill = GridBagConstraints.HORIZONTAL;
        styleTitle.weighty = 1.0d;
        styleTitle.insets.bottom = 50;
        info.add(laybut, styleTitle);
        JButton layBut = new JButton("DeLayout");
        layBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.DELAYOUT_CARD, position);
        });
    //    layBut.setPreferredSize(new Dimension(150, 50));
        styleTitle.gridx++;
        info.add(layBut, styleTitle);
        styleTitle.gridy++;
        styleTitle.insets.bottom = 25;
        JButton investBut = new JButton("Invest");
        investBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.UPGRADE_CARD, position);
        });
      //  investBut.setPreferredSize(new Dimension(150, 50));
        styleTitle.gridx = 0;
        styleTitle.weighty = 0.0d;
        info.add(investBut, styleTitle);
        JButton deinvestBut = new JButton("DeInvest");
        deinvestBut.addActionListener((e) -> {
        	Program.serverListener.sendMsg(MessageType.SELL_UPGRADE, position);
        });
        //deinvestBut.setPreferredSize(new Dimension(150, 50));
        styleTitle.gridx++;
        info.add(deinvestBut, styleTitle);
        JButton buybut = new JButton("Clear");
        buybut.addActionListener((e) -> {
        	initControlPanel(info);
        });
        styleTitle.gridwidth = 2;
        styleTitle.gridx = 0;
        styleTitle.insets.bottom = 0;
        info.add(buybut, styleTitle);
        
        info.revalidate();
        info.repaint();
    }
	
	public JLayeredPane initGameComponents() {
		GridBagConstraints styleTest = new GridBagConstraints();
	    styleTest.gridx = 0;
	    styleTest.gridy = 0;
	    styleTest.anchor = GridBagConstraints.WEST;
	    styleTest.fill = GridBagConstraints.BOTH;
	    styleTest.weightx = 1.0;
	    styleTest.weighty = 1.0;
	    infoP = initInfoPanel();
	    JLayeredPane mapPanel = new JLayeredPane();
	    mapPanel.setBackground(new Color(225, 225, 225));
	    mapPanel.setLayout(new GridBagLayout());
	    
	    JScrollPane panel2 = new JScrollPane(mapPanel);
	    panel2.setBackground(new Color(225, 225, 225));
	    panel2.setBorder(BorderFactory.createLineBorder(Color.black));
	    JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoPanel, panel2);
	    sidePanels[4].add(pane, styleTest);
	    pane.setDividerLocation(0.6d);
	    pane.setDividerSize(0);
	    pane.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	        	 pane.setDividerLocation(0.25d);
	        }
	    });
	    sidePanels[4].revalidate();
	    sidePanels[4].repaint();
	    return mapPanel;
	}
	ArrayList<JAbstractCard> map = new ArrayList<JAbstractCard>();
	
	public void displayMap(String mapData, JLayeredPane mapPanel) {
		String data[] = mapData.split("\\s++");
		int corner = -1;
		for (int i = 0; i < data.length;) {
			if (data[i].equals("card")) {
				if (data[i+1].equals("company")) {
						String name = data[i+2].toLowerCase();
						Color owner = Window.getColor(name);
						String cardName = data[i+4].replace("_", " "); 
						JCompanyCard card = new JCompanyCard(owner, data[i+3], cardName, data[i+5] , corner, map.size());
						card.addOnClickReaction(this::handleCompanyClick);
						map.add(card);
						i = i + 6;
						continue;
				}
				if (data[i+1].equals("event")) {
					JEventCard card = new JEventCard(data[i+2], data[i+3], corner);
					map.add(card);
					i = i + 4; 
					continue;
				}
				if (data[i+1].equals("corner")) {
					JCornerCard card = new JCornerCard(data[i+2], data[i+3]);
					map.add(card);
					if (corner < 4) {
						corner++;
					} else corner = 0;
					i = i + 4;
					continue;
				}
			} else i++;
		}
		GridBagConstraints styleTest = new GridBagConstraints();
		styleTest.gridx = 0;
		styleTest.gridy = 0;
		int counter = -1;
		for (JAbstractCard card : map) {
			mapPanel.add(card, styleTest, 0);
			//mapPanel.setComponentZOrder(card, 5);
			if (card instanceof JCornerCard)
				counter++;
			switch(counter) {
				case 0:
					styleTest.gridx++;
				break;
				case 1: 
					styleTest.gridy++;
				break;
				case 2:
					styleTest.gridx--;
				break;
				case 3:
					styleTest.gridy--;
				break;
			}
		}
		int amount = 0;
		for (PlayerChip chip : playerColors) {
			int[] pos = chip.getPosition();
			int[] offset = calcOffset(amount);
			amount++;
			chip.setOffset(offset);
			displayChip(chip, 0, 0);
		}
		
		GridBagConstraints styleTimer = new GridBagConstraints();
		styleTimer.gridx = 1; // TODO
		styleTimer.gridy = 6;
		styleTimer.anchor = GridBagConstraints.CENTER;
		styleTimer.gridwidth = (map.size()/4)-1;
		timerLabel.setFont(new Font("Serif Bold", Font.BOLD, 24));
		
		ActionListener[] listeners = roll.getActionListeners();
        if (listeners  != null) {
            for (ActionListener listener : listeners) {
            	roll.removeActionListener(listener);
            }
        }
		roll.addActionListener((e) -> {
			clearMenuPanel();
			roll.setVisible(false);
			createRollResultPanel();
			startContinuousRoll(username);
			Program.serverListener.sendMsg(MessageType.ROLL, null);;
		});
		
	
		
		roll.setVisible(false);
		
		mapPanel.add(timerLabel, styleTimer, 0);
		
		mapPanel.repaint();
		mapPanel.revalidate();
	}
	
	private class MoneyPanel {
	    JPanel panel;
	    JPanel itemsPanel;
	    JTextField moneyField;
	    JLabel value;
	    
	    public void addValue(String strValue) {
	    	int intValue = Integer.parseInt(strValue);
	    	int newValue = (Integer) value.getClientProperty("value") + intValue;
	    	value.putClientProperty("value", newValue);
	    	value.setText("Company value: " + newValue);
	    	value.revalidate();
	    	value.repaint();
	    }

	    MoneyPanel(JPanel panel, JPanel itemsPanel, JTextField moneyField, JLabel value) {
	        this.panel = panel;
	        this.itemsPanel = itemsPanel;
	        this.moneyField = moneyField;
	        this.value = value;
	    }
	}
	
	private boolean isTrade = false;
	private MoneyPanel leftPanel;
	private MoneyPanel rightPanel;
	private JComboBox<String> nicknameComboBox;
	private Set<Integer> receiverTradePosition = new HashSet<Integer>();
	private Set<Integer> senderTradePosition = new HashSet<Integer>();
	
	public void handleCompanyClick(JCompanyCard jcc) {
	    if (isTrade) {
	        Color cl = playerColor.get(username);
	        if (cl != null && cl.equals(jcc.getCurrentOwnerColor())) {
	        	//if () // IS LAYOUT | no star amount
	            if (!senderTradePosition.add(jcc.getPosition())) {
	            	 showError("You already added this company to your list."); 
	            	 return;
	            }
 	        	leftPanel.itemsPanel.add(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
 	        	leftPanel.addValue(jcc.getCost());
	            leftPanel.itemsPanel.revalidate();
	            leftPanel.itemsPanel.repaint();
	            return;
	        }
	        Color cl1 = playerColor.get(nicknameComboBox.getSelectedItem());
	        if (cl1 != null && cl1.equals(jcc.getCurrentOwnerColor())) {
	        	 if (!receiverTradePosition.add(jcc.getPosition())) {
	            	 showError("You already added this company to receiver list."); 
	            	 return;
	            }
	        	rightPanel.itemsPanel.add(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	        	rightPanel.addValue(jcc.getCost());
	        	rightPanel.itemsPanel.revalidate();
	        	rightPanel.itemsPanel.repaint();
	            return;
	        }
	        showError("This company does not belong to you or the receiver.");
	    } else {
	        Program.serverListener.sendMsg(MessageType.GET_COMPANY, Integer.toString(jcc.getPosition()));
	    }
	}
	
	private static Set<Integer> parseSet(String part) {
        Set<Integer> set = new HashSet<>();
        if (!part.equals("_")) {
            String[] elements = part.split("_");
            for (String element : elements) {
                if (!element.isEmpty()) {
                    set.add(Integer.parseInt(element));
                }
            }
        } else return null;
        return set;
    }
	
	JPanel tradePanel;
	
	public void createTradeOffer(String offerData) {
	    bg.setVisible(false);
	    String[] parts = offerData.split(" ");
        String nickname = parts[0];

        int money1 = Integer.parseInt(parts[1]);
        Set<Integer> set1 = parseSet(parts[2]);
        
        int money2 = Integer.parseInt(parts[3]);
        Set<Integer> set2 = parseSet(parts[4]);
        
        String value1 = parts[5];
        String value2 = parts[6];
        
	    tradePanel = new JPanel(new GridBagLayout());
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
	    
	    leftPanel = createMoneyPanel("SENDER", money1, value1);
	    rightPanel = createMoneyPanel("YOU", money2, value2);

	    if (set1 != null)
	    	for (Integer i : set1) {
	    		JCompanyCard jcc = (JCompanyCard) map.get(i);
	    		leftPanel.itemsPanel.add(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	    	}
	    
	    if (set2 != null)
	    	for (Integer i : set2) {
	    		JCompanyCard jcc = (JCompanyCard) map.get(i);
	    		rightPanel.itemsPanel.add(createItemPanel(jcc.getLogo(), jcc.getCompanyName(), jcc.getCost(), jcc.getPosition()));
	    	}
	    
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 0.5;
	    gbc.weighty = 0.5;
	    tradePanel.add(leftPanel.panel, gbc);

	    gbc.gridx = 1;
	    tradePanel.add(rightPanel.panel, gbc);
		
	    JPanel buttonsPanel = new JPanel();
	    JButton acceptButton = new JButton("ACCEPT");
	    acceptButton.setBackground(Color.GREEN);
	    acceptButton.addActionListener( (e) -> {
	    	tradePanel.setVisible(false);
	    	globalMapPanel.remove(tradePanel);
	    	Program.serverListener.sendMsg(MessageType.TRADE_ACCEPT, null);
	    });
	   
	    JButton declineButton = new JButton("DECLINE");
	    declineButton.setContentAreaFilled(false);
        declineButton.setOpaque(false);
        declineButton.setForeground(Color.RED);
        declineButton.setBorder(new LineBorder(Color.RED));
	    declineButton.addActionListener( (e) -> {
	    	tradePanel.setVisible(false);
	    	globalMapPanel.remove(tradePanel);
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

	    GridBagConstraints styleControl = new GridBagConstraints();
	    styleControl.gridx = 1;
	    styleControl.gridy = 1;
	    styleControl.insets = new Insets(3, 15, 3, 15);
	    styleControl.anchor = GridBagConstraints.CENTER;
	    styleControl.gridwidth = (map.size() / 4) - 1;
	    styleControl.gridheight = 5;
	    styleControl.fill = GridBagConstraints.BOTH;

	    tradePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

	    globalMapPanel.add(tradePanel, styleControl);
	    
	    globalMapPanel.revalidate();
	    globalMapPanel.repaint();
	}
	
	public void createTradeInterface() {
	    bg.setVisible(false);
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

	    Set<String> setString = new HashSet<>(playerColor.keySet());
	    setString.remove(username);
	    String[] nicknamesString = setString.toArray(new String[0]);
	    nicknameComboBox = new JComboBox<>(nicknamesString);
	    
	    gbc.gridx = 1;
	    gbc.anchor = GridBagConstraints.WEST;
	    tradePanel.add(nicknameComboBox, gbc);
	    
	    leftPanel = createMoneyPanel("YOU");
	    rightPanel = createMoneyPanel("RECEIVER");

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 0.5;
	    gbc.weighty = 0.5;
	    tradePanel.add(leftPanel.panel, gbc);

	    gbc.gridx = 1;
	    tradePanel.add(rightPanel.panel, gbc);

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
	    	bg.setVisible(false);
	    	isTrade = false;
	    	senderTradePosition.clear();
	    	receiverTradePosition.clear();
	    	Program.serverListener.sendMsg(MessageType.TRADE_OFFER, nicknameComboBox.getSelectedItem() + " " + leftPanel.moneyField.getText() + " " + 
	    			senderList + " " + rightPanel.moneyField.getText() + " " + receiverList);
	    });
	    JButton cancelButton = new JButton("CANCEL");
	    cancelButton.addActionListener((e) -> {
	    	isTrade = false;
	    	senderTradePosition.clear();
	    	receiverTradePosition.clear();
	    	globalMapPanel.remove(tradePanel);
	    	bg.setVisible(true);
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

	    GridBagConstraints styleControl = new GridBagConstraints();
	    styleControl.gridx = 1;
	    styleControl.gridy = 1;
	    styleControl.insets = new Insets(3, 15, 3, 15);
	    styleControl.anchor = GridBagConstraints.CENTER;
	    styleControl.gridwidth = (map.size() / 4) - 1;
	    styleControl.gridheight = 5;
	    styleControl.fill = GridBagConstraints.BOTH;

	    tradePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

	    globalMapPanel.add(tradePanel, styleControl);
	}
	
	private MoneyPanel createMoneyPanel(String moneyText, int money, String value) {
	    GridBagLayout gbl = new GridBagLayout();
	    gbl.columnWeights = new double[]{0.3, 0.7};
	    JPanel moneyPanel = new JPanel(gbl);
	    moneyPanel.setBackground(Color.DARK_GRAY);
	    GridBagConstraints gbc = new GridBagConstraints();

	    JLabel targetLabel = new JLabel(moneyText, SwingConstants.CENTER);
	    targetLabel.setForeground(Color.WHITE);
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(2, 10, 2, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    moneyPanel.add(targetLabel, gbc);

	    JLabel moneyLabel = new JLabel("Money: " + money, SwingConstants.RIGHT);
	    moneyLabel.setForeground(Color.WHITE);
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 1;
	    gbc.insets = new Insets(2, 10, 2, 5);
	    gbc.anchor = GridBagConstraints.EAST;
	    gbc.fill = GridBagConstraints.NONE;
	    moneyPanel.add(moneyLabel, gbc);

	    JPanel itemsPanel = new JPanel();
	    itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
	    JScrollPane scrollPane = new JScrollPane(itemsPanel);
	    scrollPane.setPreferredSize(new Dimension(200, 150));
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(2, 10, 2, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 1.0;
	  //  gbc.weighty = 1.0;
	    moneyPanel.add(scrollPane, gbc);
	    
	    JLabel itemsValue = new JLabel("Total value: " + value);
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(3, 10, 3, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.weightx = 1.0;
	    gbc.weighty = 1.0;
	    gbc.fill = GridBagConstraints.NONE;
	    itemsValue.setForeground(Color.WHITE);
	    moneyPanel.add(itemsValue, gbc);

	    return new MoneyPanel(moneyPanel, itemsPanel, null, itemsValue);
	}

	private MoneyPanel createMoneyPanel(String moneyText) {
	    GridBagLayout gbl = new GridBagLayout();
	    gbl.columnWeights = new double[]{0.3, 0.7};
	    JPanel moneyPanel = new JPanel(gbl);
	    moneyPanel.setBackground(Color.DARK_GRAY);
	    GridBagConstraints gbc = new GridBagConstraints();

	    JLabel targetLabel = new JLabel(moneyText, SwingConstants.CENTER);
	    targetLabel.setForeground(Color.WHITE);
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(3, 10, 3, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    moneyPanel.add(targetLabel, gbc);

	    JLabel moneyLabel = new JLabel("Money:", SwingConstants.RIGHT);
	    moneyLabel.setForeground(Color.WHITE);
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 1;
	    gbc.insets = new Insets(3, 10, 3, 5);
	    gbc.anchor = GridBagConstraints.EAST;
	    gbc.fill = GridBagConstraints.NONE;
	    moneyPanel.add(moneyLabel, gbc);

	    JTextField moneyTextField = new JTextField(6);
	    moneyTextField.setText("0");
	    gbc.gridx = 1;
	    gbc.gridy = 1;
	    gbc.insets = new Insets(3, 5, 3, 10);
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    moneyPanel.add(moneyTextField, gbc);

	    JPanel itemsPanel = new JPanel();
	    itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
	    JScrollPane scrollPane = new JScrollPane(itemsPanel);
	    scrollPane.setPreferredSize(new Dimension(200, 150));
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(2, 10, 2, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 1.0;
	    gbc.weighty = 1.0;
	    moneyPanel.add(scrollPane, gbc);
	    JLabel itemsValue = new JLabel("Company value: " + 0);
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.insets = new Insets(2, 10, 2, 10);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.weightx = 1.0;
	    gbc.weighty = 1.0;
	    gbc.fill = GridBagConstraints.NONE;
	    itemsValue.putClientProperty("value", 0);
	    itemsValue.setForeground(Color.WHITE);
	    moneyPanel.add(itemsValue, gbc);

	    return new MoneyPanel(moneyPanel, itemsPanel, moneyTextField, itemsValue);
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
	    	globalMapPanel.repaint();
	    	globalMapPanel.revalidate();// TODO
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

	
	JPanel bg = new JPanel(); 
	
	public void createRollMenuPanel(int amount) {
		clearMenuPanel();
		menuPanelStatus = 1;
		bg = new JPanel();
		bg.setLayout(new GridBagLayout());
		bg.setBackground(Color.LIGHT_GRAY);
		bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
		
		GridBagConstraints styleControl = new GridBagConstraints();
		styleControl.gridx = 1; 
		styleControl.gridy = 1;
		
		styleControl.insets = new Insets(15, 15, 15, 15);
		
		styleControl.anchor = GridBagConstraints.CENTER;
		styleControl.gridwidth = (amount/4)-1;
		styleControl.gridheight = 2;
		styleControl.fill = GridBagConstraints.BOTH;
		
		JLabel text = new JLabel("Your turn, click below to roll");
		
		text.setFont(new Font("Serif Bold", Font.BOLD, 24));
		
		GridBagConstraints styleText = new GridBagConstraints();
		styleText.gridx = 0;
		styleText.gridy = 0;
		styleText.insets.bottom = 5;
		styleText.anchor = GridBagConstraints.CENTER;
		
		GridBagConstraints styleRoll = new GridBagConstraints();
		styleRoll.gridx = 0;
		styleRoll.gridy = 1;
		styleText.insets.top = 5;
		styleRoll.anchor = GridBagConstraints.CENTER;
		
		roll.setBackground(new Color(173, 255, 47));
		
		roll.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	Dimension size = roll.getSize();
            	size.width = bg.getPreferredSize().width / 2;
            	roll.setPreferredSize(size);
            	roll.setSize(size);
            }
        });
		
		bg.add(text, styleText);
		bg.add(roll, styleRoll);
		
		globalMapPanel.add(bg, styleControl);
		globalMapPanel.repaint();
		globalMapPanel.revalidate();
	}
	
	JLabel text = new JLabel("Your roll is: ");
    private Timer continuousTimer;
    private boolean running = false;
	
	public void createRollResultPanel() {
		clearMenuPanel();
		menuPanelStatus = 2;
		bg = new JPanel();
		bg.setLayout(new GridBagLayout());
		bg.setBackground(Color.LIGHT_GRAY);
		bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
		
		GridBagConstraints styleControl = new GridBagConstraints();
		styleControl.gridx = 1; 
		styleControl.gridy = 1;
		
		styleControl.insets = new Insets(15, 15, 15, 15);
		
		styleControl.anchor = GridBagConstraints.CENTER;
		styleControl.gridwidth = (map.size()/4)-1;
		styleControl.gridheight = 2;
		styleControl.fill = GridBagConstraints.BOTH;
		
		text.setFont(new Font("Serif Bold", Font.BOLD, 24));
		
		GridBagConstraints styleText = new GridBagConstraints();
		styleText.gridx = 0;
		styleText.gridy = 0;
		styleText.insets.bottom = 5;
		styleText.anchor = GridBagConstraints.CENTER;
		
		bg.add(text, styleText);
		
		globalMapPanel.add(bg, styleControl);
		globalMapPanel.repaint();
		globalMapPanel.revalidate();
	}
	
	private final int widthSlices = 11;  // Number of slices along the width
    private final int heightSlices = 11; // Number of slices along the height
    private int delay;        // Delay in milliseconds between moves
    private Timer timer;
    private int currentPosition;
    private int endPosition;

    /**
     * Starts navigation from the starting point A to the destination point B along the perimeter.
     *
     * @param startX Starting point X coordinate.
     * @param startY Starting point Y coordinate.
     * @param endX   Destination point X coordinate.
     * @param endY   Destination point Y coordinate.
     */
    public void navigate(PlayerChip chip, int endX, int endY) {
    	int[] startCoords = chip.getPosition();
        int startPos = getPerimeterPosition(startCoords[0], startCoords[1]);
        endPosition = getPerimeterPosition(endX, endY);
        int clockwiseDistance = calculateClockwiseDistance(startPos, endPosition);
        int counterclockwiseDistance = calculateCounterclockwiseDistance(startPos, endPosition);

        if (clockwiseDistance <= counterclockwiseDistance) {
        	delay =  new Double(ROLL_WAITTIME / clockwiseDistance).intValue();
            currentPosition = startPos;
            startClockwiseMovement(chip);
        } else {
        	delay =  new Double(ROLL_WAITTIME / counterclockwiseDistance).intValue();
            currentPosition = startPos;
            startCounterclockwiseMovement(chip);
        }
    }

    /**
     * Initiates clockwise movement using a Swing Timer.
     */
    private void startClockwiseMovement(PlayerChip chip) {
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPosition != endPosition) {
                    currentPosition = (currentPosition + 1) % getTotalPerimeterLength();
                    moveToPosition(chip, currentPosition);
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    /**
     * Initiates counterclockwise movement using a Swing Timer.
     */
    private void startCounterclockwiseMovement(PlayerChip chip) {
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPosition != endPosition) {
                    currentPosition = (currentPosition - 1 + getTotalPerimeterLength()) % getTotalPerimeterLength();
                    moveToPosition(chip, currentPosition);
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    /**
     * Calculates the linear position on the perimeter based on x, y coordinates.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Linear position on the perimeter.
     */
    private int getPerimeterPosition(int x, int y) {
        if (y == 0) {
            return x; // Top side
        } else if (x == widthSlices - 1) {
            return widthSlices - 1 + y; // Right side
        } else if (y == heightSlices - 1) {
            return widthSlices - 1 + heightSlices - 1 + (widthSlices - 1 - x); // Bottom side
        } else {
            return 2 * (widthSlices - 1) + heightSlices - 1 + (heightSlices - 1 - y); // Left side
        }
    	//return x+y;
    }


    /**
     * Converts a perimeter position to x, y coordinates and triggers the movement.
     *
     * @param position Linear position on the perimeter.
     */
    private void moveToPosition(PlayerChip chip, int position) {
        int[] coordinates = getCoordinatesFromPerimeterPosition(position);
        displayChip(chip, coordinates[0], coordinates[1]);
    }

    /**
     * Converts a linear perimeter position to x, y coordinates.
     *
     * @param position Linear position on the perimeter.
     * @return Array containing x, y coordinates.
     */
    private int[] getCoordinatesFromPerimeterPosition(int position) {
        int[] coordinates = new int[2];

        if (position < widthSlices) {
            coordinates[0] = position;
            coordinates[1] = 0; // Top side
        } else if (position < widthSlices + heightSlices - 1) {
            coordinates[0] = widthSlices - 1;
            coordinates[1] = position - widthSlices + 1; // Right side
        } else if (position < 2 * widthSlices + heightSlices - 3) {
            coordinates[0] = widthSlices - 1 - (position - widthSlices - heightSlices + 2);
            coordinates[1] = heightSlices - 1; // Bottom side
        } else {
            coordinates[0] = 0;
            coordinates[1] = heightSlices - 1 - (position - 2 * widthSlices - heightSlices + 3); // Left side
        }

        return coordinates;
    }

    /**
     * Returns the total length of the perimeter.
     *
     * @return Total perimeter length.
     */
    private int getTotalPerimeterLength() {
        return 2 * (widthSlices + heightSlices - 2);
    }

    /**
     * Calculates the clockwise distance between two positions on the perimeter.
     *
     * @param startPos Start position.
     * @param endPos   End position.
     * @return Clockwise distance.
     */
    private int calculateClockwiseDistance(int startPos, int endPos) {
        return (endPos - startPos + getTotalPerimeterLength()) % getTotalPerimeterLength();
    }

    /**
     * Calculates the counterclockwise distance between two positions on the perimeter.
     *
     * @param startPos Start position.
     * @param endPos   End position.
     * @return Counterclockwise distance.
     */
    private int calculateCounterclockwiseDistance(int startPos, int endPos) {
        return (startPos - endPos + getTotalPerimeterLength()) % getTotalPerimeterLength();
    }
	
	public void changeResultText(int amount, String value1, String value2, String nickname) {
		if (nickname.equals(username))
			text.setText("Your roll is: " + value1 + " " + value2);
		else
			text.setText(nickname + "'s roll is: " + value1 + " " + value2);
			
		bg.repaint();
		bg.revalidate();	
	}
	
	public void startContinuousRoll(String nickname) {
        if (running) return;
        running = true;
        continuousTimer = new Timer(100, new ActionListener() {
            private int counter = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                String value1 = String.valueOf(counter);
                String value2 = String.valueOf(counter);
                changeResultText(0, value1, value2, nickname);
                counter = counter % 6 + 1;
            }
        });
        continuousTimer.start();
    }

    public void stopContinuousRoll() {
        if (continuousTimer != null) {
            continuousTimer.stop();
            running = false;
        }
    }

    public void startTimedRoll(long durationMillis, String msg) {
    	clearMenuPanel();
    	createRollResultPanel();
    	String data[] = msg.split("\\s++");
        startContinuousRoll(data[2]);
        Timer stopTimer = new Timer((int) durationMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopContinuousRoll();
				append(data[2] + " have rolled " + data[0] + " " + data[1],  Color.black, new Color(225, 225, 225), true, false, false);
				changeResultText(map.size(), data[0], data[1], data[2]);
				
				PlayerChip selectedChip = null;
				for (PlayerChip chip : playerColors) {
					if (chip.getUsername().equals(data[2])) {
							selectedChip = chip;
							break;
					}
				}
				
				if (alteredMovement != null && alteredMovement.getChip().equals(selectedChip)) {
					int[] movement = alteredMovement.getMovement();
				//	displayChip(selectedChip, movement[0], movement[1]);
					navigate(selectedChip, movement[0], movement[1]);
					alteredMovement = null;
				} else
					navigate(selectedChip, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
				//	displayChip(selectedChip, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
				
				Timer clearActionTimer = new Timer(ROLL_WAITTIME, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						flushDelayedMessages();
						
						if (menuPanelStatus == 2) {
							if (username.equals(data[2])) {
								switch(data[5]) {
									case "0":
										clearMenuPanel();
										Program.serverListener.sendMsg(MessageType.SKIP_TURN, null);
									break;
									case "1":
										ActionListener ac = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
										    	if (moneyAmount >= Integer.parseInt(data[7])) {
													Program.serverListener.sendMsg(MessageType.CONFIRM_BUY, null);
													clearMenuPanel();
										    	} else {
										    		showError("You don`r have enough money...");
										    	}
										  	}
										};
										ActionListener ac1 = new ActionListener() {
											    @Override
											    public void actionPerformed(ActionEvent e) {
													Program.serverListener.sendMsg(MessageType.AUCTION, null);
													clearMenuPanel();
											  	}
										};
										createTwoButtonPanel("You stepped on company " + data[6].replaceAll("_", " "), "Buy company (" + data[7] + "UAH)",
												"To auction", ac, ac1);
									break;
									case "2":
										ActionListener payNormalListener = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
										    	if (moneyAmount >= Integer.parseInt(data[7])) {
													Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
													clearMenuPanel();
										    	} else {
										    		showError("You don`r have enough money...");
										    	}
										  	}
										};
										createPayMenuPanel("<html>You stepped on company " + data[6].replaceAll("_", " ") + "<br> and have to pay rent</html>", "Pay rent ("
												+ data[7] + "UAH)", payNormalListener);
									break;
									case "3":
										ActionListener evNormalListener = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
										    	if (moneyAmount >= Integer.parseInt(data[7])) {
													Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
													clearMenuPanel();
										    	} else {
										    		showError("You don`r have enough money...");
										    	}
										  	}
										};
										createPayMenuPanel("You had an unlucky event and have to pay", "Pay (" + data[7] + "UAH)", evNormalListener); 
									break;
									case "4":
										createCasinoPanel();
									break;
									case "5":
										clearMenuPanel();
										Program.serverListener.sendMsg(MessageType.SKIP_TURN, null);
									break;
									case "6":
										ActionListener evListener = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
										    	if (moneyAmount >= Integer.parseInt(data[7])) {
													Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
													clearMenuPanel();
										    	} else {
										    		showError("You don`r have enough money...");
										    	}
										  	}
										};
										ActionListener ac2 = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
												Program.serverListener.sendMsg(MessageType.SURRENDER, null);
												clearMenuPanel();
											  
										  	}
										};
										createGiveUpPanel(data[6].replaceAll("_", " "), "Pay (" + data[7] + "UAH)",
												"GIVE UP", evListener, ac2);
									break;
									case "7":
										ActionListener payListener = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
										    	if (moneyAmount >= Integer.parseInt(data[7])) {
													Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
													clearMenuPanel();
										    	} else {
										    		showError("You don`t have enough money...");
										    	}
										  	}
										};
										ActionListener ac3 = new ActionListener() {
										    @Override
										    public void actionPerformed(ActionEvent e) {
												Program.serverListener.sendMsg(MessageType.SURRENDER, null);
												clearMenuPanel();
											  
										  	}
										};
										createGiveUpPanel(data[6].replaceAll("_", " "), "Pay (" + data[7] + "UAH)",
												"GIVE UP", payListener, ac3);
									break;
									default:
										showError("Cannot identify which panel to display...");
									break;
								}
							} else {
								clearMenuPanel();
							}
						}
					}
				});
				clearActionTimer.setRepeats(false);
				clearActionTimer.start();
            }
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }
    
    public void createCasinoPanel() {
  	  clearMenuPanel();
  	  
  	  menuPanelStatus = 5;
  	  
  	  bg = new JPanel();
  	  
  	  GridBagLayout gbl = new GridBagLayout();
  	  gbl.columnWeights = new double[] {0.5, 0.5};
  	  gbl.rowWeights = new double[]{0.4, 0.2, 0.4};
  	  
  	  bg.setLayout(gbl);
  	  bg.setBackground(Color.LIGHT_GRAY);
  	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
  	  
  	  GridBagConstraints styleControl = new GridBagConstraints();
  	  styleControl.gridx = 1; 
  	  styleControl.gridy = 1;
  	  
  	  styleControl.insets = new Insets(15, 15, 15, 15);
  	  
  	  styleControl.anchor = GridBagConstraints.CENTER;
  	  styleControl.gridwidth = (map.size()/4)-1;
  	  styleControl.gridheight = 2;
  	  styleControl.fill = GridBagConstraints.BOTH;
  	  
  	  JLabel text = new JLabel("You had stepped on the casino. Do you want to play?");
  	  
  	  text.setFont(new Font("Serif Bold", Font.BOLD, 24));
  	  
  	  GridBagConstraints styleText = new GridBagConstraints();
  	  styleText.gridx = 0;
  	  styleText.gridy = 0;
  	  styleText.insets.bottom = 5;
  	  styleText.gridwidth = 2;
  	  styleText.anchor = GridBagConstraints.CENTER;
  	  
  	  String[] bets = {"No bet", "1", "2", "3", "4", "5", "6"};
  	  
  	  JComboBox<String> bet1 = new JComboBox<String>(bets);
  	  JComboBox<String> bet2 = new JComboBox<String>(bets);
  	  JComboBox<String> bet3 = new JComboBox<String>(bets);
  	  
  	  GridBagConstraints styleBet = new GridBagConstraints();
  	  styleBet.gridx = 0;
  	  styleBet.gridy = 1;
  	  styleBet.insets.top = 5;
  	  styleBet.weightx = 1.0;
  	  styleBet.gridwidth = 2;
  	  styleBet.anchor = GridBagConstraints.CENTER;
  	  styleBet.insets.left = 0;
  	  
  	  bg.add(bet1, styleBet);
  	  styleBet.insets.left = 200;
  	  bg.add(bet2, styleBet);
  	  styleBet.insets.left = -200;
  	  bg.add(bet3, styleBet);
  	  
  	  JButton firstButton = new JButton("Place a bet (1000k UAH)");
  	  
  	  GridBagConstraints styleBuy = new GridBagConstraints();
  	  styleBuy.gridx = 0;
  	  styleBuy.gridy = 2;
  	  styleBuy.insets.top = 5;
  	  styleBuy.weightx = 1.0;
  	  styleBuy.anchor = GridBagConstraints.CENTER;
  	  
  	  firstButton.addActionListener((e) -> {
  		  if (this.moneyAmount < 1000) {
  			  showError("Not enough money.");
			  return;
  		  }
  		  ArrayList<String> predictions = new ArrayList<String>();
  		  predictions.add(bets[bet1.getSelectedIndex()]);
  		  String val2 = bets[bet2.getSelectedIndex()];
  		  if ((val2.equals("No bet") && predictions.contains(val2)) || (!predictions.contains(val2))) {
  			  predictions.add(val2);
  		  } else {
  			  showError("There are more than 1 predictions for the same value!");
  			  return;
  		  } // handle triple no bet
  		  String val3 = bets[bet3.getSelectedIndex()];
		  if ((val3.equals("No bet") && predictions.contains(val3)) || (!predictions.contains(val3))) {
			  predictions.add(val3);
		  } else {
			  showError("There are more than 1 predictions for the same value!");
			  return;
		  }
		  if (bets[bet1.getSelectedIndex()].equals("No bet") && val2.equals("No bet") && val3.equals("No bet")) {
			  showError("You can`t bet without setting at least one prediction.");
			  return;
		  } 
  		  clearMenuPanel();
  		  
  		  String result = "";
  		  
  		  for (String s : predictions) {
  			  if (s.equals("No bet"))
  				  result += " " + 0;
  			  else 
  				  result += " " + s;
  		  }
  		  result = result.substring(1);
  		  Program.serverListener.sendMsg(MessageType.CASINO_AGREE, result);
  	  });
  	  
  	  firstButton.addComponentListener(new ComponentAdapter() {
  	            @Override
  	            public void componentResized(ComponentEvent e) {
  	             Dimension size = firstButton.getSize();
  	             size.width = new Double(bg.getPreferredSize().width / 2.3).intValue();
  	             firstButton.setPreferredSize(size);
  	             firstButton.setSize(size);
  	            }
  	 });
  	  
  	  GridBagConstraints styleAuction = new GridBagConstraints();
  	  styleAuction.gridx = 1;
  	  styleAuction.gridy = 2;
  	  styleAuction.insets.top = 5;
  	  styleAuction.weightx = 1.0;
  	  styleAuction.anchor = GridBagConstraints.CENTER;
  	  
  	  JButton secondButton = new JButton("Decline");
  	secondButton.setBackground(new Color(173, 255, 47));
  	firstButton.setBackground(Color.LIGHT_GRAY);
  	firstButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
  	  
      secondButton.addActionListener((e) -> {
  		  clearMenuPanel();
  		  Program.serverListener.sendMsg(MessageType.SKIP_TURN, null);
  	  });
  	  
  	  secondButton.addComponentListener(new ComponentAdapter() {
  	            @Override
  	            public void componentResized(ComponentEvent e) {
  	             Dimension size = secondButton.getSize();
  	             size.width = new Double(bg.getPreferredSize().width / 2.3).intValue();
  	             secondButton.setPreferredSize(size);
  	             secondButton.setSize(size);
  	            }
  	  });
  	  
  	  bg.add(text, styleText);
  	  bg.add(firstButton, styleBuy);
  	  bg.add(secondButton, styleAuction);
  	  
  	  firstButton.setVisible(true);
  	  secondButton.setVisible(true);
  	  
  	  globalMapPanel.add(bg, styleControl);
  	  globalMapPanel.repaint();
  	  globalMapPanel.revalidate();
  	 }
    
    public void createGiveUpPanel(String textContent, String firstButtonContent, String secondButtonContent, ActionListener actionResult, ActionListener actionResult1) {
  	  clearMenuPanel();
  	  menuPanelStatus = 5;
  	  
  	  bg = new JPanel();
  	  
  	  GridBagLayout gbl = new GridBagLayout();
  	  gbl.columnWeights = new double[] {0.5, 0.5};
  	  gbl.rowWeights = new double[]{0.5, 0.5};
  	  
  	  bg.setLayout(gbl);
  	  bg.setBackground(Color.LIGHT_GRAY);
  	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
  	  
  	  GridBagConstraints styleControl = new GridBagConstraints();
  	  styleControl.gridx = 1; 
  	  styleControl.gridy = 1;
  	  
  	  styleControl.insets = new Insets(15, 15, 15, 15);
  	  
  	  styleControl.anchor = GridBagConstraints.CENTER;
  	  styleControl.gridwidth = (map.size()/4)-1;
  	  styleControl.gridheight = 2;
  	  styleControl.fill = GridBagConstraints.BOTH;
  	  
  	  JLabel text = new JLabel(textContent);
  	  
  	  text.setFont(new Font("Serif Bold", Font.BOLD, 24));
  	  
  	  GridBagConstraints styleText = new GridBagConstraints();
  	  styleText.gridx = 0;
  	  styleText.gridy = 0;
  	  styleText.insets.bottom = 5;
  	  styleText.gridwidth = 2;
  	  styleText.anchor = GridBagConstraints.CENTER;
  	  
  	  JButton firstButton = new JButton(firstButtonContent);
  	  
  	  GridBagConstraints styleBuy = new GridBagConstraints();
  	  styleBuy.gridx = 0;
  	  styleBuy.gridy = 1;
  	  styleBuy.insets.top = 5;
  	  styleBuy.anchor = GridBagConstraints.CENTER;
  	  
  	  firstButton.setBackground(Color.LIGHT_GRAY);
  	  firstButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
  	  
  	  firstButton.addActionListener(actionResult);
  	  
  	  firstButton.addComponentListener(new ComponentAdapter() {
  	            @Override
  	            public void componentResized(ComponentEvent e) {
  	            	Dimension size = firstButton.getSize();
  	             	size.width = new Double(bg.getPreferredSize().width / 2.3).intValue();
  	             	firstButton.setPreferredSize(size);
  	             	firstButton.setSize(size);
  	            }
  	        });
  	  
  	  GridBagConstraints styleAuction = new GridBagConstraints();
  	  styleAuction.gridx = 1;
  	  styleAuction.gridy = 1;
  	  styleAuction.insets.top = 5;
  	  styleAuction.anchor = GridBagConstraints.CENTER;
  	  
  	  JButton secondButton = new JButton(secondButtonContent);
  	  
  	  secondButton.setBackground(Color.LIGHT_GRAY);
  	  secondButton.setBorder(BorderFactory.createLineBorder(Color.RED, 4));
  	  
  	  secondButton.addActionListener(actionResult1);
  	  
  	  secondButton.addComponentListener(new ComponentAdapter() {
  	            @Override
  	            public void componentResized(ComponentEvent e) {
  	             Dimension size = secondButton.getSize();
  	             size.width = new Double(bg.getPreferredSize().width / 2.3).intValue();
  	             secondButton.setPreferredSize(size);
  	             secondButton.setSize(size);
  	            }
  	  });
  	  
  	  bg.add(text, styleText);
  	  bg.add(firstButton, styleBuy);
  	  bg.add(secondButton, styleAuction);
  	  
  	  firstButton.setVisible(true);
  	  secondButton.setVisible(true);
  	  
  	  globalMapPanel.add(bg, styleControl);
  	  globalMapPanel.repaint();
  	  globalMapPanel.revalidate();
  	 }
	
    public void createTwoButtonPanel(String textContent, String firstButtonContent, String secondButtonContent, ActionListener actionResult, ActionListener actionResult1) {
    	  clearMenuPanel();
    	  
    	  menuPanelStatus = 3;
    	  
    	  bg = new JPanel();
    	  
    	  GridBagLayout gbl = new GridBagLayout();
    	  gbl.columnWeights = new double[] {0.5, 0.5};
    	  gbl.rowWeights = new double[]{0.5, 0.5};
    	  
    	  bg.setLayout(gbl);
    	  bg.setBackground(Color.LIGHT_GRAY);
    	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
    	  
    	  GridBagConstraints styleControl = new GridBagConstraints();
    	  styleControl.gridx = 1; 
    	  styleControl.gridy = 1;
    	  
    	  styleControl.insets = new Insets(15, 15, 15, 15);
    	  
    	  styleControl.anchor = GridBagConstraints.CENTER;
    	  styleControl.gridwidth = (map.size()/4)-1;
    	  styleControl.gridheight = 2;
    	  styleControl.fill = GridBagConstraints.BOTH;
    	  
    	  JLabel text = new JLabel(textContent);
    	  
    	  text.setFont(new Font("Serif Bold", Font.BOLD, 24));
    	  
    	  GridBagConstraints styleText = new GridBagConstraints();
    	  styleText.gridx = 0;
    	  styleText.gridy = 0;
    	  styleText.insets.bottom = 5;
    	  styleText.gridwidth = 2;
    	  styleText.anchor = GridBagConstraints.CENTER;
    	  
    	  JButton firstButton = new JButton(firstButtonContent);
    	  
    	  GridBagConstraints styleBuy = new GridBagConstraints();
    	  styleBuy.gridx = 0;
    	  styleBuy.gridy = 1;
    	  styleBuy.insets.top = 5;
    	  styleBuy.weightx = 1.0;
    	  styleBuy.anchor = GridBagConstraints.CENTER;
    	  
    	  firstButton.setBackground(new Color(173, 255, 47));
    	  
    	  firstButton.addActionListener(actionResult);
    	  
    	  firstButton.addComponentListener(new ComponentAdapter() {
    	            @Override
    	            public void componentResized(ComponentEvent e) {
//    	             showMessage(firstButton.getX() + "" + firstButton.getY());
    	             
    	         // bg.revalidate();
    	             Dimension size = firstButton.getSize();
    	             size.width = new Double(bg.getPreferredSize().width / 2).intValue();
    	             firstButton.setPreferredSize(size);
    	             firstButton.setSize(size);
    	            }
    	        });
    	  
    	  GridBagConstraints styleAuction = new GridBagConstraints();
    	  styleAuction.gridx = 1;
    	  styleAuction.gridy = 1;
    	  styleAuction.insets.top = 5;
    	  styleAuction.weightx = 1.0;
    	  styleAuction.anchor = GridBagConstraints.CENTER;
    	  
    	  JButton secondButton = new JButton(secondButtonContent);
    	  
    	  secondButton.setBackground(Color.LIGHT_GRAY);
    	  secondButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    	  
    	  secondButton.addActionListener(actionResult1);
    	  
    	  secondButton.addComponentListener(new ComponentAdapter() {
    	            @Override
    	            public void componentResized(ComponentEvent e) {
    	             Dimension size = secondButton.getSize();
    	             size.width = new Double(bg.getPreferredSize().width / 2.3).intValue();
    	             secondButton.setPreferredSize(size);
    	             secondButton.setSize(size);
    	            }
    	  });
    	  
    	  bg.add(text, styleText);
    	  bg.add(firstButton, styleBuy);
    	  bg.add(secondButton, styleAuction);
    	  
    	  firstButton.setVisible(true);
    	  secondButton.setVisible(true);
    	  
    	  globalMapPanel.add(bg, styleControl);
    	  globalMapPanel.repaint();
    	  globalMapPanel.revalidate();
    	 }
	
    public void createPayMenuPanel(String textContent, String buttonContext, ActionListener buttonListener) {
    	JButton button = new JButton(buttonContext);
    	button.addActionListener(buttonListener);
    	createPayMenuPanel(textContent, buttonContext, button);
    }
    
	public void createPayMenuPanel(String textContent, String buttonContent, JButton buttonLink) {
		clearMenuPanel();
		menuPanelStatus = 4;
		
		bg = new JPanel();
		bg.setLayout(new GridBagLayout());
		bg.setBackground(Color.LIGHT_GRAY);
		bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
		
		GridBagConstraints styleControl = new GridBagConstraints();
		styleControl.gridx = 1; 
		styleControl.gridy = 1;
		
		styleControl.insets = new Insets(15, 15, 15, 15);
		
		styleControl.anchor = GridBagConstraints.CENTER;
		styleControl.gridwidth = (map.size()/4)-1;
		styleControl.gridheight = 2;
		styleControl.fill = GridBagConstraints.BOTH;
		
		JLabel text = new JLabel(textContent);
		
		text.setFont(new Font("Serif Bold", Font.BOLD, 24));
		
		GridBagConstraints styleText = new GridBagConstraints();
		styleText.gridx = 0;
		styleText.gridy = 0;
		styleText.insets.bottom = 5;
		styleText.anchor = GridBagConstraints.CENTER;
		
		buttonLink.setText(buttonContent);
		
		buttonLink.setBackground(new Color(173, 255, 47));
		
		GridBagConstraints stylePay = new GridBagConstraints();
		stylePay.gridx = 0;
		stylePay.gridy = 1;
		stylePay.insets.top = 5;
		stylePay.anchor = GridBagConstraints.CENTER;
		
		buttonLink.setBackground(new Color(173, 255, 47));
		
		buttonLink.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	Dimension size = buttonLink.getSize();
            	size.width = bg.getPreferredSize().width / 2;
            	buttonLink.setPreferredSize(size);
            	buttonLink.setSize(size);
            }
        });
		
		buttonLink.setVisible(true);
		
		bg.add(text, styleText);
		bg.add(buttonLink, stylePay);
		
		globalMapPanel.add(bg, styleControl);
		globalMapPanel.repaint();
		globalMapPanel.revalidate();
	}
	
	public void clearMenuPanel() {
		bg.removeAll();
		if (globalMapPanel != null) {
			globalMapPanel.remove(bg); 
			globalMapPanel.revalidate();
			globalMapPanel.repaint();
		}
		menuPanelStatus = 0;
	}
	
	public void displayChip(PlayerChip chip, int x, int y) {
		if (globalMapPanel.isAncestorOf(chip)) {
			globalMapPanel.remove(chip);
		}
		GridBagConstraints styleChip = new GridBagConstraints();
		styleChip.gridx = x;
		styleChip.gridy = y;
		styleChip.anchor = GridBagConstraints.CENTER;
		
		int[] offset = chip.getOffset();
		
		styleChip.insets.top = offset[0];
		styleChip.insets.left = offset[1];
		int amount = calcAmountOfChips(x, y);
		if (amount == 0)
			globalMapPanel.add(chip, styleChip, 2);
		else 
			globalMapPanel.add(chip, styleChip, 1);
		globalMapPanel.moveToFront(chip);
		chip.moveTo(x, y);
		globalMapPanel.revalidate();
		globalMapPanel.repaint();
	}
	
	public int calcAmountOfChips(int x, int y) {
		int counter = 0;
		for (PlayerChip chip : playerColors) {
			int[] pos = chip.getPosition();
			if (pos[0] == x && pos[1] == y && globalMapPanel.isAncestorOf(chip))
				counter++;
		}
		return counter;
	}
	
	public int[] calcOffset(int numberOfChips) {
		int[] result = new int[2];
		switch (numberOfChips) {
			case 0: 
				result[0] = -25;
				result[1] = -25;
				return result;
			case 1:
				result[0] = 25;
				result[1] = -25;
				return result;
			case 2:
				result[0] = -25;
				result[1] = 25;
				return result;
			case 3:
				result[0] = 25;
				result[1] = 25;
				return result;
			case 4:
				result[0] = 0;
				result[1] = 0;
				return result;
			default:
				result[0] = 0;
				result[1] = 0;
				return result;
		}
	}
	
}
