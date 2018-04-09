/*DataReciever: Registered to DataHandler, gets distributed requested data*/

package tools;

import java.util.Map;

public interface DataReciever {
	
	public int[] getDataTypes();
	
	public void recieveData(int type, Object data);

}
