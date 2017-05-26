package TotemProcessBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TotemProcessBuilder main class.
 * 
 * @author Ondřej Tom <info@ondratom.cz>
 */
public class TotemProcessBuilder
{
	/**
	 * Program calls.
	 */
	private List<String> programCalls = new ArrayList<>();
	
	
	/**
	 * Standard output file path.
	 */
	private String standardOutputFilePath = null;
	
	
	/**
	 * Error output file path.
	 */
	private String errorOutputFilePath = null;
	
	
	private boolean isRunning = false;
	
	
	private TotemProcess runningProcess = null;
	
	
	private int programCallIndex = 0;
	
	
	public TotemProcessBuilder(String command) throws CommandParserException, InvalidCommandException
	{
		CommandParser parser		= new CommandParser(command);
		CommandValidator validator	= new CommandValidator(parser);
		
		if (!validator.isValid())
		{
			throw new InvalidCommandException(validator.getInvalidityReasons());
		}
		
		this.programCalls			= parser.getProgramCalls();
		this.standardOutputFilePath = parser.getStandardOutputFilePath();
		this.errorOutputFilePath	= parser.getErrorOutputFilePath();
	}
	
	
	/**
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) 
	{
		
	}
	
	
	public void start() throws IOException
	{
		this.isRunning = true;
		
		this.processNextProgramCall();
	}
	
	
	public void processNextProgramCall() throws IOException
	{
		if (this.isRunning)
		{
			if (this.programCallIndex < this.programCalls.size())
			{
				String programCall		= this.programCalls.get(this.programCallIndex);
				TotemProcess newProcess	= new TotemProcess(programCall, this);
				
				// Setting error file if provided.
				if (this.errorOutputFilePath != null)
				{
					newProcess.setErrorOutputFile(this.errorOutputFilePath);
				}
				
				// Non-first program call - setting input to previous program output.
				if (this.runningProcess != null)
				{
					newProcess.setInput(this.runningProcess.getStandardOutput());
				}
				
				// Last program call - setting output file if provided.
				if (this.programCallIndex == this.programCalls.size() - 1 && this.standardOutputFilePath != null)
				{
					newProcess.setStandardOutputFile(this.standardOutputFilePath);
				}
				
				newProcess.start();
				
				this.runningProcess = newProcess;
				
				this.programCallIndex++;
			}
			else
			{
				this.resetState();
			}
		}
	}
	
	
	public void stop()
	{
		if (this.isRunning)
		{
			if (this.runningProcess != null)
			{
				this.runningProcess.stop();
			}
			
			this.resetState();
		}
	}
	
	
	private void resetState()
	{
		this.runningProcess		= null;
		this.programCallIndex	= 0;
		this.isRunning			= false;
	}
	
	
	private static void printParserResult(String command)
	{
		try
		{
			CommandParser parser = new CommandParser(command);
			
			for (String programCall : parser.getProgramCalls())
			{
				System.out.println("Program call: " + programCall);
			}
			
			if (parser.hasStandardOutputFileSet())
			{
				System.out.println("Standard output: " + parser.getStandardOutputFilePath());
			}
			
			if (parser.hasErrorOutputFileSet())
			{
				System.out.println("Error output: " + parser.getErrorOutputFilePath());
			}
		}
		catch (CommandParserException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
