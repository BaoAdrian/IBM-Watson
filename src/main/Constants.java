package main;

public class Constants {
    // Path to questions
    public static final String pathToQuestions = "questions.txt";

    // Index methods
    public static final String NONE = "1";
    public static final String LEMMA = "2";
    public static final String STEMMING = "3";

    // Pre-defined locations to store generated index
    public static final String defaultIndexPath   = "index/default";
    public static final String lemmaIndexPath     = "index/lemma";
    public static final String stemmingIndexPath  = "index/stemming";

    // Query Methods
    public static final String BM25 = "1";
    public static final String BOOLEAN = "2";
    public static final String TF_IDF = "3";
    public static final String JM = "4"; // Jelinek Mercer
}
