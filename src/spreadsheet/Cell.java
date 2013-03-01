package spreadsheet;

import java.util.HashSet;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.InvalidValue;

public class Cell implements Observer<Cell>{
	
	private Spreadsheet spreadsheet;
	private CellLocation cellLocation;
	private String expression;
	private Value value;
	private Set<Observer<Cell>> observers = new HashSet<Observer<Cell>>();
	private Set<Cell> references = new HashSet<Cell>();
	
	Cell(Spreadsheet spreadsheet, CellLocation cellLocation) {
		this.spreadsheet = spreadsheet;
		this.cellLocation = cellLocation;
	}
	
	public CellLocation getLocation() {
		return this.cellLocation;
	}

	public String getExpression() {
		return this.expression;
	}
	
	public Value getValue() {
		return this.value;
	}

	public void updateExpression(String expression) {
		for (Cell ref : references) {
			removeObserver(ref);
		}
		references.clear();
		
		this.expression = expression;
		this.value = new InvalidValue(expression);
		spreadsheet.addInvalidCell(this);
		
		Set<CellLocation> refLocs = ExpressionUtils.getReferencedLocations(expression);
		
		for (CellLocation loc : refLocs) {
			references.add(spreadsheet.getCell(loc));
		}
		for (Cell newRef : references) {
			addObserver(newRef);
		}
		for (Observer<Cell> obs : observers) {
			obs.update(this);
		}
	}
	
	public void updateValue(Value value) {
		this.value = value;
	}

	@Override
	public void update(Cell changed) {
		if (!spreadsheet.checkInvalidCell(this)) {
			spreadsheet.addInvalidCell(this);
			updateValue(new InvalidValue(expression));
			for (Observer<Cell> obs : observers) {
				obs.update(this);
			}
		}
	}
	
	private void removeObserver(Observer<Cell> observer) {
		observers.remove(observer);
	}
	
	private void addObserver(Observer<Cell> observer) {
		observers.add(observer);
	}
	
	public Set<Cell> getReferences() {
		 return this.references;
	}
	
}
