package us.freeandfair.corla.model;

public class IRVBallotInterpretationDuplicatesBeforeOvervotes {

    public static IRVChoices InterpretValidIntent(final IRVChoices b) {
        IRVChoices i3 = b.ApplyRule3();
        IRVChoices i1 = i3.ApplyRule1();
        return i1.ApplyRule2();
    }
}