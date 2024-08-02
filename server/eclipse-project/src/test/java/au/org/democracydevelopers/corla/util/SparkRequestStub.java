package au.org.democracydevelopers.corla.util;

import spark.Request;

import java.util.Set;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 * Note this does _not_ (yet) implement all the functions you might need - it contains only the ones
 * that I noticed being used in colorado-rla.
 * https://javadoc.io/doc/com.sparkjava/spark-core/2.5.4/spark/Request.html
 */
public class SparkRequestStub extends Request {

    private final String _body;
    private final Set<String> _queryParams;

  /**
   * Constructor, for use in testing.
   * @param body        The body of the request.
   * @param queryParams The http query parameters.
   */
  public SparkRequestStub(final String body, final Set<String> queryParams)
    {
      super();

      _body = body;
      _queryParams = queryParams;
    }

  /**
   * Get the body.
   * @return the body.
   */
  public String body()
    {
        return _body;
    }

  /**
   * This is a mock of getting the host, but obviously isn't set up
   * to return the actual host.
   * @return a test string.
   */
  public String host() { return "Test host";}

  /**
   * Get the query parameters that were set in the constructor.
   * @return the query parameters.
   */
  public Set<String> queryParams() { return _queryParams;}
}
