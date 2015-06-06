/**
* My attemp at implementing a Conways's Game of Life simulator in in Java
*
* @author John Abraham
*/

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.Thread;

public class GameOfLife {
		
	final JPanel[][] world = new JPanel[60][60];

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException ee) {
			ee.printStackTrace();
		} catch (IllegalAccessException eee) {
			eee.printStackTrace();
		} catch (UnsupportedLookAndFeelException eeee) {
			eeee.printStackTrace();
		}

		new GameOfLife();
	}

	public GameOfLife() {

		final JFrame mainFrame;
		final JPanel northBorderPanel, centerPanel, southBorderPanel;
		final JLabel iterationLabel, simSpeedLabel;
		final JButton stepSimbutton, runSimButton, clearSimButton;
		final JSlider slider;

		/**
		* Set frame size, default close operation, and location.
		*/
		mainFrame = new JFrame("Conway's Game of Life Simulation");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setResizable(false);
		mainFrame.setSize(700, 700);
		centerWindow(mainFrame);

		/**
		* Create FlowLayout JPanel and add configuration tools to it.
		* Add the configuration tool JPanel to the south border of the main frame.
		* Add a JLabel to the north border to keep track of iterations.
		*/
		southBorderPanel = new JPanel(new FlowLayout());
		northBorderPanel = new JPanel(new FlowLayout());
		simSpeedLabel = new JLabel("Set Simulation Speed: ");
		simSpeedLabel.setFont(new Font("Serif", Font.BOLD, 17));
		slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setLabelTable(slider.createStandardLabels(10));
		stepSimbutton = new JButton("Step");
		stepSimbutton.setFont(new Font("Serif", Font.BOLD, 17));
		runSimButton = new JButton("Run");
		runSimButton.setFont(new Font("Serif", Font.BOLD, 17));
		clearSimButton = new JButton("Clear");
		clearSimButton.setFont(new Font("Serif", Font.BOLD, 17));
		iterationLabel = new JLabel("Iteration: 0");
		iterationLabel.setFont(new Font("Serif", Font.BOLD, 17));
		southBorderPanel.add(simSpeedLabel);
		southBorderPanel.add(slider);
		southBorderPanel.add(stepSimbutton);
		southBorderPanel.add(runSimButton);
		southBorderPanel.add(clearSimButton);
		northBorderPanel.add(iterationLabel);
		mainFrame.add(northBorderPanel, BorderLayout.NORTH);
		mainFrame.add(southBorderPanel, BorderLayout.SOUTH);

		/**
		* Add a grid to center panel of the main frame as a startup default.
		* default centerPanel on each iteration after simulation is started.
		*/
		centerPanel = new JPanel(new GridLayout(60, 60));
		for(int x=0; x<60; x++) {
			for(int y=0; y<60; y++) {
				world[x][y] = new JPanel();
				world[x][y].setBackground(Color.darkGray);
				world[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				centerPanel.add(world[x][y]);
			}
		}
		mainFrame.add(centerPanel, BorderLayout.CENTER);

		/**
		* Show the main frame and everything that was added to it.
		*/
		mainFrame.setVisible(true);



		/********************************************************************/
		/*********EVENT LISTENERS AND HANDLERS (ANONYMOUS CLASSES)***********/
		/********************************************************************/
		stepSimbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent unused) {
				iterate();
			}
		});

		runSimButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent unused) {
				/**CONVERT TO INFINITE LOOP AND THREAD THE LOOP**/
				for (int i=0; i<100; i++) {
					iterate();
				}
			}
		});		

		clearSimButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent unused) {
				for (int y=0; y<60; y++) {
					for (int x=0; x<60; x++) {
						world[x][y].setBackground(Color.darkGray);
					}
				}
				iterationLabel.setText("Iteration: 0");
			}
		});

		for (int y=0; y<60; y++) {
			for (int x=0; x<60; x++) {
				final int xx = x;
				final int yy = y;
				world[xx][yy].addMouseListener(new MouseAdapter(){
					@Override
					public void mouseEntered(MouseEvent me) {
						if(me.getModifiers() == MouseEvent.BUTTON1_MASK) {
							world[xx][yy].setBackground(Color.GREEN);
						}
					}

					public void mousePressed(MouseEvent me) {
						if(world[xx][yy].getBackground() == Color.darkGray) {
							world[xx][yy].setBackground(Color.GREEN);
						} else {
							world[xx][yy].setBackground(Color.darkGray);
						}
					}
				});
			}
		}
	}

	/********************************************************************/
	/***********************PRIVATE HELPER METHODS***********************/
	/********************************************************************/
	/**
	* Start the simulation here. This is a one-step iteration.
	* Iterate through the world and change the colors (states) of
	* the cells based on the 4 simple rules of Conway's Game of Life.
	*/
	private void iterate() {

		JPanel[][] new_world = new JPanel[60][60];
		for (int y=0; y<60; y++) {					
			for (int x=0; x<60; x++) {
				new_world[x][y] = new JPanel();
				new_world[x][y].setBackground(Color.darkGray);
				new_world[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
		}

		for (int y=0; y<60; y++) {
			for (int x=0; x<60; x++) {
				if ( getState(world[x][y]) == State.DEAD ) {
					/**1. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.**/
					if (getNumLiveNeighbors(x, y) == 3) {
						new_world[x][y].setBackground(Color.GREEN);
					}
				}

				if ( getState(world[x][y]) == State.ALIVE ) {
					/**2. Any live cell with fewer than two live neighbours dies, as if caused by under-population.**/
					if (getNumLiveNeighbors(x, y) < 2) {
						new_world[x][y].setBackground(Color.darkGray);
					}

					/**3. Any live cell with two or three live neighbours lives on to the next generation.**/
					if (getNumLiveNeighbors(x, y) == 2 || getNumLiveNeighbors(x, y) == 3) {
						new_world[x][y].setBackground(Color.GREEN);
					}

					/**4. Any live cell with more than three live neighbours dies, as if by overcrowding.**/
					if (getNumLiveNeighbors(x, y) > 3) {
						new_world[x][y].setBackground(Color.darkGray);
					}
				}
			}
		}

		for (int y=0; y<60; y++) {
			for (int x=0; x<60; x++) {
				if (new_world[x][y].getBackground() == Color.darkGray)
					world[x][y].setBackground(Color.darkGray);
				else
					world[x][y].setBackground(Color.GREEN);
			}
		}
	}

	/**
	* helper method to get the number of live neighbors for a given cell.
	*/
	private int getNumLiveNeighbors(int x, int y) {

		int[] r_delta = {-1,-1,-1, 0, 0, 1, 1, 1};
		int[] c_delta = {-1, 0, 1,-1, 1,-1, 0, 1};
		int numLiveNeighbors = 0;
		for (int r=0, c=0; r<8; r++, c++) {
			if ( x+r_delta[r] != -1 && x+r_delta[r] != 60 && y+c_delta[c] != -1 && y+c_delta[c] != 60) {
				if ( getState( world[x+r_delta[r]][y+c_delta[c]] ) == State.ALIVE ) { numLiveNeighbors++; }
			}
		}
		return numLiveNeighbors;
	}

	/**
	* helper method that returns the state of the cell based
	* on the color of the JPnael representing the cell.
	*/
	private State getState(JPanel cell) {

		if(cell.getBackground().equals(Color.GREEN))
			return State.ALIVE;
		else
			return State.DEAD;
	}

	/**
	* Private helper mehtod that centers the main frame in the middle of the screen.
	*/
	private void centerWindow(JFrame f) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - f.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - f.getHeight()) / 2);
		f.setLocation(x, y);
	}
}
