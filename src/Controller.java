/**
* Controller: alters the state of the view.
* All calculations and event handling happens here.
* 
* @author John Abraham
*/

import java.lang.Math;
import java.lang.Thread;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;

public class Controller {

	private View view;
	private Thread thread;
	private int generationNum;
	private int univerRowSize;
	private int univerColumnSize;
	private boolean isThreadRunning;

	public Controller(View view) {
		this.view = view;
		univerRowSize = view.getUniverseSizeRows();
		univerColumnSize = view.getUniverseSizeColumns();
		generationNum = 0;
	}

	/********************************************************************/
	/*********ADD AN ACTIONLISTENER TO EVERY BUTTON IN THE VIEW**********/
	/********************************************************************/
	public void control() {

		/**ADD AN ACTIONLISTENER TO EVERY JPANEL TO DETECT STATE CHANGES BY THE USER**/
		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				final int xx = x;
				final int yy = y;
				view.getPanel(xx, yy).addMouseListener(new MouseAdapter(){
					public void mouseEntered(MouseEvent me) {
						if(me.getModifiers() == MouseEvent.BUTTON1_MASK) {
							if(view.getPanelState(xx, yy) == State.DEAD) {
								view.setPanelState(xx, yy, State.ALIVE);
								System.out.println(xx +","+yy);
							}
						}
					}

					public void mousePressed(MouseEvent me) {
						if(view.getPanelState(xx, yy) == State.DEAD) {
							view.setPanelState(xx, yy, State.ALIVE);
							System.out.println(xx +","+yy);
						} else {
							view.setPanelState(xx, yy, State.DEAD);
						}
					}
				});
			}
		}

		view.getStepButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {                  
				updateUniverse();
				view.setGenerationText( ++generationNum );
			}
		});

		view.getRunButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				/**Disable buttons so multiple threads won't be created**/
				view.getRunButton().setEnabled(false);
				view.getClearButton().setEnabled(false);
				view.getStepButton().setEnabled(false);
				isThreadRunning = true;

				/**
				* PUT THIS LOOP IN SEPARATE THREAD.
				* UI events in swing are handled by a single thread, and we
				* don't want to cause that thread to get stuck in a loop.
				*/
				thread = new Thread(new Runnable() {
					public void run() {
						while(isThreadRunning) {
							updateUniverse();
							view.setGenerationText( ++generationNum );
							/**pause this seperate thread for the simulation delay**/
							/*******************************************************
							* From the JSlider we're getting 1...20 Integers
							* Thread.sleep() takes in long 1000 for 1 Hz updates because 1000ms = 1s
							* To convert 1...20 Integers to 1...20 Hz we use f(x) = 1000*(1/x)
							* (x representing the jSlider integer values)
							**/
							int x = view.getSliderValue();
							try {
								Thread.sleep( (long)(1000/x) );
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				thread.start();
			}
		});

		view.getClearButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				clearUniverse();
				generationNum = 0;
				view.setGenerationText( 0 );
				view.getAutoFillComboBox().setSelectedIndex(0);
				view.getPreconfigurationComboBox().setSelectedIndex(0);
			}
		});

		view.getStopButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				isThreadRunning = false;
				view.getRunButton().setEnabled(true);
				view.getClearButton().setEnabled(true);
				view.getStepButton().setEnabled(true);
				view.getAutoFillComboBox().setSelectedIndex(0);
				view.getPreconfigurationComboBox().setSelectedIndex(0);
			}
		});

		view.getColorComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				/**Only change color of alive cells if one of the color options is selected and not the Color label**/
				String selectedColor = view.getColorComboBox().getSelectedItem().toString();
				if (view.getColorComboBox().getSelectedIndex() > 0) {
					if (selectedColor.equals("Black")) {
						view.setPanelColor(new Color(0,0,0));
					} else if (selectedColor.equals("White")) {
						view.setPanelColor(new Color(255,255,255));
					} else if (selectedColor.equals("Red")) {
						view.setPanelColor(new Color(200,0,0));
					} else if (selectedColor.equals("Green")) {
						view.setPanelColor(new Color(0,200,0));
					} else if (selectedColor.equals("Blue")) {
						view.setPanelColor(new Color(0,0,200));
					} else if (selectedColor.equals("Orange")) {
						view.setPanelColor(new Color(255,100,0));
					} else if (selectedColor.equals("Yellow")) {
						view.setPanelColor(new Color(255,255,0));
					} else if (selectedColor.equals("Indigo")) {
						view.setPanelColor(new Color(75,0,130	));
					} else if (selectedColor.equals("Violet")) {
						view.setPanelColor(new Color(238,130,238));
					} else if (selectedColor.equals("Purple")) {
						view.setPanelColor(new Color(160,32,240));
					}
				}
			}
		});

		view.getAutoFillComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				/**Only autofill if one of the percentage options is selected and not the AutoFill label**/
				if (view.getAutoFillComboBox().getSelectedIndex() > 0) {
					/**First reset all cells to the Dead state**/
					clearUniverse();
					/**Second autofill the universe with Dead/Alive states based on the selected percentage**/
					String selectedPercentageString = view.getAutoFillComboBox().getSelectedItem().toString();
					int selectedPercentageInteger = 
					Integer.parseInt( selectedPercentageString.substring(0, selectedPercentageString.length()-1) );
					randomlyFillUniverse( selectedPercentageInteger );
				}
			}

		});

		view.getPreconfigurationComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				/**Only set preconfiguration if one of the preconfiguration options is selected**/
				if (view.getPreconfigurationComboBox().getSelectedIndex() > 0) {
					/**Read coordinates from CSV file**/
					String selectedPreconfiguration = view.getPreconfigurationComboBox().getSelectedItem().toString();
					if(selectedPreconfiguration.equals("Horizontal Line")) { loadConfigurationFile("config/HorizontalLine.csv"); }
					else if(selectedPreconfiguration.equals("Vertical Line")) { loadConfigurationFile("config/VerticalLine.csv"); }
					else if(selectedPreconfiguration.equals("Glider")) { loadConfigurationFile("config/Glider.csv"); }
					else if(selectedPreconfiguration.equals("Gosper Glider Gun")) { loadConfigurationFile("config/GosperGliderGun.csv"); }
					else if(selectedPreconfiguration.equals("Lightweight Spaceship")) { loadConfigurationFile("config/LightweightSpaceship.csv"); }
					else if(selectedPreconfiguration.equals("Weekender")) { loadConfigurationFile("config/Weekender.csv"); }
					else if(selectedPreconfiguration.equals("25P3H1V0.2")) { loadConfigurationFile("config/25P3H1V0.2.csv"); }
					else if(selectedPreconfiguration.equals("44P5H2V0")) { loadConfigurationFile("config/44P5H2V0.csv"); }
					else if(selectedPreconfiguration.equals("30P5H2V0")) { loadConfigurationFile("config/30P5H2V0.csv"); }
					else if(selectedPreconfiguration.equals("Queen Bee Shuttle")) { loadConfigurationFile("config/QueenBeeShuttle.csv"); }
					else if(selectedPreconfiguration.equals("Tumbler")) { loadConfigurationFile("config/Tumbler.csv"); }
					else if(selectedPreconfiguration.equals("Pulsar")) { loadConfigurationFile("config/Pulsar.csv"); }
					else if(selectedPreconfiguration.equals("Pentadecathlon")) { loadConfigurationFile("config/Pentadecathlon.csv"); }
				}
			}
		});
	}

	/********************************************************************/
	/***********************PRIVATE HELPER METHODS***********************/
	/********************************************************************/
	/**
	* Start the simulation here. This is a single-step iteration.
	* Iterate through the universe and change the states of the
	* cells in the JPanel matrix based on the 4 simple rules of Conway's
	* Game of Life.
	*/
	private void updateUniverse() {
		State[][] new_temp_universe = new State[univerRowSize][univerColumnSize];

		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				new_temp_universe[x][y] = State.DEAD;
			}
		}

		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				if ( view.getPanelState(x, y) == State.DEAD ) {
					/**1. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.**/
					if (getNumLiveNeighbors(x, y) == 3) {
						new_temp_universe[x][y]  = State.ALIVE;
					}
				}
				if ( view.getPanelState(x, y) == State.ALIVE ) {
					/**2. Any live cell with fewer than two live neighbours dies, as if caused by under-population.**/
					if (getNumLiveNeighbors(x, y) < 2) {
						new_temp_universe[x][y]  = State.DEAD;
					}
					/**3. Any live cell with two or three live neighbours lives on to the next generation.**/
					if (getNumLiveNeighbors(x, y) == 2 || getNumLiveNeighbors(x, y) == 3) {
						new_temp_universe[x][y]  = State.ALIVE;
					}
					/**4. Any live cell with more than three live neighbours dies, as if by overcrowding.**/
					if (getNumLiveNeighbors(x, y) > 3) {
						new_temp_universe[x][y]  = State.DEAD;
					}
				}
			}
		}

		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				if (new_temp_universe[x][y] == State.DEAD) {
					view.setPanelState(x, y, State.DEAD);
				} else {
					view.setPanelState(x, y, State.ALIVE);
				}
			}
		}
	}

	/**
	* Helper method to get the number of live neighbors for a given cell.
	*/
	private int getNumLiveNeighbors(int x, int y) {
		int[] r_delta = {-1,-1,-1, 0, 0, 1, 1, 1};
		int[] c_delta = {-1, 0, 1,-1, 1,-1, 0, 1};
		int numLiveNeighbors = 0;
		for (int r=0, c=0; r<8; r++, c++) {
			if ( x+r_delta[r] != -1 && x+r_delta[r] != univerRowSize && y+c_delta[c] != -1 && y+c_delta[c] != univerColumnSize) {
				if ( view.getPanelState(x+r_delta[r], y+c_delta[c]) == State.ALIVE ) {
					numLiveNeighbors++;
				}
			}
		}
		return numLiveNeighbors;
	}

	/**
	* Helper method that randomly fills the univese based on a selected percentage vlue
	*/
	private void randomlyFillUniverse(int percentage) {
		/**
		* After clearing the entire universe each cell is matched with a Math.random() value.
		* Math.random() gives a double value between 0 and 1. We multiply that value by 100.
		* If this new value is less than the percentage value we make the cell corresponding
		* to it alive. This gives a reasonably close Dead:Alive cell ratio.
		*/
		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				if (Math.random()*100 < percentage) {
					view.setPanelState(x, y, State.ALIVE);
				}
			}
		}
	}

	/**
	* Helper method that resets the state of all sells to Dead
	*/
	private void clearUniverse() {
		for (int x=0; x<univerRowSize; x++) {
			for (int y=0; y<univerColumnSize; y++) {
				view.setPanelState(x, y, State.DEAD);
			}
		}
	}

	/**
	* Helper method that reads in a csv file that contains coordiantes
	* and sets the states of the corrisponding cells to alive.
	*/
	private void loadConfigurationFile(String fileName) {
		BufferedReader br = null;
		String line = "";
		try {
			/**
			* Reading files using getResourceAsStream() allows us to access those configuration
			* files in the jar file without extracting it.
			*/
			Class cls = Class.forName("Controller");
			ClassLoader cLoader = cls.getClassLoader();
			InputStream is = cLoader.getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] coordinates = line.split(",");
				view.setPanelState(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), State.ALIVE);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}