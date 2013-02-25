package spreadsheet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.*;

public class Cell implements Observer<Cell>{
	
	private Spreadsheet spreadsheet;
	private CellLocation cellLocation;
	private String expression;
	private Value value;
	
	private Set<Observer<Cell>> observers = new HashSet<Observer<Cell>>();
	private Set<CellLocation> references = new HashSet<CellLocation>();
	
	Cell(Spreadsheet spreadsheet, CellLocation cellLocation,
			String expression, Value value) {
		this.spreadsheet = spreadsheet;
		this.cellLocation = cellLocation;
		this.expression = expression;
		this.value = value;
	}
	
	public CellLocation getLocation() {
		return cellLocation;
	}

	public String getExpression(CellLocation location) {
		return expression;
	}
	
	public Value getValue(CellLocation location) {
		return value;
	}

	public void updateExpression(String expression) {
		Iterator<CellLocation> ref = references.iterator();
		while (ref.hasNext()) {
			Cell cell = Spreadsheet.getCell(ref.next());
			removeObserver(cell);
		}
		references.clear();
		
		this.expression = expression;
		updateValue(new InvalidValue(expression));
		Spreadsheet.addInvalidCell(this);
		
		references = ExpressionUtils.getReferencedLocations(expression);
		Iterator<CellLocation> newRef = references.iterator();
		while (newRef.hasNext()) {
			Cell cell = Spreadsheet.getCell(ref.next());
			addObserver(cell);
		}
		
		Iterator<Observer<Cell>> obs = observers.iterator();
		while (obs.hasNext()) {
			obs.next().update(this);
		}
		
	}
	
	public void updateValue(Value value) {
		this.value = value;
	}

	@Override
	public void update(Cell changed) {
		Spreadsheet.addInvalidCell(changed);
		changed.updateValue(new InvalidValue(expression));
	
		Iterator<Observer<Cell>> obs = observers.iterator();
		while (obs.hasNext()) {
			obs.next().update(this);
		}
		
	}
	
	private void removeObserver(Observer<Cell> observer) {
		observers.remove(observer);
	}
	
	private void addObserver(Observer<Cell> observer) {
		observers.add(observer);
	}
}
