package me.ezik.client.logic.messaging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;

import me.ezik.client.logic.Program;
import me.ezik.client.swing.ChipMovement;
import me.ezik.client.swing.JAbstractCard;
import me.ezik.client.swing.JCompanyCard;
import me.ezik.client.swing.MovementController;
import me.ezik.client.swing.PlayerChip;
import me.ezik.client.swing.UtilityAPI;
import me.ezik.client.swing.Window;
import me.ezik.shared.message.Message;
import me.ezik.shared.message.MessageType;

public class ResponseHandler implements MessageListener {

	private Window gui = Program.getGUI();
	
	@Override
	public void handle(Message msg) {
//		switch(msg.getMsgType()) {
//			case AUTH:
//				username = textField[0].getText();
//				Program.serverListener.setAuthToken(msg.getAuthToken());
//				initMainLeftComponents();
//				defaultPfp = Integer.parseInt(msg.getText());
//				initMainTopComponents(defaultPfp);
//				initMainCenterComponents();
//				initCenterPanelMenu();
//				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
//	        break;
//			case REG:
//				this.username = textField[0].getText();
//				Program.serverListener.setAuthToken(msg.getAuthToken());
//				initMainLeftComponents();
//				defaultPfp = Integer.parseInt(msg.getText());
//				initMainTopComponents(defaultPfp);
//				initMainCenterComponents();
//				initCenterPanelMenu();
//				Program.serverListener.sendMsg(MessageType.SEARCH_GAMES, "");
//	        break;
//			case ERROR:
//				UtilityAPI.showError(msg.getText());
//			break;
//			case SEARCH_GAMES:
//				try {
//					refreshGameList(msg.getText());
//				} catch(Exception ex) {
//					UtilityAPI.showError(ex.toString() + ex.getStackTrace()[0].toString());
//				}
//	        break;
//			case GET_GAME:
//				centerPanel.removeAll();
//				showGameInfo(msg.getText());
//			break;
//			case SAVE_AS_DEFAULT:
//			case SAVE_SETTINGS:
//				UtilityAPI.showMessage(msg.getText());
//			break;
//			case CHANGE_PFP:
//				sidePanels[0].removeAll();
//				defaultPfp = Integer.parseInt(msg.getText());
//				initMainTopComponents(Integer.parseInt(msg.getText()));
//			break;
//			case START_GAME:
//				this.remove(sidePanels[2]);
//				this.remove(sidePanels[3]);
//				sidePanels[4].removeAll();
//				sidePanels[0].removeAll();
//				sidePanels[0].setPreferredSize(new Dimension(70, 70));
//				initGameTopComponents(msg.getText(), 15000);
//				Program.serverListener.sendMsg(MessageType.LOAD_MAP, "");
//			break;
//			case LOAD_MAP:
//				this.remove(sidePanels[2]);
//				this.remove(sidePanels[3]);
//				sidePanels[4].removeAll();
//				globalMapPanel = initGameComponents();
//				movementController = new MovementController(playerColors, globalMapPanel);
//				try {
//					displayMap(msg.getText(), globalMapPanel);
//				} catch (OutOfMemoryError ex) {
//					UtilityAPI.showError("Java could not allocate enough memory to load map. Increase to JVM memory heap amount recommended.");
//				}
//				
//				this.revalidate();
//				this.repaint();
//			break;
//			case GET_COMPANY:
//				initCompanyInfoPanel(infoP, msg.getText());
//			break;
//			case SEND_MESSAGE:
//				append(msg.getText(),  Color.black, new Color(225, 225, 225), false, false, false);
//			break;
//			case SEND_SYSTEM_MESSAGE:
//				append(msg.getText(),  Color.black, new Color(225, 225, 225), true, false, false);
//			break;
//			case SEND_DELAYED_MESSAGE:
//				delayedMsgs.add(msg.getText());
//			break;
//			case ROLL:
//				stopContinuousRoll();
//				startTimedRoll(1500, msg.getText());
//			break;
//			case START_TURN:
//				timerLabel.setVisible(true);
//				roll.setVisible(true);
//				isAuction = false;
//				if (tradePanel != null)
//					tradePanel.setVisible(false);
//				createRollMenuPanel(map.size());
//			break;
//			case JAIL_START:
//				String panelStyle = msg.getText();
//				switch (panelStyle) {
//					case "1":
//						ActionListener ac = new ActionListener() {
//						@Override
//						    public void actionPerformed(ActionEvent e) {
//						    	if (moneyAmount >= 1000) {
//						    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
//									clearMenuPanel();
//						    	} else {
//						    		UtilityAPI.showError("Not enough money.");
//						    	}
//								
//						  	}
//						};
//						ActionListener ac1 = new ActionListener() {
//						    @Override
//						    public void actionPerformed(ActionEvent e) {
//								Program.serverListener.sendMsg(MessageType.ROLL, null);
//								clearMenuPanel();
//							  
//						  	}
//						};
//						createTwoButtonPanel("<html>You`ve got to jail. You can pay or try to <br>roll for double to quit.</html>", "Pay (1000k UAH)",
//								"Roll for double", ac, ac1);
//					break;
//					case "2":
//						ActionListener payListener = new ActionListener() {
//						    @Override
//						    public void actionPerformed(ActionEvent e) {
//						    	if (moneyAmount >= 1000) {
//						    		Program.serverListener.sendMsg(MessageType.JAIL_PAY, null);
//									clearMenuPanel();
//						    	} else {
//						    		UtilityAPI.showError("Not enough money.");
//						    	}
//						  	}
//						};
//						createPayMenuPanel("<html>You`ve exhausted all your attempts to get out of <br> jail and is forced to pay. </html>", "Pay (1000k UAH)", payListener);
//					break;
//					default:
//						UtilityAPI.showError("Error parsing jail panel style...");
//					break;
//				}
//			break;
//			case UPDATE_TIMER:
//				String message = msg.getText();
//			    String[] deadlineString = message.split("\\s++"); 
//			    if (deadlineString[1] != null && !deadlineString[1].isEmpty()) {
//			        try {
//			            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//			            Date deadline = dateFormat.parse(deadlineString[1]);
//			            try {
//			            	startTimer(deadline, deadlineString[0]);
//			            } catch (Exception ex) {
//			            	Program.serverListener.sendMsg(MessageType.DEBUG, "Exception: " + ex.getMessage());
//			            }
//			            
//			        } catch (ParseException e) {
//			            e.printStackTrace();
//			        }
//			    }
//			break;
//			case TIMEOUT:
//				if (username.equals(msg.getText())) {
//					clearMenuPanel();
//					openTrade.setVisible(false);
//					giveUp.setVisible(false);
//					UtilityAPI.showMessage("You have been eliminated by time. You can still watch the game unfold.");
//				}
//				append(msg.getText() + " have been eliminated!",  Color.black, new Color(225, 225, 225), true, false, false);
//				int i = 0;
//				for (PlayerChip chip : playerColors) {
//					if (chip.getUsername().equals(msg.getText())) {
//						globalMapPanel.remove(chip);
//						picLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));;
//						nameLabels[i].setText("<html>" + msg.getText() + "<br> bankrupt </html>");;
//						picLabels[i].repaint();
//						nameLabels[i].repaint();
//						globalMapPanel.repaint();
//						picLabels[i].revalidate();
//						nameLabels[i].revalidate();
//						globalMapPanel.revalidate();
//						break;
//					}
//					i++;
//				}
//			break;
//			case UPDATE_MONEY:
//				Program.serverListener.sendMsg(MessageType.DEBUG, msg.getText());
//			    String data[] = msg.getText().split("\\s++");
//			    for (int z = 0; z < data.length; z += 2) {
//			        if (username.equals(data[z])) {
//			            moneyAmount = Integer.parseInt(data[z + 1]);
//			        }
//			        int j = 0;
//			        for (PlayerChip chip : playerColors) {
//			            if (chip.getUsername().equals(data[z])) {
//			                nameLabels[j].setText("<html>" + data[z] + "<br> Money:" + data[z + 1] + "</html>");
//			                nameLabels[j].repaint();
//			                nameLabels[j].revalidate();
//			                break;
//			            }
//			            j++;
//			        }
//			    }
//		    break;
//			case UPDATE_CARD:
//				String cardData[] = msg.getText().split("\\s++");
//				JAbstractCard card = map.get(Integer.parseInt(cardData[0]));
//				((JCompanyCard) card).changeOwner(UtilityAPI.getColor(cardData[1]));
//				((JCompanyCard) card).changeCost(cardData[2]);
//				int starAmount = Integer.parseInt(cardData[3]);
//				if (starAmount != 0)
//					starAmount--;
//				((JCompanyCard) card).changeStarAmount(starAmount);
//				((JCompanyCard) card).setLayouted(Boolean.parseBoolean(cardData[4]));
//				((JCompanyCard) card).repaintCard();
//			break;
//			case AUCTION:
//				String[] auctionData = msg.getText().split("\\s++");
//				isAuction = true;
//				createTwoButtonPanel("<html>Do you want to take part in auction for <br> company " + auctionData[0].replace("_", " ") + 
//						"</html>", "Ramp up price " + auctionData[1], "Decline", 
//						new ActionListener() {
//						    @Override
//						    public void actionPerformed(ActionEvent e) {
//						    	if (moneyAmount >= Integer.parseInt(auctionData[1])) {
//									Program.serverListener.sendMsg(MessageType.AUCTION_YES, null);
//									isAuction = false;
//									clearMenuPanel();
//						    	} else {
//						    		UtilityAPI.showError("You don`r have enough money...");
//						    	}
//						  	}
//						}, 
//						new ActionListener() {
//					    @Override
//					    public void actionPerformed(ActionEvent e) {
//							Program.serverListener.sendMsg(MessageType.AUCTION_NO, null);
//							clearMenuPanel();
//							isAuction = false;
//					  	}
//				});
//			break;
//			case UPDATE_CHIP:
//				String[] chipData = msg.getText().split("\\s++");
//				
//				int[] clientPosition = new int[2];
//				clientPosition[0] = Integer.parseInt(chipData[1]);
//				clientPosition[1] = Integer.parseInt(chipData[2]);
//				
//				for (PlayerChip chip : playerColors) {
//					if (chip.getUsername().equals(chipData[0])) {
//						movementController.alterMovement(new ChipMovement(chip, clientPosition[0], clientPosition[1]));
//						break;
//					}
//				}
//				
//			break;
//			case DRAW_LAYOUT:
//				String cards[] = msg.getText().split("\\s++");
//				ArrayList<JCompanyCard> cds = new ArrayList<JCompanyCard>();
//				for (String st : cards) {
//					cds.add((JCompanyCard) map.get(Integer.parseInt(st)));
//				}
//				for (JCompanyCard target : cds) {
//					if (target.isLayouted()) {
//						target.decreaseLayoutTimer();
//					} else
//						target.setLayouted(true);
//					target.repaintCard();
//				}
//			break;
//			case TRADE_OFFER:
//				createTradeOffer(msg.getText());
//			break;
//			case WIN:
//				String username = msg.getText();
//				createWinDialog(username, playerColor.get(username), "logo" + userPfp.get(username));
//			break;
//			case CLEAR_TRADE:
//				tradePanel.setVisible(false);
//			break;
//			case CLEAR_MENU:
//				clearMenuPanel();
//			break;
//			case ROLLBACK_PANEL:
//				bg.setVisible(true);
//			break;
//			case KEEP_ALIVE:
//				System.out.println("pong");
//			break;
//		}
	}
	
}
