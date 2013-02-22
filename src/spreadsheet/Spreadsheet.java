package spreadsheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

public class Spreadsheet implements SpreadsheetInterface {
	
	private Set<Cell> invalidCells = new HashSet<Cell>();
	private Map<CellLocation, Cell> cells = new HashMap<CellLocation, Cell>();

	@Override
	public void setExpression(CellLocation location, String expression) {
		if (cells.get(location).getExpression(location) == null) {
			cells.put(location, new Cell(this, location, expression, new StringValue("")));
		} else {
			cells.get(location).updateExpression(expression);
			cells.get(location).updateValue(new StringValue(""));
		}
	}

	@Override
	public String getExpression(CellLocation location) {
		String expression = cells.get(location).getExpression(location);
		return (expression == null ? "" : expression);
	}

	@Override
	public Value getValue(CellLocation location) {
		Value value = cells.get(location).getValue(location);
		return (value == null ? new StringValue("") : value);
	}

	@Override
	public void recompute() {
		
	}
}
