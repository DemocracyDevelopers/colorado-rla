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

    public SparkRequestStub(final String body, final Set<String> queryParams)
    {
      super();

      _body = body;
      _queryParams = queryParams;
    }

    public String body()
    {
        return _body;
    }
    public String host() { return "Test host";}
    public Set<String> queryParams() { return _queryParams;}
}
