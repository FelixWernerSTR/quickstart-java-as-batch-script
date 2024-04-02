import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class FilesFinder extends SimpleFileVisitor<Path> {
  
  private static Logger logger = Logger.getLogger(FilesFinder.class.getName());
  
  private String regex = "(.*log4j.*)";
  private Path baseDir = Paths.get("C:/temp");
  
  public static void main(String[] args) {
    if (args.length == 2) {
      new FilesFinder().path(Paths.get(args[0])).regex(args[1]).find();
    } else {
      new FilesFinder().find();
    }
    
  }
  
  /**
   * @param baseDir
   */
  public FilesFinder path(Path baseDir) {
    this.baseDir = baseDir;
    return this;
  }
  
  /**
   * @param regex
   */
  public FilesFinder regex(String regex) {
    this.regex = regex;
    return this;
  }
  
  public void find() {
    logger.info("find started! :{}" + this.toString());
    try {
      Files.walkFileTree(baseDir, this);
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("find finished!");
  }
  
  @Override
  public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) {
    // logger.info("preVisitDirectory " + file.getFileName().toString());
    return FileVisitResult.CONTINUE;
  }
  
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    String filename = file.getFileName().toString();
    
    Pattern searchPattern = Pattern.compile(regex);
    Matcher matcher = searchPattern.matcher(filename);
    
    if (matcher.find()) {
      logger.info("found:" + file);
      
    }
    return FileVisitResult.CONTINUE;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FilesFinder [regex=");
    builder.append(regex);
    builder.append(", baseDir=");
    builder.append(baseDir);
    builder.append("]");
    return builder.toString();
  }
  
}
