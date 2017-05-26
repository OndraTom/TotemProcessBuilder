package TotemProcessBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Totem process.
 * 
 * @author Ondřej Tom <info@ondratom.cz>
 */
public class TotemProcess implements Runnable
{
	private String command;
	
	
	private TotemProcessBuilder builder;
	
	
	private List<String> input = new ArrayList<>();
	
	
	private List<String> standardOutput = new ArrayList<>();
	
	
	private List<String> errorOutput = new ArrayList<>();
	
	
	private PrintWriter standardOutputFile = null;
	
	
	private PrintWriter errorOutputFile = null;
	
	
	private Process process = null;
	
	
	public TotemProcess(String command, TotemProcessBuilder builder)
	{
		this.command = command;
		this.builder = builder;
	}
	
	
	public void setInput(List<String> input)
	{
		this.input = input;
	}
	
	
	public void setStandardOutputFile(String standardOutputFilePath) throws IOException
	{
		this.standardOutputFile = new PrintWriter(standardOutputFilePath, "UTF-8");
	}
	
	
	public void setErrorOutputFile(String errorOutputFilePath) throws IOException
	{
		this.errorOutputFile = new PrintWriter(errorOutputFilePath, "UTF-8");
	}
	
	
	public List<String> getStandardOutput()
	{
		return this.standardOutput;
	}
	
	
	public List<String> getErrorOutput()
	{
		return this.errorOutput;
	}
	
	
	@Override
	public void run()
	{
		ProcessBuilder processBuilder = new ProcessBuilder(this.command);
		
		try
		{
			this.process = processBuilder.start();
			
			// Write standard output if the input has been provided.
			if (!this.input.isEmpty())
			{
				this.writeProcessInput();
			}
			
			this.prepareOutputs();
			this.process.waitFor();
			this.closeFiles();
			
			// Contact builder about process end - continue signal.
			this.builder.processNextProgramCall();
		}
		catch (Exception e)
		{
			
		}
	}
	
	
	/**
	 * Creates and starts the thread.
	 */
	public void start()
	{
		Thread t = new Thread(this);

		t.start();
	}
	
	
	public void stop()
	{
		this.process.destroy();
		this.closeFiles();
	}
	
	
	private void writeProcessInput() throws IOException
	{
		BufferedWriter standardOutputWriter = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream()));
				
		for (String inputLine : this.input)
		{
			standardOutputWriter.write(inputLine);
		}

		standardOutputWriter.flush();
		standardOutputWriter.close();
	}
	
	
	private void prepareOutputs() throws IOException
	{
		BufferedReader standardOutputReader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
		BufferedReader errorOutputReader	= new BufferedReader(new InputStreamReader(this.process.getErrorStream()));

		String line;

		while ((line = standardOutputReader.readLine()) != null)
		{
			this.saveStandardOutputLine(line);
		}

		while ((line = errorOutputReader.readLine()) != null)
		{
			this.saveErrorOutputLine(line);
		}
	}
	
	
	private void saveStandardOutputLine(String line)
	{
		if (this.standardOutputFile != null)
		{
			this.standardOutputFile.println(line);
		}
		else
		{
			this.standardOutput.add(line);
		}
	}
	
	
	private void saveErrorOutputLine(String line)
	{
		if (this.errorOutputFile != null)
		{
			this.errorOutputFile.println(line);
		}
		else
		{
			this.errorOutput.add(line);
		}
	}
	
	
	private void closeFiles()
	{
		if (this.standardOutputFile != null)
		{
			this.standardOutputFile.close();
		}
		
		if (this.errorOutputFile != null)
		{
			this.errorOutputFile.close();
		}
	}
}
