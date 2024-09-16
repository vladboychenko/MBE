package me.ezik.client.logic;

import javax.swing.JComponent;

public class Setting {

	private String name = "unnamed";
	private JComponent component;
	private String helpInfo = "unlisted";
	
	public Setting(String name, JComponent component, String helpInfo) {
		this.name = name;
		this.component = component;
		this.helpInfo = helpInfo;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JComponent getComponent() {
		return component;
	}
	public void setComponent(JComponent component) {
		this.component = component;
	}
	public String getHelpInfo() {
		return helpInfo;
	}
	public void setHelpInfo(String helpInfo) {
		this.helpInfo = helpInfo;
	}
	
}
