import java.util.ArrayList;
import java.util.List;

class Heuristic implements SolverInterface {
	private int[][] grid;
	static int N = 9;

	public Heuristic(int[][] puzzle) {
		this.grid = puzzle;
	}

	/**
	 * Class to abstract the representation of a cell. Cell => (x, y)
	 */
	static class Cell {
		int row, col;
		List<Integer> possibleAssignments = new ArrayList<Integer>();

		public Cell(int row, int col) {
			super();
			this.row = row;
			this.col = col;
			
			//default possible options for a Cell
			for(int i = 0; i < N; i++)
			{
				possibleAssignments.add(i);
			}
		}

		@Override
		public String toString() {
			return "Cell [row=" + row + ", col=" + col + "]";
		}
	};

	/**
	 * Utility function to check whether @param value is valid for @param cell
	 */
	boolean isValid(Cell cell, int value) {
		if (grid[cell.row][cell.col] != 0) {
			throw new RuntimeException("Cannot call for cell which already has a value");
		}
		// if v present row, return false
		for (int c = 0; c < N; c++) {
			if (grid[cell.row][c] == value)
				return false;
		}
		// if v present in col, return false
		for (int r = 0; r < N; r++) {
			if (grid[r][cell.col] == value)
				return false;
		}
		// if v present in grid, return false
		// to get the grid we should calculate (x1,y1) (x2,y2)
		int x1 = 3 * (cell.row / 3);
		int y1 = 3 * (cell.col / 3);
		int x2 = x1 + 2;
		int y2 = y1 + 2;
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				if (grid[x][y] == value)
					return false;
		// if value not present in row, col and bounding box, return true
		return true;
	}

	// simple function to get the next cell
	// read for yourself, very simple and straight forward
	static Cell getNextCell(Cell cur) {
		int row = cur.row;
		int col = cur.col;
		// next cell => col++
		col++;
		// if col > 8, then col = 0, row++
		// reached end of row, got to next row
		if (col > 8) {
			// goto next line
			col = 0;
			row++;
		}
		// reached end of matrix, return null
		if (row > 8)
			return null; // reached end
		Cell next = new Cell(row, col);
		return next;
	}

	// Return Next cell based on MRV (or MCV) - helps in vertex/variable ordering
	// chooses one among the unassigned cells based on MCV
	Cell getNextMCVCell() {
		// System.out.println("Trying to MCV - head**********");
		// max possible +1
		int minSoFar = N+1;
		Cell mcvCell = null;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (grid[r][c] == 0) {
					// System.out.println("Trying to getNextMCV");
					Cell cell = new Cell(r, c);
					int temp = getMRVAssignment(cell);
					if (temp < minSoFar) {
						minSoFar = temp;
						mcvCell = cell;
					}
				}
			}
		}

		return mcvCell;
	}
	
	/*
	 * Find the value(among possible values) which will rule out less possibilities for neighbors.
	 * @param currCell - is the cell we want to do an assignment
	 * currCell.possibleAssignments - gives and array of possible values that we can assign for this cell 
	 */
	int getLeastConstrainedValue(Cell currCell)
	{
		//int[] possibleAssignments = new int[9];
		
		int maxOfLeast = -1;
		int maxOfLeastAssignment = -1;
		
		for(int i=0; i < currCell.possibleAssignments.size(); i++)
		{
			grid[currCell.row][currCell.col] = currCell.possibleAssignments.get(i);
			int temp = getMostConstrainedNeigbor(currCell);
			if(temp > maxOfLeast)
			{
				maxOfLeast = temp;
				//the assignment that made it possible
				maxOfLeastAssignment = currCell.possibleAssignments.get(i);
			}
		}
		grid[currCell.row][currCell.col] = 0;
		//once we detect that this is the assignment that we are going to use, remove it from the possible assignments so that we won't use the
		//same when we backtrack
		currCell.possibleAssignments.remove(new Integer(maxOfLeastAssignment));
		return maxOfLeastAssignment;
	}
	
	int getMostConstrainedNeigbor(Cell currCell)
	{
		int least = N+1;
		
		//column neighbors
		for (int c = 0; c < N; c++) {
			if (grid[currCell.row][c] == 0)
			{
				int temp = getMRVAssignment(new Cell(currCell.row, c));
				if(temp < least)
				{
					least = temp;
				}
			}
		}
		
		//row neighbors
		for (int r = 0; r < N; r++) {
			if (grid[r][currCell.col] == 0)
			{
				int temp = getMRVAssignment(new Cell(r, currCell.col));
				if(temp < least)
				{
					least = temp;
				}
			}
		}
		
		//grid neighbors
		int x1 = 3 * (currCell.row / 3);
		int y1 = 3 * (currCell.col / 3);
		int x2 = x1 + 2;
		int y2 = y1 + 2;
		for (int x = x1; x <= x2; x++)
		{
			for (int y = y1; y <= y2; y++) {
				if (grid[x][y] == 0)
				{
					int temp = getMRVAssignment(new Cell(x,y));
					if(temp < least)
					{
						least = temp;
					}
				}
			}
		}
		
		return least;
	}
	
	/**
	 * Utility function to get the possible assignments for @param cell
	 */
		int getMRVAssignment(Cell cell) {

		// each index is possible value in domain
		// at the end a[i-1] = 1 represents i has been taken and can't be used for this
		// cell, a[i-1] = 0 otherwise
		// i ranges from 1 <= i <=N for the sudoku puzzle we are dealing with, so 0 to 8
		// in terms of array (hence i-1 above)
		int possibleValues[] = new int[N];
		//List<Integer> possibleValues = new ArrayList<Integer>();

		if (grid[cell.row][cell.col] != 0) {
			throw new RuntimeException("Cannot call for cell which already has a value");
		}
		// checking other value assignments in the row
		for (int c = 0; c < N; c++) {
			if (grid[cell.row][c] != 0)
				possibleValues[(grid[cell.row][c]) - 1] = 1; // indicating this value is taken
				//possibleValues.add((grid[cell.row][c]));
		}
		// checking other value assignments in the column
		for (int r = 0; r < N; r++) {
			if (grid[r][cell.col] != 0)
				possibleValues[(grid[r][cell.col]) - 1] = 1; // indicating this value is taken
				//possibleValues.add(grid[r][cell.col]);
		}

		// check other value assignments in the grid
		// to get the grid we should calculate (x1,y1) (x2,y2)
		int x1 = 3 * (cell.row / 3);
		int y1 = 3 * (cell.col / 3);
		int x2 = x1 + 2;
		int y2 = y1 + 2;
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				if (grid[x][y] != 0)
					possibleValues[(grid[x][y]) - 1] = 1; // indicating this value is taken
					//possibleValues.add(grid[x][y]);

		// Run through the array and get the values which are unassigned
		//int noPossibleValues = N;
		List<Integer> possList = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			//noPossibleValues = noPossibleValues - possibleValues[i];
			if(possibleValues[i] == 0)
			{
				possList.add(i+1);
			}
		}

		// System.out.println("MRV for cell: "+cell.toString()+" is:
		// "+noPossibleValues);
		cell.possibleAssignments = possList;
		return possList.size();
	}

	// everything is put together here
	// very simple solution
	// must return true, if the soduku is solved, return false otherwise
	boolean solve(Cell cur) {
		// if the cell is null, we have reached the end
		if (cur == null)
			return true;
		// if grid[cur] already has a value, there is nothing to solve here,
		// continue on to next cell
		if (grid[cur.row][cur.col] != 0) {
			// return whatever is being returned by solve(next)
			// i.e the state of soduku's solution is not being determined by
			// this cell, but by other cells
			// return solve(getNextCell(cur));
			return solve(getNextMCVCell());
		}
		// this is where each possible value is being assigned to the cell, and
		// checked if a solutions could be arrived at.

		// if grid[cur] doesn't have a value
		// try each possible value
		//for (int i = 1; i <= N; i++) {
		while (cur.possibleAssignments.size() > 0) {
		//	System.out.println("Trying to assign Cell: "+cur+"Possible Assignments Size: "+cur.possibleAssignments.size());
			// check if valid, if valid, then update
			int i = getLeastConstrainedValue(cur);
			boolean valid = isValid(cur, i);
			if (!valid) // i not valid for this cell, try other values
				continue;
			// assign here
			grid[cur.row][cur.col] = i;
			// continue with next cell
			// boolean solved = solve(getNextCell(cur));
			boolean solved = solve(getNextMCVCell());
			// if solved, return, else try other values
			if (solved)
				return true;
			else
				grid[cur.row][cur.col] = 0; // reset
			// continue with other possible values
		}
		// if you reach here, then no value from 1 - N for this cell can solve
		// return false
		return false;
	}

	@Override
	public void solve() {
		//ReadInput read = new ReadInput();
		//grid = read.readInput("sudoku-hard.txt");
	//	long start = System.nanoTime();
		// boolean solved = true;
		// System.out.println("Choosing: "+getNextMCVCell().toString());
		boolean solved = solve(getNextMCVCell());
	//	long end = System.nanoTime();
		if (!solved) {
			System.out.println("SUDOKU cannot be solved.");
			return;
		}
	//	System.out.println("SOLUTION Found: Time taken: " + (end - start) / 1000 + " microseconds.\n");
		printGrid(grid);
	}
	

	// utility to print the grid
	void printGrid(int grid[][]) {
		for (int row = 0; row < N; row++) {
			for (int col = 0; col < N; col++)
				System.out.print(grid[row][col]);
			System.out.println();
		}
	}

}
