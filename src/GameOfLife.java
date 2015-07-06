/**
* GameOfLife: main program
*
* @author John Abraham
*/

import javax.swing.SwingUtilities;

public class GameOfLife {

	public static void main(String[] args) {

		final View view = new View();
		Controller controller = new Controller(view);
		controller.control();
	}
}