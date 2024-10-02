package me.ezik.client.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MoneyPanel {
	
    private JPanel panel;
    private JPanel itemsPanel;
    private JTextField moneyField;
    private JLabel value;
    
    public void addItem(Component comp) {
    	itemsPanel.add(comp);
    }
    
    public void addValue(String strValue) {
    	int intValue = Integer.parseInt(strValue);
    	int newValue = (Integer) value.getClientProperty("value") + intValue;
    	value.putClientProperty("value", newValue);
    	value.setText("Company value: " + newValue);
    	value.revalidate();
    	value.repaint();
    }
    
    public String getTradeMoneyAmount() {
    	return moneyField.getText();
    }

    public JPanel getPanel() {
    	return panel;
    }
    
    public void updateItems() {
    	itemsPanel.revalidate();
    	itemsPanel.repaint();
    }
    
    private MoneyPanel(JPanel panel, JPanel itemsPanel, JTextField moneyField, JLabel value) {
        this.panel = panel;
        this.itemsPanel = itemsPanel;
        this.moneyField = moneyField;
        this.value = value;
    }
    
    public static MoneyPanel createMoneyPanel(String moneyText, int money, String value) {
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
    
    public static MoneyPanel createMoneyPanel(String moneyText) {
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
   
}