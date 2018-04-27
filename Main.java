
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

        // instantiate your solver and invoke solve
        SolverInterface s;
        switch (algorithm)
        {
            case Backtracking:
                s = new BackTracking(puzzle); 
                s.solve();
                System.out.println("Backtracking");
                break;
            case Forwardchecking:
                s = new ForwardChecking(puzzle);
                s.solve();
                System.out.println("Forwardchecking");
                break;
            case AC3:
                s = new AC3Solver(puzzle);
                s.solve();
                System.out.println("AC3");
                break;
            case Heuristic:
                s = new Heuristic(puzzle);
                s.solve();
                System.out.println("Heauristic");
                break;
            case SimulatedAnnealing:
                s = new SimulatedAnnealing(puzzle);
                s.solve();
                System.out.println("Annealing");
                break;
        }
    }
}
