/*GridAnalyzerTest: A test for the Position Analyzer
 * Supposed to analyze the grid made by floor tiles in order to track the position of the rover.
 * Author: Vikram Kashyap, 2019
 * */

package testing;

import org.opencv.core.Core;

import rover.PositionAnalyzer;
import rover.WebcamController;
import tools.DataHandler;

public class GridAnalyzerTest {
	DataHandler dh;
	WebcamController wcc;
	PositionAnalyzer pa;

	public GridAnalyzerTest() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		dh = new DataHandler();
		wcc = new WebcamController(dh);
		pa = new PositionAnalyzer(dh);
	}
	
	public static void main(String[] args) {
		new GridAnalyzerTest();
	}

}
