/**
* Controller: alters the state of the view.
* All event handling happens here.
*
* @author John Abraham
*/

import java.lang.Thread;
import javax.swing.JSlider;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;

public class Controller {

	private Thread thread;
	private View view;
	private int universeSize;
	private int generationNum;

	public Controller(View view) {
		this.view = view;
		universeSize = view.getUniverseSize();
		generationNum = 0;
	}

	/********************************************************************/
	/*********ADD AN ACTIONLISTENER TO EVERY BUTTON IN THE VIEW**********/
	/********************************************************************/
	public void control() {
		view.getStepButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {                  
				updateWorld();
				view.setGenerationText( ++generationNum );
			}
		});

		view.getRunButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				/**Disable buttons so multiple threads won't be created**/
				view.getRunButton().setEnabled(false);
				view.getClearButton().setEnabled(false);
				view.getStepButton().setEnabled(false);

				/**PUT THIS LOOP IN SEPARATE THREAD.
				* UI events in swing are handled by a single thread, and we
				* don't want to cause that thread to get stuck in a loop.
				*/
				thread = new Thread(new Runnable() {
					public void run() {
						while(true) {
							updateWorld();
							view.setGenerationText( ++generationNum );
							/**pause this seperate thread for a simulation delay**/
							try {
								Thread.sleep(view.getSlider().getValue());
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
				for (int y=0; y<universeSize; y++) {
					for (int x=0; x<universeSize; x++) {
						view.setPanelState(x, y, State.DEAD);
					}
				}
				generationNum = 0;
				view.setGenerationText( 0 );
			}
		});

		view.getStopButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				view.getRunButton().setEnabled(true);
				view.getClearButton().setEnabled(true);
				view.getStepButton().setEnabled(true);
				thread.stop();
			}
		});

		/**ADD AN ACTIONLISTENER TO EVERY JPANEL TO DETECT STATE CHANGES BY THE USER**/
		for (int y=0; y<universeSize; y++) {
			for (int x=0; x<universeSize; x++) {
				final int xx = x;
				final int yy = y;
				view.getPanel(xx, yy).addMouseListener(new MouseAdapter(){
					public void mouseEntered(MouseEvent me) {
						if(me.getModifiers() == MouseEvent.BUTTON1_MASK) {
							view.setPanelState(xx, yy, State.ALIVE);
 						}
					}

					public void mousePressed(MouseEvent me) {
						if(view.getPanelState(xx, yy) == State.DEAD) {
							view.setPanelState(xx, yy, State.ALIVE);
						} else {
							view.setPanelState(xx, yy, State.DEAD);
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
	* Start the simulation here. This is a single-step iteration.
	* Iterate through the universe and change the states of the
	* cells in the JPanel grid based on the 4 simple rules of Conway's
	* Game of Life.
	*/
	private void updateWorld() {
		State[][] new_universe = new State[universeSize][universeSize];

		for (int y=0; y<universeSize; y++) {
			for (int x=0; x<universeSize; x++) {
				new_universe[x][y] = State.DEAD;
			}
		}

		for (int y=0; y<universeSize; y++) {
			for (int x=0; x<universeSize; x++) {
				if ( view.getPanelState(x, y) == State.DEAD ) {
					/**1. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.**/
					if (getNumLiveNeighbors(x, y) == 3) {
						new_universe[x][y]  = State.ALIVE;
					}
				}

				if ( view.getPanelState(x, y) == State.ALIVE ) {
					/**2. Any live cell with fewer than two live neighbours dies, as if caused by under-population.**/
					if (getNumLiveNeighbors(x, y) < 2) {
						new_universe[x][y]  = State.DEAD;
					}

					/**3. Any live cell with two or three live neighbours lives on to the next generation.**/
					if (getNumLiveNeighbors(x, y) == 2 || getNumLiveNeighbors(x, y) == 3) {
						new_universe[x][y]  = State.ALIVE;
					}

					/**4. Any live cell with more than three live neighbours dies, as if by overcrowding.**/
					if (getNumLiveNeighbors(x, y) > 3) {
						new_universe[x][y]  = State.DEAD;
					}
				}
			}
		}

		for (int y=0; y<universeSize; y++) {
			for (int x=0; x<universeSize; x++) {
				if (new_universe[x][y] == State.DEAD) {
					view.setPanelState(x, y, State.DEAD);
				} else {
					view.setPanelState(x, y, State.ALIVE);
				}
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
			if ( x+r_delta[r] != -1 && x+r_delta[r] != universeSize && y+c_delta[c] != -1 && y+c_delta[c] != universeSize) {
				if ( view.getPanelState(x+r_delta[r], y+c_delta[c]) == State.ALIVE ) {
					numLiveNeighbors++;
				}
			}
		}
		return numLiveNeighbors;
	}
}