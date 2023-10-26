package us.freeandfair.corla.model;


public class IRVPreference implements Comparable<IRVPreference> {

    private final Integer rank;
    private final String candidateName;

    public int getRank() {
        return rank;
    }

    public String getCandidateName() {
        return candidateName;
    }
    public IRVPreference(Integer r, String name) {
        rank = r;
        candidateName = name;
    }

    // We only care about whether the rank is lower than the other rank,
    // not the name of the candidate.
    @Override
    public int compareTo(IRVPreference preference) {
        return this.rank.compareTo(preference.rank);
    }

    public String toString() {
        return candidateName+"("+rank+")";
    }
}
