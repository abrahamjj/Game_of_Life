/**
*
* @author John Abraham
*/

public class Cell {

	private int x_coordinate;
	private int y_coordinate;
	private State state;

	/**
	* Cell constructor.
	* Defaults the state of a cell to DEAD upon creation.
	*/
	public Cell() {

		state = State.DEAD; 
	}

	/**
	* accesstor methods
	*/
	public int get_x() {

		return x_coordinate;
	}

	public int get_y() {

		return y_coordinate;
	}

	public State get_state() {

		return state;
	}

	/**
	* mutator methods
	*/
	public void set_x(int x_coordinate) {

		this.x_coordinate = x_coordinate;
	}

	public void set_y(int y_coordinate) {

		this.y_coordinate = y_coordinate;
	}

	public void set_state(State state) {

		this.state = state;
	}
}
