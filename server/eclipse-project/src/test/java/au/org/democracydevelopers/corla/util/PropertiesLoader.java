package au.org.democracydevelopers.corla.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads a properties file. Used for test.properties.
 * Copied from <a href="https://www.baeldung.com/inject-properties-value-non-spring-class">...</a>
 */
public class PropertiesLoader {

    public static Properties loadProperties(String resourceFileName) throws IOException {
      Properties configuration = new Properties();
      InputStream inputStream = PropertiesLoader.class
          .getClassLoader()
          .getResourceAsStream(resourceFileName);
      configuration.load(inputStream);
      assert inputStream != null;
      inputStream.close();
      return configuration;
    }
}
