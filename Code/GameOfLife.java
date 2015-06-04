/**
* A GUI implementaiton of Conways's Game of Life in Java
*
* @author John Abraham
*/

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.lang.Thread;

public class GameOfLife {

	public GameOfLife() {

		/**
		* Set frame size, default close operation, and location.
		*/
		JFrame mainFrame = new JFrame("Conway's Game of Life Simulation");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(700, 700);
		centerWindow(mainFrame);

		/**
		* Create FlowLayout JPanel and add configuration tools to it.
		* Add the configuration tool JPanel to the south border of the main frame.
		* Add a JLabel to the north border to keep track of iterations.
		*/
		JPanel southBorderPanel = new JPanel(new FlowLayout());
		JPanel northBorderPanel = new JPanel(new FlowLayout());
		JLabel simSpeedLabel = new JLabel("Set Simulation Speed: ");
		simSpeedLabel.setFont(new Font("Serif", Font.BOLD, 17));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setLabelTable(slider.createStandardLabels(10));
		JButton startButton = new JButton("Start Simulation");
		startButton.setFont(new Font("Serif", Font.BOLD, 17));
		JLabel iterationLabel = new JLabel("Iteration: ");
		iterationLabel.setFont(new Font("Serif", Font.BOLD, 17));
		JLabel iterationNumLabel = new JLabel("0");
		iterationNumLabel.setFont(new Font("Serif", Font.BOLD, 17));
		southBorderPanel.add(simSpeedLabel);
		southBorderPanel.add(slider);
		southBorderPanel.add(startButton);
		northBorderPanel.add(iterationLabel);
		northBorderPanel.add(iterationNumLabel);
		mainFrame.add(northBorderPanel, BorderLayout.NORTH);
		mainFrame.add(southBorderPanel, BorderLayout.SOUTH);

		/**
		* add a grid to center panel of the main frame.
		*/
		JPanel centerPanel = new JPanel(new GridLayout(10, 10));
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

///////////////////////////////////////////////////////////////////////////////
		for(int i=0; i<100; i++) {
			JPanel cell = new JPanel();
			cell.setBackground(Color.GRAY);
			cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			centerPanel.add(cell);
		}
		mainFrame.add(centerPanel, BorderLayout.CENTER);
//////////////////////////////////////////////////////////////////////////////

		/**
		* Show the main frame and everything that was added to it.
		*/
		mainFrame.setVisible(true);

		/**
		* Set up world and cells here.
		* Threading happens next
		*/
	}

	public static void main(String[] args) throws Exception{

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new GameOfLife();
	}

	private void centerWindow(JFrame f) {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - f.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - f.getHeight()) / 2);
		f.setLocation(x, y);
	}
}
