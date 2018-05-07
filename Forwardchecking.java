
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForwardChecking implements SolverInterface {

    static Map<Integer, List<Integer>> mapGlobal = new HashMap<>();

    public ForwardChecking(int[][] grid) {
        this.grid = grid;
    }
    private static int[][] grid;

    static class Cell {

        int row, col;

        public Cell(int row, int col) {
            super();
            this.row = row;
            this.col = col;

        }

        @Override
        public String toString() {
            return "Cell [row=" + row + ", col=" + col + "]";
        }
    };

    static boolean isValid(Cell cell, int value) {

        if (grid[cell.row][cell.col] != 0) {
            throw new RuntimeException(
                    "Cannot call for cell which already has a value");
        }

        for (int c = 0; c < 9; c++) {
            if (grid[cell.row][c] == value) {
                return false;
            }
        }

        for (int r = 0; r < 9; r++) {
            if (grid[r][cell.col] == value) {
                return false;
            }
        }

        int x1 = 3 * (cell.row / 3);
        int y1 = 3 * (cell.col / 3);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (grid[x][y] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    static Cell getNextCell(Cell cur) {

        int row = cur.row;
        int col = cur.col;

        col++;

        if (col > 8) {

            col = 0;
            row++;
        }

        if (row > 8) {
            return null;
        }
        Cell next = new Cell(row, col);
        return next;
    }

    static boolean solving(Cell cur) {

        if (cur == null) {
            return true;
        }

        if (grid[cur.row][cur.col] != 0) {

            return solving(getNextCell(cur));
        }

        int id = (cur.row * 9) + cur.col;

        List<Integer> tmp = mapGlobal.get(id);
        for (int i = 0; i < tmp.size(); i++) {
            boolean flag = assignandUpdate(cur, tmp.get(i));
            if (!flag) {
                updateReference(cur);
                continue;
            } else {
                boolean solved = solving(getNextCell(cur));
                if (solved) {
                    return true;
                } else {
                    updateReference(cur);
                }
            }

        }

        return false;
    }

    static void updateReference(Cell cell) {
        grid[cell.row][cell.col] = 0;
        Map<Integer, List<Integer>> mapReinsert = new HashMap<>();
        for (int c = 0; c < 9; c++) {
            int id1 = (cell.row * 9) + c;
            if (grid[cell.row][c] == 0) {
                //int id1=(cell.row*9)+c;
                List<Integer> listVal = new ArrayList<>();
                for (int k = 1; k < 10; k++) {
                    if (isValid(new Cell(cell.row, c), k)) {
                        listVal.add(k);
                    }
                }
                mapReinsert.put(id1, listVal);
            }
        }

        for (int r = 0; r < 9; r++) {
            int id2 = (r * 9) + cell.col;
            if (grid[r][cell.col] == 0) {

                List<Integer> listVal = new ArrayList<>();
                for (int k = 1; k < 10; k++) {
                    if (isValid(new Cell(r, cell.col), k)) {
                        listVal.add(k);
                    }
                }
                mapReinsert.put(id2, listVal);
            }
        }

        int x1 = 3 * (cell.row / 3);
        int y1 = 3 * (cell.col / 3);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                int id3 = (x * 9) + y;
                if (grid[x][y] == 0) {

                    List<Integer> listVal = new ArrayList<>();
                    for (int k = 1; k < 10; k++) {
                        if (isValid(new Cell(x, y), k)) {
                            listVal.add(k);
                        }
                    }
                    mapReinsert.put(id3, listVal);
                }
            }
        }
        for (int i : mapReinsert.keySet()) {
            mapGlobal.put(i, mapReinsert.get(i));
        }

    }

    static boolean assignandUpdate(Cell cell, int value) {
        int id = cell.row * 9 + cell.col;
        Map<Integer, List<Integer>> affectedCells = new HashMap<>();
        grid[cell.row][cell.col] = value;
        for (int c = 0; c < 9; c++) {
            int id1 = (cell.row * 9) + c;
            if (grid[cell.row][c] == 0) {

                List<Integer> listVal = new ArrayList<>();
                for (int k = 1; k < 10; k++) {
                    if (isValid(new Cell(cell.row, c), k)) {
                        listVal.add(k);
                    }
                }
                if (listVal.isEmpty()) {
                    return false;
                }
                affectedCells.put(id1, listVal);
            }
        }

        for (int r = 0; r < 9; r++) {
            int id2 = (r * 9) + cell.col;
            if (grid[r][cell.col] == 0) {
                //int id1=(cell.row*9)+c;
                List<Integer> listVal = new ArrayList<>();
                for (int k = 1; k < 10; k++) {
                    if (isValid(new Cell(r, cell.col), k)) {
                        listVal.add(k);
                    }
                }
                if (listVal.isEmpty()) {
                    return false;
                }
                affectedCells.put(id2, listVal);
            }
        }

        int x1 = 3 * (cell.row / 3);
        int y1 = 3 * (cell.col / 3);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                int id3 = (x * 9) + y;
                if (grid[x][y] == 0) {

                    List<Integer> listVal = new ArrayList<>();
                    for (int k = 1; k < 10; k++) {
                        if (isValid(new Cell(x, y), k)) {
                            listVal.add(k);
                        }
                    }
                    if (listVal.isEmpty()) {
                        return false;
                    }
                    affectedCells.put(id3, listVal);
                }
            }

        }

        mapGlobal.remove(new Integer(id));

        for (int i : affectedCells.keySet()) {
            mapGlobal.put(i, affectedCells.get(i));
        }

        return true;

    }

    static void constraint(int grid[][]) {

        for (int i = 0; i < 81; i++) {
            if (grid[i / 9][i % 9] == 0) {
                List<Integer> listVal = new ArrayList<>();
                for (int k = 1; k < 10; k++) {
                    if (isValid(new Cell(i / 9, i % 9), k)) {
                        listVal.add(k);
                    }
                }
                mapGlobal.put(i, listVal);
            }
        }
    }

    static void printGrid(int grid[][]) {
        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {
                System.out.print(grid[row][col]);
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    public void solve() {
        constraint(grid);
        System.out.println("In Forward Checking");
        boolean solved = solving(new Cell(0, 0));
        if (!solved) {
            System.out.println("NO SOLUTION");
            printGrid(grid);
            return;
        }
        System.out.println("SOLUTION");
        printGrid(grid);
    }
}