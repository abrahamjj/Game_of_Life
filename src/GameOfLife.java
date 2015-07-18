/**
* GameOfLife: main program
*
* @author John Abraham
*/

public class GameOfLife {

	public static void main(String[] args) {

		final View view = new View();
		Controller controller = new Controller(view);
		controller.control();
	}
}