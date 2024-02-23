package au.org.democracydevelopers.util;

public class IRVParsingException extends Exception {
    // Parameterless Constructor
    public IRVParsingException() {}

    // Constructor that accepts a message
    public IRVParsingException(String s) {
        super(s);
    }
}
