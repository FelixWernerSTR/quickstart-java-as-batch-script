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
 * 
 * bitte als Parameter eine Text-Datei mit Liste mit Docker-Images uebergeben:[imageName:version]
 * 
 */
public class PullAndSaveDockerImagesAsTar {
  
  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("biite eine DockerImage Liste im Format Uebergeben: [imageName:version] und Docker-Daemon/Docker-Desktop muss laufen!");
    } else {
      String multilineString = new String(Files.readAllBytes(Paths.get(args[0])));
      for (String line : multilineString.lines().filter(line -> !line.isBlank()).map(String::strip).collect(Collectors.toList())) {
        System.out.println(line.split(":")[0] + ":" + line.split(":")[1]);
        process("docker", "pull", line);
        System.out.println("pull finished: " + line);
        process("docker", "save", line, "-o", line.split(":")[0].replace('/','-') + "-" + line.split(":")[1] + ".tar");
        System.out.println("save finished: " + line.split(":")[0].replace('/','-') + "-" + line.split(":")[1] + ".tar");
      }
    }
    
  }
  
	private static int process(String... command) throws Exception {

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();

		logInputAndErrorStream(process);

		return process.waitFor();
	}
	
	private static void logInputAndErrorStream(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			lines.add(line);
		}
		writeToFile(lines,PullAndSaveDockerImagesAsTar.class.getSimpleName() + ".ptotokoll.log");	
		logErrors(process);
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
			writeToFile(errors,PullAndSaveDockerImagesAsTar.class.getSimpleName() + ".error.log");
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

