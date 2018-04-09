/*DataSource: Provides data of some type to the DataHandler
 * May be phased out in favor of anything being able to send any data to the handler
 * without being registered*/

package tools;

public interface DataSource {
	
	int[] getOfferedDataTypes();

}