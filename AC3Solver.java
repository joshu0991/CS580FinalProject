import java.util.*;
/*
 * Reduces the search space of the backtrack solver
 * by reducing the possible values for each square
 * in the sudoku puzzle there are there constraints
 * that allow this 
 * 1) No node in the same row can have the same value
 * 2) No node in the same column can have the same value
 * 3) no node in the 3X3 box can have the same value
 */

class AC3Solver implements SolverInterface 
{
    private final List< Integer > domain = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    private int counter;
    private int givenTiles;
    private Node[][] constraintGraph;
    private int[][] puzzle;
    
    public AC3Solver(int[][] puzzle)
    {
        this.puzzle = puzzle;
        constraintGraph = new Node[9][9];
        counter = 0;
        givenTiles = 0;
        // we preprocess the board to set up the constraint graph
        for (int row = 0; row < 9; ++row)
        {
            for (int column = 0; column < 9; ++column)
            {
                int value = puzzle[row][column];
                if (value != 0)
                {
                    constraintGraph[row][column] = new Node(row, column, value, true);
                    ++givenTiles;
                }
                else
                {
                    constraintGraph[row][column] = new Node(row, column, value, domain, false);
                }
            }
        }
    }

    public void solve()
    {
        // remove from the domain row column then 3 x 3 cell until nothing changes
        // then if the puzzle isn't solved pass the puzzle to backtracking
        boolean changed = true;
        while (counter != 81 && changed)
        {
            changed = false;

            // for every row check each vertex in the graph and remove inconsistencies from the graph
            for (int row = 0; row < 9; ++row)
            {
                // for every value in the row cell check to see if it has a value
                // if it does remove this value from the domain of every other 
                // cell in this row
                for (int column = 0; column < 9; ++column)
                {
                    Node n = constraintGraph[row][column];
                    
                    // if the node is zero then we don't care
                    if (n.getValue() == 0)
                    {
                        continue;
                    }

                    // otherwise the node has a value hence no other
                    // node in this row can have this value so remove it
                    if (removeFromRowDomain(n.getValue(), row))
                    {
                       changed = true;
                    } 
                }
            }
            
            // for every column check each vertex in the graph and remove inconsistencies from the graph
            for (int column = 0; column < 9; ++column)
            {
                // for every value in this column remove this value from the domain of every node
                // in this column  
                for (int row = 0; row < 9; ++row)
                {
                    Node n = constraintGraph[row][column];
                    
                    // if the node is zero then we don't care
                    if (n.getValue() == 0)
                    {
                        continue;
                    }

                    // otherwise the node has a value hence no other
                    // node in this row can have this value so remove it
                    if (removeFromColDomain(n.getValue(), column))
                    {
                       changed = true;
                    }
                }
            }
            
            // for 3x3 cell check each vertex in the graph and remove inconsistencies from the graph
            for (int row = 0; row < 9; row+=3)
            {
                // for every value in this 3x3 box remove this value from the domain of every node
                // in this 3x3 box get the nodes in the three by three box associated with this row/col
                for (int column = 0; column < 9; column+=3)
                {
                    // get the 3x3 box starting at this x,y
                    List< Node > box = getBox(row, column);
                    for (Node n : box)
                    {
                        if (n.getValue() == 0)
                        {
                            continue;
                        }

                        if (removeFromBoxDomain(n.getValue(), box))
                        {
                           changed = true;
                        } 
                    }
                }
            }
        }

        if (counter != 81 - givenTiles)
        {
            // we haven't fully solved the board 
            // so we need to run backtracking
            
            // first modify the puzzle to reduce the search domain
            for (int row = 0; row < 9; ++row)
            {
                for (int column = 0; column < 9; ++column)
                {
                    puzzle[row][column] = constraintGraph[row][column].getValue();
                }
            }
            System.out.println("Need backtracking with AC3");
            BackTracking b = new BackTracking(puzzle);
            b.solve();
        }
        else
        {
            // otherwise we have solved the puzzle
            System.out.println("Solved using AC3 only no backtracking needed");
            printConstraintGraph();
        }
    }

    List< Node > getBox(int row, int column)
    {
        List< Node > ret = new ArrayList< Node >(9);
        for (int rIter = row; rIter < row + 3; ++rIter)
        {
            for (int colIter = column; colIter < column + 3; ++colIter)
            {
                Node n = constraintGraph[rIter][colIter];
                ret.add(n);
            }
        }
        return ret;
    }

    private boolean removeFromRowDomain(int value, int row)
    {
        boolean changed = false;
        for (int column = 0; column < 9; ++column)
        {
            Node n = constraintGraph[row][column];
            
            if (n.haveValue())
            {
                continue;
            }
            
            if (n.removeFromDomain(value))
            {
                changed = true;
            }
        }
        return changed;
    }

    private boolean removeFromColDomain(int value, int column)
    {
        boolean changed = false;
        for (int row = 0; row < 9; ++row)
        {
            Node n = constraintGraph[row][column];
            
            if (n.haveValue())
            {
                continue;
            }
            
            if (n.removeFromDomain(value))
            {
                changed = true;
            }
        }
        return changed;
    }
    
    private boolean removeFromBoxDomain(int value, List< Node > box)
    {
        boolean changed = false;
        for (int iter = 0; iter < 9; ++iter)
        {
            Node n = box.get(iter);
            if (n.haveValue())
            {
                continue;
            }
            
            if (n.removeFromDomain(value))
            {
                changed = true;
            }
        }
        return changed;
    }

    /*
     * A node for the constraint graph
     */
    private class Node
    {
        // the possible values remaining for this node
        private List< Integer > domain;

        // true iff the value has been assigned.
        private boolean haveValue;

        private int value;

        // nodes are aware of where they are for easy debugging
        private int row;
        private int column;
        
        public Node(int row, int column, int value, boolean givenValue)
        {
            haveValue = givenValue;
            this.value = value;
            this.row = row;
            this.column = column;
        }

        public Node(int row, int column, int value, List< Integer > domain, boolean givenValue)
        {
            this.domain = new ArrayList<Integer>(domain);
            haveValue = givenValue;
            this.value = value;
            this.row = row;
            this.column = column;
        }
        
        public boolean removeFromDomain(int value)
        {
            // this will return true if the list contained the element
            boolean ret = domain.remove(Integer.valueOf(value));

            if (domain.size() == 1)
            {
                this.value = domain.get(0);
                haveValue = true;
                ++counter;
             //   System.out.println("Added node " + counter + " Set cell " + this.row + ", "
             //       + this.column + " to " + domain.get(0));
            }
            return ret;
        }

        public int getValue()
        {
            return value;
        }
        
        public boolean haveValue()
        {
            return haveValue;
        }
    }

    private void printConstraintGraph()
    {
        for (int row = 0; row < 9; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                System.out.print(constraintGraph[row][col].getValue() + " ");
            }
            System.out.println("");
        }
    }
}
