package me.ezik.client.swing;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLayeredPane;
import javax.swing.Timer;

public class MovementController {

	private PlayerChip[] playerColors;
	private JLayeredPane globalMapPanel;
	
	private final int widthSlices = 11;  // Number of slices along the width
    private final int heightSlices = 11; // Number of slices along the height
    private int delay;        // Delay in milliseconds between moves
    private Timer timer;
    private int currentPosition;
    private int endPosition;
    
    private final int ROLL_WAITTIME = 1500;
    private ChipMovement alteredMovement = null;

	public MovementController(PlayerChip[] chips, JLayeredPane mapPanel) {
		this.playerColors = chips;
		this.globalMapPanel = mapPanel;
	}
    
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
	
	public void alterMovement(ChipMovement chip) {
		alteredMovement = chip;
	}
	
	public void moveChip(ChipMovement chipMovement) {
		if (alteredMovement != null && alteredMovement.getChip().equals(chipMovement.getChip())) {
			int[] movement = alteredMovement.getMovement();
			this.navigate(chipMovement.getChip(), movement[0], movement[1]);
			alteredMovement = null;
		} else {
			int[] movement = chipMovement.getMovement();
			this.navigate(chipMovement.getChip(), movement[0], movement[1]);
		}
	}
	
}
