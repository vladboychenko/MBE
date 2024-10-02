package me.ezik.client.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class BoardPanelController {

	private GameFrameController window;
    private MainPanelGenerator mainPanelGenerator = new MainPanelGenerator(this);
    private TradePanelController tradePanelController = new TradePanelController(this);
    private MovementController movementController = null;
	
	public BoardPanelController(GameFrameController window) {
		this.window = window;
	}

	private JLabel timerLabel = new JLabel("Initializing timer...");
	private Timer swingTimer;
    private long timeLeft;   
	private JLayeredPane globalMapPanel;
	private ArrayList<JAbstractCard> map = new ArrayList<JAbstractCard>();
	
	private HashMap<String, PlayerChip> playerChips;
	
    private final int ROLL_WAITTIME = 1500;
    
	private JPanel tradePanel;
	private JPanel bg = new JPanel(); 
	boolean isAuction = false;
	
    public void initialize(String mapData) {
		initChips();

		movementController = new MovementController(playerChips.values().toArray(new PlayerChip[playerChips.size()]), globalMapPanel);
		mainPanelGenerator = new MainPanelGenerator(this);
		tradePanelController = new TradePanelController(this);
		
		try {
			displayMap(mapData);
		} catch (OutOfMemoryError ex) {
			UtilityAPI.showError("Java could not allocate enough memory to load map. Increase to JVM memory heap amount recommended.");
		}
    }
    
    public void initChips() {
		Set<String> playerNames = window.getPlayerUsernames();
		
		playerChips = new HashMap<String, PlayerChip>();
		
		for (String name : playerNames) {
			playerChips.put(name, new PlayerChip(window.getPlayerColorByUsername(name), name));
		}
		
    }
    
    public void removeChip(String username) {
		globalMapPanel.remove(playerChips.get(username));
		updateBoard();
    }
    
    public void handleStartTurn() {
		timerLabel.setVisible(true);
		isAuction = false;
		if (tradePanel != null)
			tradePanel.setVisible(false);
		setAsMainPanel( mainPanelGenerator.createRollMenuPanel() );
    }
    
    public void handleAuction(String companyName, String companyPrice) {
    	isAuction = true;
		setAsMainPanel( mainPanelGenerator.createTwoButtonPanel("<html>Do you want to take part in auction for <br> company " + companyName + 
				"</html>", "Ramp up price " + companyPrice, "Decline", 
				new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (window.getMoneyAmount() >= Integer.parseInt(companyPrice)) {
							Program.serverListener.sendMsg(MessageType.AUCTION_YES, null);
							isAuction = false;
							clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`r have enough money...");
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
		}) );
    }
    
    public void handleJail(String panelStyle) {
    	switch (panelStyle) {
			case "1":
				ActionListener ac = new ActionListener() {
				@Override
				    public void actionPerformed(ActionEvent e) {
				    	if (window.getMoneyAmount() >= 1000) {
				    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
							clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("Not enough money.");
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
				setAsMainPanel( mainPanelGenerator.createTwoButtonPanel("<html>You`ve got to jail. You can pay or try to <br>roll for double to quit.</html>", "Pay (1000k UAH)",
						"Roll for double", ac, ac1) );
				
			break;
			case "2":
				ActionListener payListener = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (window.getMoneyAmount() >= 1000) {
				    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
							clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("Not enough money.");
				    	}
				  	}
				};
				setAsMainPanel( mainPanelGenerator.createPayMenuPanel("<html>You`ve exhausted all your attempts to get out of <br> jail and is forced to pay. </html>",
						"Pay (1000k UAH)", payListener) );
			break;
			default:
				UtilityAPI.showError("Error parsing jail panel style...");
			break;
    	}
    }
    
    public void handleCardUpdate(int cardPosition, Color newColor, String newCost, int newStarAmount, boolean isLayouted) {
    	JAbstractCard card = map.get(cardPosition);
    	if (!(card instanceof JCompanyCard))
    		return;
    	JCompanyCard companyCard = (JCompanyCard) card;
    	companyCard.changeOwner(newColor);
    	companyCard.changeCost(newCost);
    	companyCard.changeStarAmount(newStarAmount);
    	companyCard.setLayouted(isLayouted);
    	companyCard.repaint();
    }
    
    public void handleLayoutCards(int[] positions) {
    	ArrayList<JCompanyCard> cards = new ArrayList<JCompanyCard>();
    	for (int position : positions) {
    		cards.add((JCompanyCard) map.get(position));
    	}
		
    	for (JCompanyCard target : cards) {
			if (target.isLayouted()) {
				target.decreaseLayoutTimer();
			} else
				target.setLayouted(true);
			target.repaintCard();
		}
    }
    
    public void handleChipMovementChange(String chipUsername, int newX, int newY) {
		movementController.alterMovement(new ChipMovement(playerChips.get(chipUsername), newX, newY));
    }
    
    public void handleTradeOffer(String nickname, int money1, Set<Integer> set1, int money2, Set<Integer> set2, 
    		String value1, String value2) {
    	
    	Set<JCompanyCard> cSet1 = new HashSet<JCompanyCard>();
    	
    	for (Integer i : set1) {
    		cSet1.add((JCompanyCard) map.get(i));
    	}
    	
    	Set<JCompanyCard> cSet2 = new HashSet<JCompanyCard>();
    	
    	for (Integer i : set2) {
    		cSet2.add((JCompanyCard) map.get(i));
    	}
    	
		setTradePanel( tradePanelController.createTradeOffer(nickname, money1, cSet1, money2, cSet2, value1, value2) );
    }
    
    public void stopContinuousRoll() {
    	mainPanelGenerator.stopContinuousRoll();
    }
    
    public void startTimer(Date deadline, String username) {
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
	        updateBoard();
    	});
    }
	
	public JScrollPane createBoardReference() {

	    globalMapPanel = new JLayeredPane();
	    globalMapPanel.setBackground(new Color(225, 225, 225));
	    globalMapPanel.setLayout(new GridBagLayout());
	    
	    JScrollPane panel2 = new JScrollPane(globalMapPanel);
	    panel2.setBackground(new Color(225, 225, 225));
	    panel2.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    return panel2;
	}
	
	public void displayMap(String mapData) {
		String data[] = mapData.split("\\s++");
		int corner = -1;
		for (int i = 0; i < data.length;) {
			if (data[i].equals("card")) {
				if (data[i+1].equals("company")) {
						String name = data[i+2].toLowerCase();
						Color owner = UtilityAPI.getColor(name);
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
			globalMapPanel.add(card, styleTest, 0);
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
		for (PlayerChip chip : playerChips.values()) {
			int[] offset = UtilityAPI.calcOffset(amount);
			amount++;
			chip.setOffset(offset);
			movementController.displayChip(chip, 0, 0);
		}
		
		GridBagConstraints styleTimer = new GridBagConstraints();
		styleTimer.gridx = 1; // TODO
		styleTimer.gridy = 6;
		styleTimer.anchor = GridBagConstraints.CENTER;
		styleTimer.gridwidth = (map.size()/4)-1;
		timerLabel.setFont(new Font("Serif Bold", Font.BOLD, 24));
		
		globalMapPanel.add(timerLabel, styleTimer, 0);
		updateBoard();
	}
	
	public void handleCompanyClick(JCompanyCard jcc) {
	    if (tradePanelController.isTrading()) {
	        tradePanelController.handleNewCompany(jcc);
	    } else {
	        Program.serverListener.sendMsg(MessageType.GET_COMPANY, Integer.toString(jcc.getPosition()));
	    }
	}
	
	public Color getPlayerColorByUsername(String username) {
		return window.getPlayerColorByUsername(username);
	}
	
    public void startTimedRoll(long durationMillis, String msg) {
    	clearMenuPanel();
    	setAsMainPanel(mainPanelGenerator.createRollResultPanel());
    	String data[] = msg.split("\\s++");
        mainPanelGenerator.startContinuousRoll(data[2]);
        Timer stopTimer = new Timer((int) durationMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanelGenerator.stopContinuousRoll();
				window.displaySystemChatMessage(data[2] + " have rolled " + data[0] + " " + data[1]);
				mainPanelGenerator.changeResultText(map.size(), data[0], data[1], data[2]);
				
				PlayerChip selectedChip = playerChips.get(data[2]);
				
				movementController.moveChip(new ChipMovement(selectedChip, Integer.parseInt(data[3]), 
						Integer.parseInt(data[4])));
				
				Timer clearActionTimer = new Timer(ROLL_WAITTIME, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						window.flushDelayedMessages();
						clearMenuPanel();
						if (mainPanelGenerator.getMainPanelStatus() == 2) {
							if (window.getWindowOwner().equals(data[2])) {
								bg = mainPanelGenerator.determineAndCreateMainPanel(Arrays.copyOfRange(data, 5, data.length));
								setAsMainPanel(bg);
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
    
    public int getMoneyAmount() {
    	return window.getMoneyAmount();
    }
    
    public int getPreferredMapWidth() {
    	return bg.getPreferredSize().width;
    }
    
	public void hideMainPanel() {
		bg.setVisible(false);
	}
	
	public void showMainPanel() {
		bg.setVisible(true);
	}
	
	public void clearMenuPanel() {
		if (bg != null)
			bg.removeAll();
		if (globalMapPanel != null) {
			if (bg != null)
				globalMapPanel.remove(bg); 
			globalMapPanel.revalidate();
			globalMapPanel.repaint();
		}
	}
	
	public void sendMsg(MessageType msgType, String text) {
		Program.sendMsg(msgType, text);
	}
	
	public void setAsMainPanel(JPanel panel) {
		if (panel == null)
			return;
		bg = panel;
		
		GridBagConstraints styleControl = new GridBagConstraints();
	  	styleControl.gridx = 1; 
	  	styleControl.gridy = 1;
	  	styleControl.insets = new Insets(15, 15, 15, 15);
	  	styleControl.anchor = GridBagConstraints.CENTER;
	  	styleControl.gridwidth = (map.size()/4)-1;
	  	styleControl.gridheight = 2;
	  	styleControl.fill = GridBagConstraints.BOTH;
	  	
	  	globalMapPanel.add(panel, styleControl);
	  	globalMapPanel.repaint();
	  	globalMapPanel.revalidate();
	}
	
	public void updateBoard() {
		globalMapPanel.repaint();
	  	globalMapPanel.revalidate();
	}
	
	public void clearTradePanel() {
		globalMapPanel.remove(tradePanel);
		tradePanel = null;
	}
	
	public void setTradePanel(JPanel panel) {
		
		if (panel == null)
			return;
		
		tradePanel = panel;
		
		GridBagConstraints styleControl = new GridBagConstraints();
	    styleControl.gridx = 1;
	    styleControl.gridy = 1;
	    styleControl.insets = new Insets(3, 15, 3, 15);
	    styleControl.anchor = GridBagConstraints.CENTER;
	    styleControl.gridwidth = (map.size() / 4) - 1;
	    styleControl.gridheight = 5;
	    styleControl.fill = GridBagConstraints.BOTH;
		
		globalMapPanel.add(tradePanel, styleControl);
		updateBoard();
	}
	
	public boolean isCompanyLayoted(int position) {
		return ((JCompanyCard) map.get(position)).isLayouted();
	}
	
	public void openTradePanel() {
		if (tradePanelController.isTrading() == false && bg.isVisible() && !isAuction) {
			setTradePanel( tradePanelController.createTradeInterface() );
		}
		else
			UtilityAPI.showError("It`s not your turn.");
	}
	
	public Set<String> getListOfPlayers() {
		return window.getListOfCompetitorPlayers();
	}
	
	public String getWindowOwner() {
		return window.getWindowOwner();
	}
	
	public void reset() {
        map.clear();
        globalMapPanel.removeAll();
        swingTimer.stop();
        tradePanelController.reset();
	}
	
}
