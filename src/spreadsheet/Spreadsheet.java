package spreadsheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

public class Spreadsheet implements SpreadsheetInterface {
	
	private static Set<Cell> invalidCells = new HashSet<Cell>();
	private static Map<CellLocation, Cell> cells = new HashMap<CellLocation, Cell>();

	@Override
	public void setExpression(CellLocation location, String expression) {
		if (cells.get(location) == null) {
			cells.put(location, new Cell(this, location, expression, new StringValue(expression)));
		} else {
			cells.get(location).updateExpression(expression);
			cells.get(location).updateValue(new StringValue(expression));
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
		Iterator<Cell> itr = invalidCells.iterator();
		while (itr.hasNext()) {
			Cell cell = itr.next(); 
			CellLocation loc = cell.getLocation();
			String exp = getExpression(loc);
			setExpression(loc, exp);
			invalidCells.remove(cell);
		}	
	}
	
	public static void addInvalidCell(Cell cell) {
		invalidCells.add(cell);
	}
	
	public static Cell getCell(CellLocation location) {
		return cells.get(location);
	}
}
