import java.util.*;

class SimulatedAnnealing implements SolverInterface
{
	 private void copyarray(int[][] x,int[][] y)
	 {

		 for(int i = 0; i < 9; i++)
			 for(int j = 0; j < 9; j++)
				 y[i][j]=x[i][j];
		 
	 }
	 
	 private void neighbourPuzzle(int[][] neighbour)
	 {

		 copyarray(puzzle,neighbour);
		 Random rand= new Random();
		 int randomCol1,randomCol2,row ;
		 do {
			row = rand.nextInt((9));
		    randomCol1 = rand.nextInt((9));
		 }while(fixedCells[row][randomCol1]);
		  do	{ 
		        do {randomCol2 = rand.nextInt((9));
		        }while (randomCol2==randomCol1);
			    randomCol2 = rand.nextInt((9));
		  }while(fixedCells[row][randomCol2]);
		 // switch 
		 int temp=neighbour[row][randomCol1];
		 neighbour[row][randomCol1]= neighbour[row][randomCol2];
		 neighbour[row][randomCol2]=temp;
	 }
	 private int threeBythree(int x,int y,int[][] board)
	 {
		 int count=0;
		 int[] countDigit =new int[10];
		  for(int i = 1; i <= 9; i++)
			   countDigit[i]=-1;	
		 for(int col = 0; col < 3; col++)
		 for(int row = 0; row < 3; row++)
			 countDigit[board[x+row][y+col]]++;
		 for (int i = 1; i <= 9; i++)
			 if (countDigit[i]>0)
			  count+=countDigit[i];
		 
		
		 return count;
	 }
	 public List<Integer> NumberFrom1To9(List<Integer> numbers)
 	{
 		
 		for(int i = 1; i <= 9; i++)
 			numbers.add(i);
 		return numbers;
 	}
	 private int ViolationCount(int[][] board)
		{
		 int count=0;
		 int[] countDigit =new int[10];

		  // count number of violation in col
		 for(int col = 0; col < 9; col++){
			 for(int i = 1; i <= 9; i++)
				 countDigit[i]=-1;	
			 for(int row = 0; row < 9; row++)
				 countDigit[board[row][col]]++;
			 for (int i = 1; i <= 9; i++)
				 if (countDigit[i]>0)
				  count+=countDigit[i];  
		 
		 }  
		 // count number of violation in 3*3 
		 count+=threeBythree(0,0,board);
		 count+=threeBythree(0,3,board);
		 count+=threeBythree(0,6,board);
		 count+=threeBythree(3,0,board);
		 count+=threeBythree(3,3,board);
		 count+=threeBythree(3,6,board);
		 count+=threeBythree(6,0,board);
		 count+=threeBythree(6,3,board);
		 count+=threeBythree(6,6,board);

		 
		 return count;
		}
    
    
    public SimulatedAnnealing(int[][] puzzle)
    {
        this.puzzle = puzzle;        
     // set true to fixed Cells in puzzle
   	 for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle[i][j] != 0)
					fixedCells[i][j] = true;
				else
					fixedCells[i][j] = false;

			}
		}
   	
   	 
    }

    public void solve()
    {   
    	//print /////////////////////////
    	for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++)
			   System.out.print(puzzle[i][j] + " ");
			System.out.println();
		}
        /////////////////////////////////
    	List<Integer> numbers =new ArrayList<Integer>();
         numbers = NumberFrom1To9(numbers);
    	 // randomly fill the 0's cells with remaining numbers row by row  
    	for(int row = 0; row < 9; row++)
			{
    		    Random randomizer = new Random();
    		    for(int col= 0; col< 9; col++)
    		    	if(puzzle[row][col] != 0)
					   numbers.remove(numbers.indexOf(puzzle[row][col])); 
    		    for(int col = 0; col < 9; col++)
 				   if(puzzle[row][col] == 0)
 				   {
 					  puzzle[row][col]= numbers.get(randomizer.nextInt(numbers.size()));
 					  numbers.remove(numbers.indexOf(puzzle[row][col]));
 				   }
    		    // reset for next row
    		    numbers = NumberFrom1To9(numbers);  
			}  
     	//print /////////////////////////
    	for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++)
			   System.out.print(puzzle[i][j] + " ");
			System.out.println();
		}
        /////////////////////////////////
    	 System.out.println("Violation: "+ViolationCount(puzzle));
    	////////////////////////////////////////////////////////////
    	 int iteration=-1;
    	 double proba;            // probability of accepting wrong
    	 double alpha =0.999;
    	 double temperature = 2.5;
    	 double epsilon = 0.001;
    	 double delta;
         int[][] nighbour=new int[9][9];
    	  //while the temperature did not reach epsilon
    	 while(temperature > epsilon)
    	    {
    		 iteration++;
    		 if (iteration%1000==0)
    			 System.out.println("iteration:"+iteration+"violation: "+ViolationCount(puzzle)+"\n");
    	     //get the next random
    		 neighbourPuzzle(nighbour);  
    		 delta=ViolationCount(nighbour)-ViolationCount(puzzle);
    		 if(ViolationCount(puzzle)==0)
    		 {
    			 System.out.println("solution found! \n");
    			 break;
    		 }    			 
    		 if (delta<0)
    			 copyarray(nighbour,puzzle);
    		 else
    	        {
    	            proba = Math.random();
    	           // System.out.println(proba);
    	            if(proba< Math.exp(-delta/temperature))
    	            	copyarray(nighbour,puzzle);
    	        }
    	        //cooling 
    	        temperature *=alpha;
    	        //print every 400 iterations

    	    }
    	  	//print /////////////////////////
    	    	for(int i = 0; i < 9; i++){
    				for(int j = 0; j < 9; j++)
    				   System.out.print(puzzle[i][j] + " ");
    				System.out.println();
    		 }
   	    	System.out.println("Violation: "+ViolationCount(puzzle));
    	    	 
   }
    private int[][] puzzle;
	private boolean[][] fixedCells =new boolean[9][9];
}

