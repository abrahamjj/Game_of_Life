/**
* View: displays the state of the unverse to the screen,
* and provides control tools for the simulator.
*
* @author John Abraham
*/

import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.UnsupportedLookAndFeelException;

public class View {

	/**Global Constants**/
	final int UNIVERSE_SIZE_X = 55;
	final int UNIVERSE_SIZE_Y = 61;
	final Color ALIVE_CELL_COLOR = new Color(0, 200, 0);
	final Color DEAD_CELL_COLOR = new Color(64, 64, 64);

	/**Global variables**/
	JSlider slider;
	JLabel generationLabel;
	JPanel[][] universe = new JPanel[UNIVERSE_SIZE_X][UNIVERSE_SIZE_Y];
	JButton stepSimbutton, runSimButton, clearSimButton, stopSimulation;
	JComboBox<String> preconfigurationComboBox, autoFillComboBox;

	public View() {
		
		JFrame mainFrame;
		JLabel simSpeedLabel;
		JPanel northBorderPanel, centerPanel;

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

		/**
		* Set frame size, default close operation, layout, title, and logo.
		*/
		mainFrame = new JFrame("Conway's Game of Life Simulator");
		mainFrame.setIconImage(new ImageIcon(GameOfLife.class.getResource("img/logo.png")).getImage());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setResizable(false);

		/**
		* Add configuration and simulation control tools to North border..
		*/
		northBorderPanel = new JPanel(new FlowLayout());
		simSpeedLabel = new JLabel("<html><font color='rgb(0,200,0)'>Speed (Hz):</font></html>");
		simSpeedLabel.setFont(new Font("Serif", Font.BOLD, 12));
		simSpeedLabel.setBackground(Color.GREEN);
		slider = new JSlider(JSlider.HORIZONTAL, 1, 30, 1);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setLabelTable(slider.createStandardLabels(10));
		stepSimbutton = new JButton("Step");
		stepSimbutton.setFont(new Font("Serif", Font.BOLD, 10));
		runSimButton = new JButton("Run");
		runSimButton.setFont(new Font("Serif", Font.BOLD, 10));
		stopSimulation = new JButton("Stop");
		stopSimulation.setFont(new Font("Serif", Font.BOLD, 10));
		clearSimButton = new JButton("Clear");
		clearSimButton.setFont(new Font("Serif", Font.BOLD, 10));
		String[] configurationOptions = { "Pre-Configurations","Horizontal Line",
										  "Vertical Line","Glider","Gosper Glider Gun",
										  "Lightweight Spaceship","Pulsar", "Pentadecathlon" };
		preconfigurationComboBox = new JComboBox<String>(configurationOptions);
		preconfigurationComboBox.setFont(new Font("Serif", Font.BOLD, 10));
		String[] autoFillOptions = { "Autofill", "10%", "20%", "30%", "40%",
							        "50%", "60%", "70%", "80%", "90%" };
		autoFillComboBox = new JComboBox<String>(autoFillOptions);
		autoFillComboBox.setFont(new Font("Serif", Font.BOLD, 10));
		generationLabel = new JLabel("<html><font color='rgb(0,200,0)'>Generation: 0</font></html>");
		generationLabel.setFont(new Font("Serif", Font.BOLD, 12));
		northBorderPanel.add(simSpeedLabel);
		northBorderPanel.add(slider);
		northBorderPanel.add(stepSimbutton);
		northBorderPanel.add(runSimButton);
		northBorderPanel.add(stopSimulation);
		northBorderPanel.add(clearSimButton);
		northBorderPanel.add(preconfigurationComboBox);
		northBorderPanel.add(autoFillComboBox);
		northBorderPanel.add(generationLabel);
		northBorderPanel.setBackground(DEAD_CELL_COLOR);
		mainFrame.add(northBorderPanel, BorderLayout.NORTH);
 
 		/**
		* Add a grid to center panel of the main frame as a startup default.
		* Update the center panel on each iteration after simulation is started.
		*/
		centerPanel = new JPanel(new GridLayout(UNIVERSE_SIZE_X, UNIVERSE_SIZE_Y));
		for(int x=0; x<UNIVERSE_SIZE_X; x++) {
			for(int y=0; y<UNIVERSE_SIZE_Y; y++) {
				universe[x][y] = new JPanel();
				universe[x][y].setBackground(DEAD_CELL_COLOR);
				universe[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				centerPanel.add( universe[x][y] );
			}
		}
		mainFrame.add(centerPanel, BorderLayout.CENTER);

		/**
		* Display all components in the JFrame.
		*/
		mainFrame.pack();
		mainFrame.setVisible(true);

		/**
		* Center JFrame in middle of screen.
		*/
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int xx = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
		int yy = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
		mainFrame.setLocation(xx, yy);

	}

	/*************************************************/
	/**CONTROLLER NEEDS ACCESS TO JCOMPONENTS TO ADD**/
	/**ACTIONLISTENERS AND EVENT HANDLERS TO THEM*****/
	/*************************************************/
	public int getSliderValue() {
		return slider.getValue();
	}

	public JButton getStepButton() {
		return stepSimbutton;
	}

	public JButton getRunButton() {
		return runSimButton;
	}

	public JButton getClearButton() {
		return clearSimButton;
	}

	public JButton getStopButton() {
		return stopSimulation;
	}

	public JComboBox getAutoFillComboBox() {
		return autoFillComboBox;
	}

	public JComboBox getPreconfigurationComboBox() {
		return preconfigurationComboBox;
	}

	public JPanel getPanel(int x, int y) {
		return universe[x][y];
	}

	/**********************************************/
	/******FOR CELL COLOR/STATE MANIPULATION*******/
	/**********************************************/
	public State getPanelState(int x, int y) {
		if(universe[x][y].getBackground() == DEAD_CELL_COLOR)
			return State.DEAD;
		else
			return State.ALIVE;
	}
 
	public void setPanelState(int x, int y, State state) {
		if(state == State.DEAD)
			universe[x][y].setBackground(DEAD_CELL_COLOR);
		else
			universe[x][y].setBackground(ALIVE_CELL_COLOR);
	}

	public void setGenerationText(int generationNum) {
		generationLabel.setText("<html><font color='rgb(0,200,0)'>Generation: "+generationNum+"</font></html>");
	}

	public int getUniverseSizeRows() {
		return UNIVERSE_SIZE_X;
	}

	public int getUniverseSizeColumns() {
		return UNIVERSE_SIZE_Y;
	}
}