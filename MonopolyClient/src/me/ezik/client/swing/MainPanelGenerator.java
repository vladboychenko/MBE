package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class MainPanelGenerator {

	 private int menuPanelStatus = -1;
	 private BoardPanelController boardController;
	 
	 private JLabel text = new JLabel("Your roll is: ");
	 private Timer continuousTimer;
	 private boolean running = false;
	 
	 private JPanel rollResult = createRollResultPanel();
	 
	 public MainPanelGenerator(BoardPanelController boardController) {
		 this.boardController = boardController;
	 }
	 
	 public int getMainPanelStatus() {
		 return menuPanelStatus;
	 }
	 
	 public JPanel getRollResultPanel() {
		 return rollResult;
	 }
	 
	 public JPanel determineAndCreateMainPanel(String[] data) {
		 
		 JPanel resultPanel = null;
		 
		 switch(data[0]) {
			case "0":
				boardController.clearMenuPanel();
				Program.serverListener.sendMsg(MessageType.SKIP_TURN, null);
			break;
			case "1":
				ActionListener ac = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (boardController.getMoneyAmount() >= Integer.parseInt(data[2])) {
							Program.serverListener.sendMsg(MessageType.CONFIRM_BUY, null);
							boardController.clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`t have enough money...");
				    	}
				  	}
				};
				ActionListener ac1 = new ActionListener() {
					    @Override
					    public void actionPerformed(ActionEvent e) {
							Program.serverListener.sendMsg(MessageType.AUCTION, null);
							boardController.clearMenuPanel();
					  	}
				};
				resultPanel = createTwoButtonPanel("You stepped on company " + data[1].replaceAll("_", " "), "Buy company (" + data[2] + "UAH)",
						"To auction", ac, ac1);
			break;
			case "2":
				ActionListener payNormalListener = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (boardController.getMoneyAmount() >= Integer.parseInt(data[2])) {
							Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
							boardController.clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`t have enough money...");
				    	}
				  	}
				};
				resultPanel = createPayMenuPanel("<html>You stepped on company " + data[1].replaceAll("_", " ") + "<br> and have to pay rent</html>", "Pay rent ("
						+ data[2] + "UAH)", payNormalListener);
			break;
			case "3":
				ActionListener evNormalListener = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (boardController.getMoneyAmount() >= Integer.parseInt(data[2])) {
							Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
							boardController.clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`r have enough money...");
				    	}
				  	}
				};
				resultPanel = createPayMenuPanel("You had an unlucky event and have to pay", "Pay (" + data[2] + "UAH)", evNormalListener); 
			break;
			case "4":
				resultPanel = createCasinoPanel();
			break;
			case "5":
				boardController.clearMenuPanel();
				Program.serverListener.sendMsg(MessageType.SKIP_TURN, null);
			break;
			case "6":
				ActionListener evListener = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (boardController.getMoneyAmount() >= Integer.parseInt(data[2])) {
							Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
							boardController.clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`r have enough money...");
				    	}
				  	}
				};
				ActionListener ac2 = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
						Program.serverListener.sendMsg(MessageType.SURRENDER, null);
						boardController.clearMenuPanel();
					  
				  	}
				};
				resultPanel = createGiveUpPanel(data[1].replaceAll("_", " "), "Pay (" + data[2] + "UAH)",
						"GIVE UP", evListener, ac2);
			break;
			case "7":
				ActionListener payListener = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	if (boardController.getMoneyAmount() >= Integer.parseInt(data[2])) {
							Program.serverListener.sendMsg(MessageType.CONFIRM_EVENT, null);
							boardController.clearMenuPanel();
				    	} else {
				    		UtilityAPI.showError("You don`t have enough money...");
				    	}
				  	}
				};
				ActionListener ac3 = new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
						Program.serverListener.sendMsg(MessageType.SURRENDER, null);
						boardController.clearMenuPanel();
					  
				  	}
				};
				resultPanel = createGiveUpPanel(data[1].replaceAll("_", " "), "Pay (" + data[7] + "UAH)",
						"GIVE UP", payListener, ac3);
			break;
			default:
				UtilityAPI.showError("Cannot identify which panel to display...");
			break;
		}	
		return resultPanel;
	 }
	
	 public JPanel createRollMenuPanel() {
			boardController.clearMenuPanel();
			
			menuPanelStatus = 1;
			JPanel bg = new JPanel();
			bg.setLayout(new GridBagLayout());
			bg.setBackground(Color.LIGHT_GRAY);
			bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			
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
			
			JButton roll = new JButton("Roll dice");
			roll.addActionListener((e) -> {
				roll.setVisible(false);
				boardController.clearMenuPanel();
				boardController.setAsMainPanel(createRollResultPanel());
				menuPanelStatus = 2;
				startContinuousRoll(boardController.getWindowOwner());
				boardController.sendMsg(MessageType.ROLL, null);;
			});
			
			roll.setVisible(true);
			
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
			
			return bg;
	}
		
	public JPanel createRollResultPanel() {

			JPanel bg = new JPanel();
			bg.setLayout(new GridBagLayout());
			bg.setBackground(Color.LIGHT_GRAY);
			bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			
			text.setFont(new Font("Serif Bold", Font.BOLD, 24));
			
			GridBagConstraints styleText = new GridBagConstraints();
			styleText.gridx = 0;
			styleText.gridy = 0;
			styleText.insets.bottom = 5;
			styleText.anchor = GridBagConstraints.CENTER;
			
			bg.add(text, styleText);
			
			return bg;
	}
		
	public void changeResultText(int amount, String value1, String value2, String nickname) {
			
		if (nickname.equals(boardController.getWindowOwner()))
			text.setText("Your roll is: " + value1 + " " + value2);
		else
			text.setText(nickname + "'s roll is: " + value1 + " " + value2);
		rollResult.repaint();
		rollResult.revalidate();	
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
	 
	 public JPanel createCasinoPanel() {
	  	  
	  	  menuPanelStatus = 5;
	  	  
	  	  JPanel bg = new JPanel();
	  	  
	  	  GridBagLayout gbl = new GridBagLayout();
	  	  gbl.columnWeights = new double[] {0.5, 0.5};
	  	  gbl.rowWeights = new double[]{0.4, 0.2, 0.4};
	  	  
	  	  bg.setLayout(gbl);
	  	  bg.setBackground(Color.LIGHT_GRAY);
	  	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
	  	  
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
	  		  if (boardController.getMoneyAmount() < 1000) {
	  			  UtilityAPI.showError("Not enough money.");
				  return;
	  		  }
	  		  ArrayList<String> predictions = new ArrayList<String>();
	  		  predictions.add(bets[bet1.getSelectedIndex()]);
	  		  String val2 = bets[bet2.getSelectedIndex()];
	  		  if ((val2.equals("No bet") && predictions.contains(val2)) || (!predictions.contains(val2))) {
	  			  predictions.add(val2);
	  		  } else {
	  			  UtilityAPI.showError("There are more than 1 predictions for the same value!");
	  			  return;
	  		  } // handle triple no bet
	  		  String val3 = bets[bet3.getSelectedIndex()];
			  if ((val3.equals("No bet") && predictions.contains(val3)) || (!predictions.contains(val3))) {
				  predictions.add(val3);
			  } else {
				  UtilityAPI.showError("There are more than 1 predictions for the same value!");
				  return;
			  }
			  if (bets[bet1.getSelectedIndex()].equals("No bet") && val2.equals("No bet") && val3.equals("No bet")) {
				  UtilityAPI.showError("You can`t bet without setting at least one prediction.");
				  return;
			  } 
			  boardController.clearMenuPanel();
	  		  
	  		  String result = "";
	  		  
	  		  for (String s : predictions) {
	  			  if (s.equals("No bet"))
	  				  result += " " + 0;
	  			  else 
	  				  result += " " + s;
	  		  }
	  		  result = result.substring(1);
	  		  boardController.sendMsg(MessageType.CASINO_AGREE, result);
	  	  });
	  	  
	  	  firstButton.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
             Dimension size = firstButton.getSize();
             size.width = new Double(boardController.getPreferredMapWidth() / 2.3).intValue();
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
	  		  boardController.clearMenuPanel();
	  		  boardController.sendMsg(MessageType.SKIP_TURN, null);
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
	  	  
	  	  return bg;
	  }
	   
	 
	  public JPanel createGiveUpPanel(String textContent, String firstButtonContent, String secondButtonContent, ActionListener actionResult, ActionListener actionResult1) {

	  	  menuPanelStatus = 5;
	  	  
	  	  JPanel bg = new JPanel();
	  	  
	  	  GridBagLayout gbl = new GridBagLayout();
	  	  gbl.columnWeights = new double[] {0.5, 0.5};
	  	  gbl.rowWeights = new double[]{0.5, 0.5};
	  	  
	  	  bg.setLayout(gbl);
	  	  bg.setBackground(Color.LIGHT_GRAY);
	  	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
	  	  
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
  	             	size.width = new Double(boardController.getPreferredMapWidth() / 2.3).intValue();
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
	  	             size.width = new Double(boardController.getPreferredMapWidth() / 2.3).intValue();
	  	             secondButton.setPreferredSize(size);
	  	             secondButton.setSize(size);
	  	            }
	  	  });
	  	  
	  	  bg.add(text, styleText);
	  	  bg.add(firstButton, styleBuy);
	  	  bg.add(secondButton, styleAuction);
	  	  
	  	  firstButton.setVisible(true);
	  	  secondButton.setVisible(true);
	  	  
	  	  bg.revalidate();
	  	  bg.repaint(); 
	  	  
	  	  return bg;
	 }

	 public JPanel createTwoButtonPanel(String textContent, String firstButtonContent, String secondButtonContent, ActionListener actionResult, ActionListener actionResult1) {
    	  boardController.clearMenuPanel();
    	  
    	  menuPanelStatus = 3;
    	  
    	  JPanel bg = new JPanel();
    	  
    	  GridBagLayout gbl = new GridBagLayout();
    	  gbl.columnWeights = new double[] {0.5, 0.5};
    	  gbl.rowWeights = new double[]{0.5, 0.5};
    	  
    	  bg.setLayout(gbl);
    	  bg.setBackground(Color.LIGHT_GRAY);
    	  bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
    	  
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
             Dimension size = firstButton.getSize();
             size.width = new Double(boardController.getPreferredMapWidth() / 2).intValue();
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
             size.width = new Double(boardController.getPreferredMapWidth() / 2.3).intValue();
             secondButton.setPreferredSize(size);
             secondButton.setSize(size);
            }
    	  });
    	  
    	  bg.add(text, styleText);
    	  bg.add(firstButton, styleBuy);
    	  bg.add(secondButton, styleAuction);
    	  
    	  firstButton.setVisible(true);
    	  secondButton.setVisible(true);
    	  
    	  return bg;
	 }
		
	    public JPanel createPayMenuPanel(String textContent, String buttonContext, ActionListener buttonListener) {
	    	JButton button = new JButton(buttonContext);
	    	button.addActionListener(buttonListener);
	    	return createPayMenuPanel(textContent, buttonContext, button);
	    }
	    
		public JPanel createPayMenuPanel(String textContent, String buttonContent, JButton buttonLink) {
			boardController.clearMenuPanel();
			menuPanelStatus = 4;
			
			JPanel bg = new JPanel();
			bg.setLayout(new GridBagLayout());
			bg.setBackground(Color.LIGHT_GRAY);
			bg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			
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
	            	size.width = boardController.getPreferredMapWidth() / 2;
	            	buttonLink.setPreferredSize(size);
	            	buttonLink.setSize(size);
	            }
	        });
			
			buttonLink.setVisible(true);
			
			bg.add(text, styleText);
			bg.add(buttonLink, stylePay);
			
			return bg;
		}
	
}
