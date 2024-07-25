package au.org.democracydevelopers.corla.util;

import spark.Response;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 * Note this does _not_ (yet) implement all the functions you might need, for example header(),
 * status(), etc.
 */
public class SparkResponseStub extends Response {
    private String _body;

    public SparkResponseStub(String body)
    {
        _body = body;
    }

    public String body()
    {
        return _body;
    }
}
