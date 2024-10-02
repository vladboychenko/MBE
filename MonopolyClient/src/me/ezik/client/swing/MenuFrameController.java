package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.ezik.client.logic.PictureChangeListener;
import me.ezik.client.logic.ProfilePictureListener;
import me.ezik.client.logic.Program;
import me.ezik.client.logic.Setting;
import me.ezik.shared.message.MessageType;

public class MenuFrameController implements PictureChangeListener {

	private Window window;
	
	private HashMap<String, JPanel> panels = new HashMap<String, JPanel>();

	private DefaultListModel<String> listModel;
	
	public MenuFrameController(Window window) {
		this.window = window;
		initSettings();
	}
	
	public DefaultListModel<String> getModel() {return listModel;}
	
	
	private JPanel centerPanel;
	
	private JList<String> gameList;
	private ArrayList<Setting> settings = new ArrayList<Setting>();

	
	private int defaultPfp = 1;
	private String currentlyViewedGameName = null;
	public static JLabel selectedPicture = null;
	
	private ArrayList<String> userData = new ArrayList<String>();
	private HashMap<String, String> userPfp = new HashMap<String, String>();

	
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
	
	@Override
	public void handleChange(JLabel newPicture) {
		if (selectedPicture != null)
			selectedPicture.setBorder(null);
		selectedPicture = newPicture;
		newPicture.setBorder(BorderFactory.createLineBorder(Color.cyan, 7));
	}
	
	public boolean initTextField(JComponent comp, String value) {
		if (comp instanceof JTextField) {
			((JTextField) comp).setText(value);
			return true;
		}
		
		return false;
	}
	
	public void setPfp(int id) {
		defaultPfp = id;
	}
	
	public void initialize(int pfpId) {
		
			window.getContentPane().removeAll();
			
			window.setLayout(new BorderLayout());
			
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(60, 60));
			panel.setBackground(new Color(225, 225, 225));	
			panel.setLayout(new GridBagLayout());
			panel.setBorder(BorderFactory.createLineBorder(Color.black));
			
			JPanel topPanel = new JPanel();
			topPanel.setPreferredSize(new Dimension(60, 60));
			topPanel.setBackground(Color.GRAY);
			topPanel.setLayout(new GridBagLayout());
			topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			
			JPanel leftPanel = new JPanel();
			leftPanel.setPreferredSize(new Dimension(60, 60));
			leftPanel.setBackground(new Color(175, 175, 175));
			leftPanel.setLayout(new GridBagLayout());
			leftPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			
			JPanel rightPanel = new JPanel();
			rightPanel.setPreferredSize(new Dimension(60, 60));
			rightPanel.setBackground(new Color(175, 175, 175));
			rightPanel.setLayout(new GridBagLayout());
			rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			
			
			panels.put("top", topPanel);
			panels.put("center", panel);
			panels.put("left", leftPanel);
			panels.put("right", rightPanel);
			
			window.add(topPanel, BorderLayout.NORTH);
			window.add(panel, BorderLayout.CENTER);
			window.add(leftPanel, BorderLayout.WEST);
			window.add(rightPanel, BorderLayout.EAST);
			
			createLeftPanel();
			createTopPanel(pfpId);
			initMainCenterComponents();
			initCenterPanelMenu();
			
			window.revalidate();
			window.repaint();
	}
	
	public void initialize() { 
        this.userPfp.clear();
		this.initialize(defaultPfp);
        Program.sendMsg(MessageType.SEARCH_GAMES, "");
	}
	
	public HashMap<String, String> getUserPfp() {
		return userPfp;
	}
	
	public void createLeftPanel() {
		
		JButton createGame = new JButton("+");
    	createGame.addActionListener(e -> {
    		String name = JOptionPane.showInputDialog(window, "Enter name of the game:", "DefaultGameName");
    		if (name != null && !name.isEmpty() && name.matches("\\S+")) {
    			Program.serverListener.sendMsg(MessageType.CREATE_GAME, name);
    		} else 
    			UtilityAPI.showError("Name should not contain whitespaces.");
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
        
        JPanel leftPanel = panels.get("left");
        
        leftPanel.setLayout(new GridBagLayout()); 
        
        leftPanel.add(createGame, styleCreate);
        leftPanel.add(refresh, styleRefresh);
        leftPanel.add(home, styleHome);
	}
	
	public void createTopPanel(int pfpId) {
	    try {
	    	
	    	defaultPfp = pfpId;
	    	
	        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/logo" + pfpId + ".png");
	        BufferedImage myPicture = ImageIO.read(imageStream);
	        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));

	        JLabel username = new JLabel(window.getWindowOwner());
	        username.setVisible(true);
	        username.setFont(new Font("Times New Roman", Font.PLAIN, 18));
	        username.setForeground(Color.BLACK);
	        username.setBorder(new EmptyBorder(0,0,0,0));
			
		    JPanel topPanel = panels.get("top");  
			
		    topPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		    topPanel.add(picLabel);
		    topPanel.add(username);
			
		    topPanel.revalidate();
			topPanel.repaint();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void redrawTopPanel(int newId) {
		panels.get("top").removeAll();
		createTopPanel(newId);
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
	//////////////

	public void initMainCenterComponents() {
		
		JPanel center = panels.get("center");
		
		center.setLayout(new GridBagLayout());
		
	    listModel = new DefaultListModel<String>();
	    
	    center.removeAll();
	    
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
                		UtilityAPI.showError("For some reason I can`t find selected game`s name.");
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
	    
	    center.add(pane, styleTest);
	    
	    pane.setDividerLocation(0.6d);
	    pane.setDividerSize(0);
	    pane.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	        	 pane.setDividerLocation(0.25d);
	        }
	    });
	    
	    center.revalidate();
	    center.repaint();
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

	    window.revalidate();
	    window.repaint();

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
	    save.addActionListener((e) -> Program.serverListener.sendMsg(MessageType.CHANGE_PFP, Integer.toString((int) 
	    		selectedPicture.getClientProperty("index"))));
	    
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

	    window.revalidate();
	    window.repaint();

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
		
	public void showGameInfo(String gameData) {
		
		if (centerPanel != null)
			centerPanel.removeAll();
		
		JPanel center = panels.get("center");
		
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
		
		center.repaint();
		center.revalidate();
		
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
					if (!window.getWindowOwner().equals(userData.get(j))) {
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
					JOptionPane.showMessageDialog(window, settings.get(Integer.parseInt(helpButton.getName())).getHelpInfo(),"Help", JOptionPane.INFORMATION_MESSAGE);
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
					JOptionPane.showMessageDialog(window, settings.get(Integer.parseInt(helpButton.getName())).getHelpInfo(),"Help", JOptionPane.INFORMATION_MESSAGE);
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
		    		UtilityAPI.showError("Settings can`t contain whitespaces.");
		    	}
			});
			saveDefault.addActionListener((e) -> {
				String mp = ((JTextField) settings.get(0).getComponent()).getText();
				String tk = ((JTextField) settings.get(2).getComponent()).getText(); 
				if (mp != null && !mp.isEmpty() && mp.matches("\\S+") && tk != null && !tk.isEmpty() && tk.matches("\\S+")) {
		    		Program.serverListener.sendMsg(MessageType.SAVE_SETTINGS, mp + " " + ((JComboBox<String>) settings.get(1).getComponent()).getSelectedIndex() + " " + tk);
		    	} else {
		    		UtilityAPI.showError("Settings can`t contain whitespaces.");
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

		centerPanel.repaint();
		centerPanel.revalidate();
	}
	
}
