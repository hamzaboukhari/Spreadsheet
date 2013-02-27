package spreadsheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Deque;
import java.util.ArrayDeque;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.LoopValue;
import spreadsheet.api.ExpressionUtils;

public class Spreadsheet implements SpreadsheetInterface {
	
	private Set<Cell> invalidCells = new HashSet<Cell>();
	private Set<Cell> calculatedCells = new HashSet<Cell>();
	private Map<CellLocation, Cell> cells = new HashMap<CellLocation, Cell>();
	private Deque<Cell> cellsToCompute = new ArrayDeque<Cell>();
 	
	public Cell getCell(CellLocation location) {
		return cells.get(location);
	}
	
	public void addInvalidCell(Cell cell) {
		invalidCells.add(cell);
	}

	@Override
	public void setExpression(CellLocation location, String expression) {
		if (cells.get(location) == null) {
			cells.put(location, new Cell(this, location, expression, new StringValue(expression)));
		} else {
			cells.get(location).updateExpression(expression);
			cells.get(location).updateValue(new StringValue(expression));
		}
		recompute();
	}

	@Override
	public String getExpression(CellLocation location) {
		String expression = cells.get(location).getExpression();
		return (expression == null ? "" : expression);
	}

	@Override
	public Value getValue(CellLocation location) {
		Value value = cells.get(location).getValue();
		return (value == null ? new StringValue("") : value);
	}

	@Override
	public void recompute() {
//		for (Cell inv : invalidCells) {
//			CellLocation loc = inv.getLocation();
//			setExpression(loc, getExpression(loc));
//		}
//		invalidCells.clear();
		while(!invalidCells.isEmpty()) {
			recomputeCell(invalidCells.iterator().next());
		}
	}
	
	private void recomputeCell(Cell c) {
		checkLoops(c, new LinkedHashSet<Cell>());
		
		if (!c.getValue().equals(LoopValue.INSTANCE)) {
			cellsToCompute.add(c);
			while(!cellsToCompute.isEmpty()) {
				Cell current = cellsToCompute.poll();
				boolean canCalculate = true;
				for (Cell cell : current.getReferences()) {
					if (!calculatedCells.contains(cell)) {
						cellsToCompute.addFirst(cell);
						canCalculate = canCalculate && false;
					}
				}	
				if(canCalculate) {
					cellsToCompute.remove();
					calculatedCells.add(current);
					calculateCellValue(current);
				}
			}
		}
		invalidCells.remove(c);
	}
	
	
	private void calculateCellValue(Cell cell) {
		HashMap<CellLocation, Double> cellDependents = new HashMap<CellLocation, Double>();
		while(!cell.getReferences().isEmpty()) {
			for (Cell c : cell.getReferences()) {
				double val = Double.valueOf(c.getValue().toString());
				cellDependents.put(c.getLocation(), val);
				cell.getReferences().remove(c);
			}
		    cell.updateValue(ExpressionUtils.computeValue(cell.getExpression(), cellDependents));
		}
	}
	
	private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
		if (cellsSeen.contains(c)) {
			markAsLoop(c, cellsSeen);
			for (Cell cell : cellsSeen) {
				markAsLoop(cell, new LinkedHashSet<Cell>());
			}
		} else {
			cellsSeen.add(c);
			for (Cell cell : c.getReferences()) {
				checkLoops(cell, new LinkedHashSet<Cell>());
			}
			cellsSeen.remove(c);
		}
	}
	
	private void markAsLoop(Cell startCell, LinkedHashSet<Cell> cells) {
		startCell.updateValue(LoopValue.INSTANCE);
		for (Cell cell : cells) {
			cell.updateValue(LoopValue.INSTANCE);
			invalidCells.remove(cell);
		}
	}
	
}
