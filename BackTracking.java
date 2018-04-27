

public class BackTracking implements SolverInterface
{
    public BackTracking(int[][] puzzle)
    {
        this.puzzle = puzzle;
    }

    public void solve()
    {
        System.out.println("In Backtracking");
    }

    private int[][] puzzle;
}

