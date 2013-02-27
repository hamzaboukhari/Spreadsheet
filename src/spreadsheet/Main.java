package spreadsheet;

import spreadsheet.gui.SpreadsheetGUI;

public class Main {

    private static final int DEFAULT_NUM_ROWS = 5000;
    private static final int DEFAULT_NUM_COLUMNS = 5000;
    private static int rows;
    private static int columns;
    private static SpreadsheetGUI spreadsheet; 
    
    
    public static void main(String[] args) {
    	if(args.length == 2) {
    		rows = Integer.parseInt(args[0]);
    		columns = Integer.parseInt(args[1]);
    	} else {
    		rows = DEFAULT_NUM_ROWS;
    		columns = DEFAULT_NUM_COLUMNS;
    	}
    	
    	spreadsheet = new SpreadsheetGUI(new Spreadsheet(), rows, columns);
    	spreadsheet.start();
    }

    
}
