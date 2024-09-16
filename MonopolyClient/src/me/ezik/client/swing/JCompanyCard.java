package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

public class JCompanyCard extends JAbstractCard {

	private static final long serialVersionUID = 1L;
	
	private String moneyDisplayed = "0";
	private String cost = "0";
	private Color ownerColor = Color.WHITE;
	private String name = "Unspecified";
	private JPanel ownerPanel = new JPanel() {
		
		@Override
		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    int width = getWidth();
	        int height = getHeight();

	        Graphics2D g2d = (Graphics2D) g;
	        
	        if (isLayout) {
			    for (int i = 0; i < width + height; i += 20) {
				       g2d.setColor(ownerColor);
				       g2d.drawLine(i, 0, 0, i);
				}	
	        } else {
	        	 g2d.setColor(ownerColor);
	             g2d.fillRect(0, 0, width, height);
	        }
		}
	};
	
	private JLabel costLabel = new JLabel();
	private String logoName = "logo0";
	private double angle = 0;
	private double scale = 1;
	private double delta = 0.01;
	Rectangle.Float r = new Rectangle.Float(20, 20, 200, 200);
	private int position = 0;
	
	private int starAmount = 0;
	
	private boolean isLayout = false;
	private int layoutTimer = 15;
	public static boolean t = false;
	private int rotation;
	
	public String getLogo() {
		return logoName;
	}
	
	public String getCompanyName() {
		return name;
	}
	
	public void changeOwner(Color newOwner) {
		ownerColor = newOwner;
	}
	
	public void changeCost(String cost) {
		moneyDisplayed = cost;
	}
	
	public void changeStarAmount(int starAmount) {
		this.starAmount = starAmount;
	}
	
	public boolean isLayouted() {
		return isLayout;
	}
	
	public void setLayouted(boolean val) {
		if (val) {
			isLayout = true;
			layoutTimer = 15;
		} else {
			isLayout = false;
		}
	}
	
	public void decreaseLayoutTimer() {
		layoutTimer--;
	}
	
	public String getCost() {
		return cost;
	}
	  
	public JCompanyCard(Color ownership, String cost, String name, String logoName, int paintRotation, int position) {
		this.moneyDisplayed = cost;
		this.cost = cost;
		this.ownerColor = ownership;
		this.name = name;
		this.logoName = logoName;
		this.rotation = paintRotation;
		this.position = position;
		switch(paintRotation) {
			case 0:
			case 2:
				paintCard();
			break;
			case 1:
				paintRight();
			break;
			case 3: 
				paintLeft();
			break;
		}
	}
	
	public void repaintCard() {
		this.removeAll();
		switch(this.rotation) {
			case 0:
			case 2:
				paintCard();
			break;
			case 1:
				paintRight();
			break;
			case 3: 
				paintLeft();
			//	paintRight();
			break;
		}
	}
	
	public void paintCard() {
		this.setPreferredSize(new Dimension(70, 120));
		this.setLayout(new GridBagLayout());
		GridBagConstraints styleCard = new GridBagConstraints();
		styleCard.gridx = 0;
		styleCard.gridy = 0;
		styleCard.anchor = GridBagConstraints.PAGE_START;
		styleCard.fill = GridBagConstraints.BOTH;
		styleCard.insets.bottom = 0;
		styleCard.insets.left = 0;
		styleCard.insets.top = 0;
		styleCard.weightx=1.0d;
		styleCard.insets.right = 0;
		
		this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		costLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		if (!isLayout)
			costLabel.setText(moneyDisplayed);
		else
			costLabel.setText("FREE");
		costLabel.setHorizontalAlignment(SwingConstants.CENTER);
		costLabel.setPreferredSize(new Dimension(70, 35));
		this.add(costLabel, styleCard);
		
		BufferedImage myPicture = null;
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
        try {
            myPicture = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));
        
        styleCard.gridy++;
        styleCard.anchor = GridBagConstraints.CENTER;
        styleCard.fill = GridBagConstraints.BOTH;
        styleCard.fill = GridBagConstraints.VERTICAL;
        styleCard.insets.top = 10;
        
        JLabel lockLabel = null;
		this.add(picLabel, styleCard);
		if (isLayout) {
	        InputStream lockStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/lock.png");
	        try {
	            myPicture = ImageIO.read(lockStream);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        lockLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			this.add(lockLabel, styleCard, 0);
		}
		
		styleCard.fill = GridBagConstraints.BOTH;
		styleCard.gridy++;
		styleCard.insets.top = 10;
		styleCard.weighty=1.0d;
		styleCard.anchor = GridBagConstraints.SOUTH;
		ownerPanel = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    int width = getWidth();
		        int height = getHeight();

		        Graphics2D g2d = (Graphics2D) g;
		        this.setBackground(Color.WHITE);
		        if (isLayout) {
				    for (int i = 0; i < width + height; i += 20) {
					       g2d.setColor(ownerColor);
					       g2d.drawLine(i, 0, 0, i);
					}	
		        } else {
		        	this.setBackground(ownerColor);
//		        	 g2d.setColor(ownerColor);
//		             g2d.fillRect(0, 0, width, height);
		        }
			}
		}; 
		ownerPanel.setPreferredSize(new Dimension(100, 35));
		ownerPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
		ownerPanel.setLayout(new GridBagLayout());
	    ownerPanel.removeAll();
	    
		if (!isLayout) {
			JLabel starNumber = new JLabel("" + starAmount);
			Color textColor = getContrastingColor(ownerColor);
			starNumber.setForeground(textColor);
			ownerPanel.add(starNumber);
		
			InputStream starStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/star.png");
			try {
				myPicture = ImageIO.read(starStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    JLabel star = new JLabel(new ImageIcon(myPicture.getScaledInstance(18, 18, Image.SCALE_DEFAULT)));
		    ownerPanel.add(star);
		} else {
			JLabel turnAmount = new JLabel(layoutTimer + "");
			ownerPanel.add(turnAmount);
		}
	    
//	    ownerPanel.add(starPanel);
		this.add(ownerPanel, styleCard);
		
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
	}
	
	public void addOnClickReaction(Consumer<JCompanyCard> mousePressedFunction) {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedFunction.accept(JCompanyCard.this);
            }
        });
    }
	
	public Color getCurrentOwnerColor() {
		return ownerColor;
	}
	
	public int getPosition() {
		return position;
	}
	
	 private Color getContrastingColor(Color color) {
	        // Calculate luminance
	        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;

	        // Choose black or white based on luminance
	        return luminance > 0.5 ? Color.black : Color.white;
	 }
	
	  public void paintLeft() {
	        this.setPreferredSize(new Dimension(120, 70));  // Swap width and height for rotation
	        this.setLayout(new GridBagLayout());
	        GridBagConstraints styleCard = new GridBagConstraints();
	        styleCard.gridx = 0;
	        styleCard.gridy = 0;
	        styleCard.anchor = GridBagConstraints.LINE_START;  // Change anchor to LINE_START
	        styleCard.fill = GridBagConstraints.VERTICAL;  // Change fill to VERTICAL
	        styleCard.weightx = 0.5;  // Adjust weightx to distribute space horizontally

	        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));

	        // Rotate text in costLabel
	        costLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	        if (!isLayout)
				costLabel.setText(moneyDisplayed);
			else
				costLabel.setText("FREE");
	        costLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        costLabel.setPreferredSize(new Dimension(35, 70));  // Swap width and height for rotation
	        costLabel.setUI(new VerticalLabelUI(false));
	        this.add(costLabel, styleCard);

	        BufferedImage myPicture = null;
	        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
	        try {
	            myPicture = ImageIO.read(imageStream);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));
	        picLabel.setUI(new VerticalLabelUI(false));
	        
	        styleCard.gridx++;
	        styleCard.anchor = GridBagConstraints.CENTER;  // Change anchor to CENTER for picLabel
	        styleCard.fill = GridBagConstraints.NONE;  // Change fill to NONE for picLabel
	        styleCard.insets.left = 7;  // Change insets.left for spacing
	        this.add(picLabel, styleCard);

	        JLabel lockLabel = null;
//			this.add(picLabel, styleCard);
			if (isLayout) {
		        InputStream lockStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/lock.png");
		        try {
		            myPicture = ImageIO.read(lockStream);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        lockLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
				this.add(lockLabel, styleCard, 0);
			}
	        
	        styleCard.fill = GridBagConstraints.BOTH;  // Change fill to VERTICAL
	        styleCard.gridx++;
	        styleCard.insets.left = 7;
	        styleCard.anchor = GridBagConstraints.LINE_END;  // Change anchor to LINE_END
	        styleCard.weighty = 1.0;  // Set weighty to make ownerLabel take up vertical space

	        ownerPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	//		ownerPanel.setBackground(ownerColor);

			ownerPanel.setPreferredSize(new Dimension(35, 70));  // Swap width and height for rotation
	        ownerPanel.setLayout(new GridBagLayout());
		    ownerPanel.removeAll();
		    
			if (!isLayout) {
				JLabel starNumber = new JLabel("" + starAmount);
				Color textColor = getContrastingColor(ownerColor);
				starNumber.setForeground(textColor);
				starNumber.setUI(new VerticalLabelUI(false));
				
			    GridBagConstraints gbc = new GridBagConstraints();
			    gbc.gridx = 0;
			    gbc.gridy = 1;
				
				ownerPanel.add(starNumber, gbc);
			
				InputStream starStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/star.png");
				try {
					myPicture = ImageIO.read(starStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			    JLabel star = new JLabel(new ImageIcon(myPicture.getScaledInstance(18, 18, Image.SCALE_DEFAULT)));
			    star.setUI(new VerticalLabelUI(false));
			    
			    gbc.gridx = 0;
			    gbc.gridy = 0;
			    
			    ownerPanel.add(star, gbc);
			} else {
				JLabel turnAmount = new JLabel(layoutTimer + "");
				turnAmount.setUI(new VerticalLabelUI(false));
				ownerPanel.add(turnAmount);
			}
		    
//		    ownerPanel.add(starPanel);
			this.add(ownerPanel, styleCard); 

	        this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	    }
	  
	  public void paintRight() {
	        this.setPreferredSize(new Dimension(120, 70));  // Swap width and height for rotation
	        this.setLayout(new GridBagLayout());
	        GridBagConstraints styleCard = new GridBagConstraints();
	        styleCard.gridx = 0;
	        styleCard.gridy = 0;
	        styleCard.anchor = GridBagConstraints.LINE_START;  // Change anchor to LINE_START
	        styleCard.fill = GridBagConstraints.BOTH;  // Change fill to VERTICAL
	        styleCard.weightx = 0.5;  // Adjust weightx to distribute space horizontally

	        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));

	        ownerPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

			ownerPanel.setPreferredSize(new Dimension(35, 70));  // Swap width and height for rotation
			ownerPanel.setLayout(new GridBagLayout());
		    ownerPanel.removeAll();
		    
		    BufferedImage myPicture = null;
			if (!isLayout) {
				JLabel starNumber = new JLabel("" + starAmount);
				Color textColor = getContrastingColor(ownerColor);
				starNumber.setForeground(textColor);
				starNumber.setUI(new VerticalLabelUI(true));
				
			    GridBagConstraints gbc = new GridBagConstraints();
			    gbc.gridx = 0;
			    gbc.gridy = 1;
				
				ownerPanel.add(starNumber, gbc);
			
				InputStream starStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/star.png");
				try {
					myPicture = ImageIO.read(starStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			    JLabel star = new JLabel(new ImageIcon(myPicture.getScaledInstance(18, 18, Image.SCALE_DEFAULT)));
			    star.setUI(new VerticalLabelUI(true));
			    
			    gbc.gridx = 0;
			    gbc.gridy = 0;
			    
			    ownerPanel.add(star, gbc);
			} else {
				JLabel turnAmount = new JLabel(layoutTimer + "");
				turnAmount.setUI(new VerticalLabelUI(true));
				ownerPanel.add(turnAmount);
			}
			this.add(ownerPanel, styleCard); 
	        // Rotate text in costLabel
	        
	        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/" + logoName + ".png");
	        try {
	            myPicture = ImageIO.read(imageStream);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(60, 60, Image.SCALE_DEFAULT)));
	        JLabel lockLabel = null;
			this.add(picLabel, styleCard);
	        
	        styleCard.gridx++;
	        styleCard.anchor = GridBagConstraints.CENTER;  // Change anchor to CENTER for picLabel
	        styleCard.fill = GridBagConstraints.NONE;  // Change fill to NONE for picLabel
	        styleCard.insets.left = 7;  // Change insets.left for spacing
	        this.add(picLabel, styleCard);

			if (isLayout) {
		        InputStream lockStream = getClass().getClassLoader().getResourceAsStream("me/ezik/client/assets/lock.png");
		        try {
		            myPicture = ImageIO.read(lockStream);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        lockLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
				this.add(lockLabel, styleCard, 0);
			}
	        
	        styleCard.fill = GridBagConstraints.VERTICAL;  // Change fill to VERTICAL
	        styleCard.gridx++;
	        styleCard.insets.left = 7;
	        styleCard.anchor = GridBagConstraints.LINE_END;  // Change anchor to LINE_END
	        styleCard.weighty = 1.0;  // Set weighty to make ownerLabel take up vertical space

	        costLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	        if (!isLayout)
				costLabel.setText(moneyDisplayed);
			else
				costLabel.setText("FREE");
	        costLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        costLabel.setPreferredSize(new Dimension(35, 70));  // Swap width and height for rotation
	        costLabel.setUI(new VerticalLabelUI(true));
	        this.add(costLabel, styleCard);

	        this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	    }
	
}
