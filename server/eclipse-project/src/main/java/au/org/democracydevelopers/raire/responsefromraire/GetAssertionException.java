package au.org.democracydevelopers.raire.responsefromraire;

/** Exceptions the RAIRE algorithm may produce. The real detail is in the RaireError class. */
public class GetAssertionException extends Exception {
    public final GetAssertionError error;
    public GetAssertionException(GetAssertionError error) {
        this.error = error;
    }
}