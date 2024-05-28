package de.svi.devops.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Diese Klasse kann mit jedem Editor angepasst werden und 
 *  mit Java11+ direkt aufgerufen werden: [JAVA_11_HOME]/bin/java HttpCheck
 */
public class HttpCheck {
  private static final Logger logger = Logger.getLogger(HttpCheck.class.getName());
  static {
    logger.setLevel(Level.FINE);
  }
  
  /**
   *
   * @param args
   * @throws InterruptedException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    
    HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(20)).build();
    
    HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(args[0])).build();
    
    HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    
    logger.info("httpResponse.statusCode(): " + httpResponse.statusCode());
    
    logger.info("httpResponse.sslSession().isEmpty():" + httpResponse.sslSession().isEmpty());
    
    logger.info("httpResponse.sslSession().isPresent:" + httpResponse.sslSession().isPresent());
    
    logger.info("httpResponse.headers():" + httpResponse.headers().map());
    
    logger.fine("httpResponse.body(): " + httpResponse.body());
    
  }
  
}
