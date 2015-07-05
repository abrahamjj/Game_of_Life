/**
* My attemp at implementing a Conways's Game of Life simulator in Java
* GameOfLife: main program
*
* @author John Abraham
*/

public class GameOfLife {

	public static void main(String[] args) {

		View view = new View();
		Controller controller = new Controller(view);
		controller.control();
	}
}