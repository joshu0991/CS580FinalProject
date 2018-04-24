
import java.util.Scanner;

class Main
{
    private enum Algorithms
    {
        Backtracking,
        Forwardchecking,
        AC3,
        Heuristic,
        SimulatedAnnealing
    }

    public static void main(String[] args)
    {
        Scanner Cin = new Scanner(System.in);
        System.out.println("Hello welcome to sudoku enter an algorithm to run");

        // read in the input
        Algorithms algorithm = Algorithms.values()[(Cin.nextInt())];

        // instantiate your solver and invoke solve
        SolverInterface s;
        switch (algorithm)
        {
            case Backtracking:
                s = new BackTracking(); 
                s.solve();
                System.out.println("Backtracking");
                break;
            case Forwardchecking:
                System.out.println("Forwardchecking");
                break;
            case AC3:
                System.out.println("AC3");
                break;
            case Heuristic:
                System.out.println("Heauristic");
                break;
            case SimulatedAnnealing:
                System.out.println("Annealing");
                break;
        }

    }
}
