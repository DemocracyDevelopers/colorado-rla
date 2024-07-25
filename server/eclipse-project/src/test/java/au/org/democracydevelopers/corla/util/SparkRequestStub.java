package au.org.democracydevelopers.corla.util;

import spark.Request;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 */
public class SparkRequestStub extends Request {

    private final String _body;

    public SparkRequestStub(String body)
    {
      super();

      _body = body;
    }

    public String body()
    {
        return _body;
    }
}
