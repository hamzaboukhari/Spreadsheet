package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.value.Value;

public class Cell {
	
	private Spreadsheet spreadsheet;
	private CellLocation cellLocation;
	private String expression;
	private Value value;
	
	Cell(Spreadsheet spreadsheet, CellLocation cellLocation,
			String expression, Value value) {
		this.spreadsheet = spreadsheet;
		this.cellLocation = cellLocation;
		this.expression = expression;
		this.value = value;
	}

	public String getExpression(CellLocation location) {
		return expression;
	}
	
	public Value getValue(CellLocation location) {
		return value;
	}

	public void updateExpression(String expression) {
		this.expression = expression;
	}
	
	public void updateValue(Value value) {
		this.value = value;
	}
}
