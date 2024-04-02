import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 */
public class ExecuteCommandFromTextFile {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("bitte eine Liste im Format Uebergeben: [command:param1:param2:paramX...]!");
		} else {
			String multilineString = new String(Files.readAllBytes(Paths.get(args[0])));
			for (String line : multilineString.lines().filter(line -> !line.isBlank()).map(String::strip).collect(Collectors.toList())) {
				System.out.println(line);
				process(line.split(":"));
				System.out.println("command finished: " + line);
			}
		}

	}

	private static int process(String... command) throws Exception {

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			lines.add(line);
		}
		writeToFile(lines,ExecuteCommandFromTextFile.class.getSimpleName() + ".ptotokoll.log");	
		logErrors(process);

		return process.waitFor();
	}

	/**
	 * @param process
	 * @throws IOException
	 */
	private static void logErrors(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;
		List<String> errors = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			errors.add(line);
		}
		if (!errors.isEmpty()) {
			writeToFile(errors,ExecuteCommandFromTextFile.class.getSimpleName() + ".error.log");
		}
	}

	private static void writeToFile(List<String> lines, String fileName) throws IOException {
		if (Files.notExists(Paths.get(fileName))) {
			Files.write(Paths.get(fileName),
					String.join("\n", lines).getBytes());
		} else {
			Files.write(Paths.get(fileName),
					String.join("\n", lines).getBytes(), StandardOpenOption.APPEND);
		}
	}

}
