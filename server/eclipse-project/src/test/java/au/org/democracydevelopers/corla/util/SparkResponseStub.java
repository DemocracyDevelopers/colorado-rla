package au.org.democracydevelopers.corla.util;

import spark.Response;

import javax.servlet.ServletOutputStream;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 * Note this does _not_ (yet) implement all the functions you might need - it contains only the ones
 * that I noticed being used in colorado-rla.
 * There are others, including redirect(), type() and raw() that are not yet implemented.
 * See <a href="https://javadoc.io/doc/com.sparkjava/spark-core/2.5.4/spark/Response.html">...</a> for types.
 * In particular, raw() causes problems for at least some endpoints.
 */
public class SparkResponseStub extends Response {
    private String _body;
    private int _statusCode;
    private String _name;
    private String _value;
    private ServletOutputStream _os;

    public SparkResponseStub() {
        super();
    }

    /**
     * set the body
     * @param body the body to be set.
     */
    public void body(String body) {
        _body = body;
    }

    /**
     * Get the body
     */
    public String body() {
        return _body;
    }

    public void status(int statusCode) {
        _statusCode = statusCode;
    }

    public void header(final String name, final String value) {
        _name = name;
        _value = value;
    }

    public int getStatus() {
        return _statusCode;
    }

    public int status() {
        return _statusCode;
    }
}
