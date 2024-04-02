import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 */
public class ExecuteGitCommandInAllSubFolders {
	
	static int waitFor = Integer.valueOf(System.getProperty("PROCESS_WAIT_FOR_EXECUTING_COMMAND", "30"));

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("bitte GitReposRoot-Verzeichnis angeben!");
		} else {
			String command = "git;pull;--progress;origin";
			Files.list(Paths.get(args[0])).filter( s-> Files.isDirectory(s)).forEach(s -> process(s,command.split(";")));
			System.exit(0);
		}
	}
	
	private static void process(Path s, String... commands) {

		try {
			System.out.println("execute: "+String.join(";", commands)+ " in folder: "+s);
			writeToFile(executeCommand(s.toFile(),commands),ExecuteGitCommandInAllSubFolders.class.getSimpleName() + ".ptotokoll.log");
			System.out.println(String.join(";", commands)+ " in folder: "+s+" finished!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static List<String> executeCommand(File workingPath, String... command) throws Exception {

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(workingPath);
		Process process = processBuilder.start();
		
		System.out.println("executeCommand... with timeout of: "+waitFor+" seconds");
		
		boolean processWaitFor = process.waitFor(waitFor, TimeUnit.SECONDS);
		
		List<String> lines = new ArrayList<>();
		if(processWaitFor) {
			InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
			BufferedReader reader = new BufferedReader(inputStreamReader);
			//System.out.println("initialized reader: inputStreamReader/reader");
			String line;
			
			lines.add(workingPath.getName());
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				lines.add(line);
			}
			//System.out.println("closing: inputStreamReader/reader");
			reader.close();
			inputStreamReader.close();
		}else {
			logErrors(process);
		}

		process.destroyForcibly();
		
		lines.add("successfull?!: "+processWaitFor+"\n");
		return lines;
	}

	/**
	 * @param process
	 * @throws IOException
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private static void logErrors(Process process) throws InterruptedException, ExecutionException, TimeoutException {

		//diese Lösung im callable eingebettet, da es manchmal hängt!
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		final Future<Void> handler = executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				InputStreamReader inputStreamReader = new InputStreamReader(process.getErrorStream());
				BufferedReader reader = new BufferedReader(inputStreamReader);
				System.out.println("logErrors with timeout of: "+waitFor+" seconds!");
				String line;
				List<String> errors = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					errors.add(line);
				}
				if (!errors.isEmpty()) {
					writeToFile(errors, ExecuteGitCommandInAllSubFolders.class.getSimpleName() + ".error.log");
				}
				System.out.println("logErrors finished.");
				reader.close();
				inputStreamReader.close();
				process.destroyForcibly();
	
				return null;
			}

		});
		
		handler.get(waitFor, TimeUnit.SECONDS);

	}

	private static void writeToFile(List<String> lines, String fileName) throws IOException {
		if (Files.notExists(Paths.get(fileName))) {
			Files.write(Paths.get(fileName), String.join("\n", lines).getBytes());
		} else {
			Files.write(Paths.get(fileName), String.join("\n", lines).getBytes(), StandardOpenOption.APPEND);
		}
	}

}
