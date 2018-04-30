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
    private static final List< Integer > domain = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    public AC3Solver(int[][] puzzle)
    {
        this.puzzle = puzzle;
        constraintGraph = new Node[9][9];

        // we preprocess the board to set up the constraint graph
        for (int row = 0; row < 9; ++row)
        {
            for (int column = 0; column < 9; ++column)
            {
                int value = puzzle[row][column];
                if (value != 0)
                {
                    constraintGraph[row][column] = new Node(value, true);
                }
                else
                {
                    constraintGraph[row][column] = new Node(value, domain, false);
                }
            }
        }
    }

    public void solve()
    {
        // remove from the domain row column then 3 x 3 cell until nothing changes
        // then if the puzzle isn't solved pass the puzzle to backtracking
        boolean changed = true;
        while (changed)
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
    }

    List< Node > getBox(int row, int column)
    {
        List< Node > ret = new ArrayList< Node >(9);
        for (int rIter = row; rIter < rIter + 3; ++rIter)
        {
            for (int colIter = column; colIter < column + 3; ++colIter)
            {
                Node n = constraintGraph[row][column];
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
        for (Node n : box)
        {
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
        
        // true iff this value was given in the puzzle
        private boolean setInStone;

        // true iff the value has been assigned.
        private boolean haveValue;

        private int value;

        public Node(int value, boolean givenValue)
        {
            setInStone = givenValue;
            haveValue = givenValue;
            this.value = value;
        }

        public Node(int value, List< Integer > domain, boolean givenValue)
        {
            this.domain = domain;
            setInStone = givenValue;
            haveValue = givenValue;
            this.value = value;
        }
        
        public boolean removeFromDomain(int value)
        {
            // this will return true if the list contained the element
            boolean ret = domain.remove(Integer.valueOf(value));

            if (domain.size() == 1)
            {
                this.value = domain.get(0);
                haveValue = true;
            }
            return ret;
        }

        public int getValue()
        {
            return value;
        }
    }

    private int[][] puzzle;

    private Node[][] constraintGraph;
}
