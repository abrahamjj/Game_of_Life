/**
* View: displays the state of the unverse to the screen.
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

	/**Global variables**/
	JSlider slider;
	JLabel generationLabel;
	int universeSize = 60;
	JPanel[][] universe = new JPanel[universeSize][universeSize];
	JButton stepSimbutton, runSimButton, clearSimButton, stopSimulation;
	Color aliveCellColor = new Color(0, 200, 0);
	Color deadCellColor = Color.darkGray;

	public View() {
		
		JFrame mainFrame;
		JLabel simSpeedLabel;
		JPanel northBorderPanel, centerPanel, southBorderPanel;
		JComboBox configurationComboBox;

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
		* Set frame size, default close operation, and location.
		*/
		mainFrame = new JFrame("Conway's Game of Life Simulation");
		mainFrame.setIconImage(new ImageIcon(GameOfLife.class.getResource("/logo.png")).getImage());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setResizable(true);
		mainFrame.setSize(855, 725);

		/**Center JFrame in middle of screen**/
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int xx = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
		int yy = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
		mainFrame.setLocation(xx, yy);

		/**
		* Create FlowLayout JPanel and add configuration tools to it.
		* Add the configuration tool JPanel to the south border of the main frame.
		* Add a JLabel to the north border to keep track of generations.
		*/
		southBorderPanel = new JPanel(new FlowLayout());
		northBorderPanel = new JPanel(new FlowLayout());
		simSpeedLabel = new JLabel("Simulation Speed: ");
		simSpeedLabel.setFont(new Font("Serif", Font.BOLD, 16));
		slider = new JSlider(JSlider.HORIZONTAL, 1, 1000, 500);
		slider.setMajorTickSpacing(100);
		slider.setPaintTicks(true);
		slider.setInverted(true);
		slider.setLabelTable(slider.createStandardLabels(10));
		stepSimbutton = new JButton("Step");
		stepSimbutton.setFont(new Font("Serif", Font.BOLD, 16));
		runSimButton = new JButton("Run");
		runSimButton.setFont(new Font("Serif", Font.BOLD, 16));
		stopSimulation = new JButton("Stop");
		stopSimulation.setFont(new Font("Serif", Font.BOLD, 16));
		clearSimButton = new JButton("Clear");
		clearSimButton.setFont(new Font("Serif", Font.BOLD, 16));
		generationLabel = new JLabel("Generation: 0");
		generationLabel.setFont(new Font("Serif", Font.BOLD, 16));
		String[] configurationOptions = {"Select Configuration", "Gosper Glider Gun", 
							"Horizontal Line", "Vertical Line", "X",
							"Gliders", "Pulsar"
						   };
		configurationComboBox = new JComboBox(configurationOptions);
		configurationComboBox.setFont(new Font("Serif", Font.BOLD, 15));
		southBorderPanel.add(simSpeedLabel);
		southBorderPanel.add(slider);
		southBorderPanel.add(stepSimbutton);
		southBorderPanel.add(runSimButton);
		southBorderPanel.add(stopSimulation);
		southBorderPanel.add(clearSimButton);
		southBorderPanel.add(configurationComboBox);
		northBorderPanel.add(generationLabel);
		mainFrame.add(northBorderPanel, BorderLayout.NORTH);
		mainFrame.add(southBorderPanel, BorderLayout.SOUTH);

		/**
		* Add a grid to center panel of the main frame as a startup default.
		* Update centerPanel on each iteration after simulation is started.
		*/
		centerPanel = new JPanel(new GridLayout(universeSize, universeSize));
		for(int y=0; y<universeSize; y++) {
			for(int x=0; x<universeSize; x++) {
				universe[x][y] = new JPanel();
				universe[x][y].setBackground(deadCellColor);
				universe[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				centerPanel.add( universe[x][y] );
			}
		}
		mainFrame.add(centerPanel, BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	/**********************************************/
	/**CONTROLLER NEEDS ACCESS TO JBUTTONS TO ADD**/
	/**ACTIONLISTENERS AND EVENT HANDLERS TO THEM**/
	/**********************************************/
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

	public JSlider getSlider() {
		return slider;
	}

	public JPanel getPanel(int x, int y) {
		return universe[x][y];
	}

	/**********************************************/
	/******FOR CELL COLOR/STATE MANIPULATION*******/
	/**********************************************/
	public State getPanelState(int x, int y) {
		if(universe[x][y].getBackground() == deadCellColor)
			return State.DEAD;
		else
			return State.ALIVE;
	}
 
	public void setPanelState(int x, int y, State state) {
		if(state == State.DEAD)
			universe[x][y].setBackground(deadCellColor);
		else
			universe[x][y].setBackground(aliveCellColor);
	}

	public void setGenerationText(int generationNum) {
		generationLabel.setText("Generation: " +generationNum);
	}

	public int getUniverseSize() {
		return universeSize;
	}
}