
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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

    public static String millisToShortDHMS(long duration) {
        String res = "";    // java.util.concurrent.TimeUnit;
        long days       = TimeUnit.MILLISECONDS.toDays(duration);
        long hours      = TimeUnit.MILLISECONDS.toHours(duration) -
                          TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes    = TimeUnit.MILLISECONDS.toMinutes(duration) -
                          TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds    = TimeUnit.MILLISECONDS.toSeconds(duration) -
                          TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        long millis     = TimeUnit.MILLISECONDS.toMillis(duration) - 
                          TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration));

        if (days == 0)      res = String.format("%02d:%02d:%02d.%04d", hours, minutes, seconds, millis);
        else                res = String.format("%dd %02d:%02d:%02d.%04d", days, hours, minutes, seconds, millis);
        return res;
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

        long startTime = System.nanoTime();
        s.solve();
        long endTime = System.nanoTime();
        
        System.out.println("Terminating");
        long duration = (endTime - startTime);
        System.out.println("Took " + (duration/1000000) + " milliseconds");
        System.out.println("Timer " + millisToShortDHMS( duration/1000000 ));
    }
}
