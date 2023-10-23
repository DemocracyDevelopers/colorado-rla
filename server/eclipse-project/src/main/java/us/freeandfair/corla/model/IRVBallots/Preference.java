package us.freeandfair.corla.model.IRVBallots;


public class Preference implements Comparable<Preference> {

    private final Integer rank;
    private final String candidateName;

    public int getRank() {
        return rank;
    }

    public String getCandidateName() {
        return candidateName;
    }
    public Preference(Integer r, String name) {
        rank = r;
        candidateName = name;
    }

    // We only care about whether the rank is lower than the other rank,
    // not the name of the candidate.
    @Override
    public int compareTo(Preference preference) {
        return this.rank.compareTo(preference.rank);
    }

    public String toString() {
        return candidateName+"("+rank+")";
    }
}
