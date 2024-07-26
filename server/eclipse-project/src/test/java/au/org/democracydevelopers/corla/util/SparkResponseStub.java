package au.org.democracydevelopers.corla.util;

import spark.Response;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 * Note this does _not_ (yet) implement all the functions you might need - it contains only the ones
 * that I noticed being used in colorado-rla.
 * There are others, including redirect(), type() and raw() that are not yet implemented.
 * See https://javadoc.io/doc/com.sparkjava/spark-core/2.5.4/spark/Response.html for types.
 */
public class SparkResponseStub extends Response {
    private String _body;
    private int _statusCode;
    private String _name;
    private String _value;

    public SparkResponseStub() { }

    public void body(String body)
    {
        _body = body;
    }

    public void status(int statusCode) {
        _statusCode = statusCode;
    }

    public void header(String name, String value) {
        _name = name;
        _value = value;
    }
}
