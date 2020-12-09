package main;

import org.apache.lucene.document.Document;

public class Result {
    private Document DocName;
    private double docScore = 0;

    public Result(Document DocName, double docScore) {
        this.DocName = DocName;
        this.docScore = docScore;
    }

    /**
     * Public accessor of the DocName attribute
     *
     * @return DocName instance variable
     */
    public Document getDocName() {
        return this.DocName;
    }

    /**
     * Public accessor of the docScore attribute
     *
     * @return docScore instance variable
     */
    public double getDocScore() {
        return this.docScore;
    }
}
