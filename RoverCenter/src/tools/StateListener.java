/*StateListener: Has a simple state reader function that is updated by the object it is attached to*/

package tools;

public interface StateListener {
	
	//Current state, pushed as true or false
	public void updateState(boolean state);
}
