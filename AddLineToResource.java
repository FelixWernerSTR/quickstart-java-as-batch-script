import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class AddLineToResource {
	private static final Logger log = Logger.getLogger(AddLineToResource.class.getName());
	
	private String line;
	private String resourcePath;
    
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new AddLineToResource().line(args[0]).resourcePath(args[1]).process();
	}
	
	public AddLineToResource line(String line) {
		this.line = line;
		return this;
	}
	
	public AddLineToResource resourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
		return this;
	}

	void process() throws IOException { 
    	if(line == null || resourcePath == null) {
    		throw new RuntimeException("line and resourcePath have to be set! line/resourcePath: "+line+"/"+resourcePath);
    	}

    	List<String> currentFileLines = new ArrayList<>();
    	if(Files.exists(Paths.get(resourcePath))) {
    		currentFileLines = Files.readAllLines(Paths.get(resourcePath));
    	}else {
    		log.warning("ATTENTION! "+resourcePath+ " does not exist and will be created now!");
    	}
    	if(!currentFileLines.contains(line)) {
    		currentFileLines.add(line);
    		Files.write(Paths.get(resourcePath), currentFileLines, StandardOpenOption.CREATE);
    	}
	}

}
