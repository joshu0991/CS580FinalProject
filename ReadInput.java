import java.io.BufferedReader;
import java.io.FileReader;

public class ReadInput {
	
	public int[][] readInput(String fileName)
	{
		int grid[][] = new int[9][9];
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String str;
			int row = 0;
			while((str = br.readLine()) != null)
			{
				String[] nos = str.split(" ");
				for(int i = 0; i < 9; i++)
				{
					grid[row][i] = Integer.parseInt(nos[i]); 
				}
				row++;
			}
			br.close();
		}
		catch(Exception ex)
		{
			System.out.println("Exception reading input file..");
			ex.printStackTrace();
		}
		
		return grid;
		
	}
	
	public static void main(String[] args) {
		
		ReadInput read = new ReadInput();
		System.out.println(read.readInput("sudoku1.txt"));
	
	}

}
