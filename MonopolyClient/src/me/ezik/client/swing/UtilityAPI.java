package me.ezik.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import me.ezik.client.logic.Program;
import me.ezik.shared.message.MessageType;

// contains utility gui methods
public class UtilityAPI {

	public static void showError(String message, String code) {
	    showDialog(message, "Error code: " + code, JOptionPane.WARNING_MESSAGE);
	}

	
	public static void showMessage(String message) {
	    showDialog(message, "Operation successful", JOptionPane.PLAIN_MESSAGE);
	}

	
	public static void showError(String message) {
	    showError(message, "unknown");
	}

	private static void showDialog(String message, String title, int messageType) {
	    JDialog dialog = new JDialog(Program.getGUI(), title, Dialog.ModalityType.MODELESS);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setSize(400, 200);
	    dialog.setLocationRelativeTo(Program.getGUI());

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
	
	public static int[] calcOffset(int numberOfChips) {
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
	
	public static Color getColor(String col) {
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
	
	public static Set<Integer> parseSet(String part) {
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
	
}
