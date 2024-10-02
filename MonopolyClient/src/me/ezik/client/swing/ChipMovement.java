package me.ezik.client.swing;

public class ChipMovement {
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
