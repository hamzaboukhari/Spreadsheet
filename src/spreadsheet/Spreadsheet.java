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
import spreadsheet.api.value.ValueVisitor;
import spreadsheet.api.value.LoopValue;
import spreadsheet.api.ExpressionUtils;

public class Spreadsheet implements SpreadsheetInterface {
	
	private Set<Cell> invalidCells = new HashSet<Cell>();
	private Set<Cell> calculatedCells = new HashSet<Cell>();
	private Set<Cell> LoopCells = new HashSet<Cell>();
	private Map<CellLocation, Cell> cells = new HashMap<CellLocation, Cell>();
	private Deque<Cell> cellsToCompute = new ArrayDeque<Cell>();
	private HashMap<CellLocation, Double> cellDependents = new HashMap<CellLocation, Double>();
 	
	public Cell getCell(CellLocation location) {
		if (cells.get(location) == null) {
			cells.put(location, new Cell(this, location));
		}
		return cells.get(location);
	}
	
	public void addInvalidCell(Cell cell) {
		invalidCells.add(cell);
	}

	@Override
	public void setExpression(CellLocation location, String expression) {
		if (cells.get(location) == null) {
			cells.put(location, new Cell(this, location));
		}
		cells.get(location).updateExpression(expression);
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
		for (Cell inv : invalidCells) {
			recomputeCell(inv);
		}
		invalidCells.clear();
	}
	
	private void recomputeCell(Cell c) {
		checkLoops(c, new LinkedHashSet<Cell>());
		
		if (!LoopCells.contains(c)) {
			cellsToCompute.add(c);
			while(!cellsToCompute.isEmpty()) {
				Cell current = cellsToCompute.getFirst();
				boolean canCalculate = true;
				for (Cell cell : current.getReferences()) {
					if (!calculatedCells.contains(cell)) {
						cellsToCompute.addFirst(cell);
						canCalculate = canCalculate && false;
					}
				}	
				if(canCalculate) {
					cellsToCompute.remove(current);
					calculatedCells.add(current);
  				    calculateCellValue(current);
				}
			}
		}
	}
	double dval;
	private void calculateCellValue(Cell cell) {
			Value val = ExpressionUtils.computeValue(cell.getExpression(), cellDependents);
			val.visit( new ValueVisitor(){
						public void visitDouble(double value) {
							dval = value;
						}
					    
					    public void visitLoop(){}
					    
					    public void visitString(String expression){}

					    public void visitInvalid(String expression){}
			});
			cellDependents.put(cell.getLocation(), dval);
		    cell.updateValue(val);
		    
		    
	}
	
	private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
		if (cellsSeen.contains(c)) {
			markAsLoop(c, cellsSeen);
		} else {
			cellsSeen.add(c);
			for (Cell cell : c.getReferences()) {
				checkLoops(cell, cellsSeen);
			}
			cellsSeen.remove(c);
		}
	}
	
	private void markAsLoop(Cell startCell, LinkedHashSet<Cell> cells) {
		startCell.updateValue(LoopValue.INSTANCE);
		boolean passedStartCell = false;
		for (Cell cell : cells) {
			if(passedStartCell) {
				cell.updateValue(LoopValue.INSTANCE);
			} else if (cell.equals(startCell)) {
				passedStartCell = true;
			}
			LoopCells.add(cell);
		}	
	}
	
	boolean checkInvalidCell(Cell cell) {
		return invalidCells.contains(cell);
	}
	
}
