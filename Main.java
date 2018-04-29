
import java.util.Scanner;

class Main
{
    public enum Algorithms
    {
        Backtracking,
        Forwardchecking,
        AC3,
        Heuristic,
        SimulatedAnnealing
    }

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Not enough args");
            return;
        }

        // read in the input
        Algorithms algorithm = Algorithms.values()[Integer.parseInt(args[0])];
        ReadInput inputReader = new ReadInput();

        // file name is second arg
        int[][] puzzle = inputReader.readInput(args[1]);

        SolverInterface s;
        switch (algorithm)
        {
            case Backtracking:
                s = new BackTracking(puzzle); 
                System.out.println("Running Backtracking");
                break;
            case Forwardchecking:
                s = new ForwardChecking(puzzle);
                System.out.println("Running Forwardchecking");
                break;
            case AC3:
                s = new AC3Solver(puzzle);
                System.out.println("Running AC3");
                break;
            case Heuristic:
                s = new Heuristic(puzzle);
                System.out.println("Running Heauristic");
                break;
            case SimulatedAnnealing:
                s = new SimulatedAnnealing(puzzle);
                System.out.println("Running Annealing");
                break;
            default:
                System.out.println("Incorrect algorithm");
                return;
        }
        s.solve();
    }
}
