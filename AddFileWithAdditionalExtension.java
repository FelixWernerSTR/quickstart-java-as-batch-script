import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddFileWithAdditionalExtension extends SimpleFileVisitor<Path> {
  
  private static Logger logger = Logger.getLogger(AddFileWithAdditionalExtension.class.getName());
  
  private String regex = "(.*)(.groovy)";
  
  private String suffix = ".md"; // 2. group
  
  private Path baseDir = Paths.get("C:/temp");
  
  public static void main(String[] args) {
    if (args.length == 2) {
      new AddFileWithAdditionalExtension().path(Paths.get(args[0])).regex(args[1]).process();
    } else {
      new AddFileWithAdditionalExtension().process();
    }
    
  }
  
  /**
   * @param baseDir
   */
  public AddFileWithAdditionalExtension path(Path baseDir) {
    this.baseDir = baseDir;
    return this;
  }
  
  /**
   * @param regex
   */
  public AddFileWithAdditionalExtension regex(String regex) {
    this.regex = regex;
    return this;
  }
  
  public void process() {
    logger.info("renaming started! :{}" + this.toString());
    try {
      Files.walkFileTree(baseDir, this);
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("renaming finished!");
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
      logger.info("found: " + file);
      Path target = Paths.get(file + suffix);
      try {
        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
      
    }
    return FileVisitResult.CONTINUE;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FilesRenamer [regex=");
    builder.append(regex);
    builder.append(", baseDir=");
    builder.append(baseDir);
    builder.append("]");
    return builder.toString();
  }
  
}
