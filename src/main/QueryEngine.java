package main;

import edu.stanford.nlp.simple.Sentence;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class QueryEngine {
    private IndexEngine indexEngine;
    private String queryMethod;
    private String indexMethod;

    // Lucene objects needed to read from index
    private IndexReader reader;
    private IndexSearcher searcher;

    // Running statistics
    int totalQCount = 0;
    int correctAnsCount = 0;

    public QueryEngine(IndexEngine indexEngine, String queryMethod, String indexMethod) {
        this.indexEngine = indexEngine;
        this.queryMethod = queryMethod;
        this.indexMethod = indexMethod;

        // Setup Reader
        try {
            Directory index = FSDirectory.open(new File(indexEngine.getIndexPath()).toPath());
            reader = DirectoryReader.open(index);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Setup Searcher
        searcher = new IndexSearcher(reader);
        if (this.queryMethod.equals(Constants.BOOLEAN)) {
            searcher.setSimilarity(new BooleanSimilarity());
        } else if (this.queryMethod.equals(Constants.BM25)) {
            searcher.setSimilarity(new BM25Similarity());
        } else if (this.queryMethod.equals(Constants.TF_IDF)) {
            // Ref: https://lucene.apache.org/core/7_2_1/core/index.html?org/apache/lucene/search/similarities/ClassicSimilarity.html
            searcher.setSimilarity(new ClassicSimilarity()); // extends TFIDFSimilarity
        } else if (this.queryMethod.equals(Constants.JM)) {
            searcher.setSimilarity(new LMJelinekMercerSimilarity((float)0.5));
        }
    }

    /**
     * Method that iterates over all questions provided by given
     * path & uses the generated IndexEngine to execute queries
     */
    public void processQuestions() {
        // Format of questions being parsed
        // (1) Category
        // (2) Clue
        // (3) Answer
        // (4) Newline (whitespace)
        String category, clue, answer;
        ArrayList<Result> results;

        try (Scanner sc = new Scanner(new File(Constants.pathToQuestions))) {
            while (sc.hasNextLine()) {
                // Extract items pertaining to THIS question
                category = sc.nextLine().trim();
                clue = sc.nextLine().trim();
                answer = sc.nextLine().trim();
                sc.nextLine(); // Ingest newline (unused)

                // Gather results from query on index (10 results returned)
                results = executeQuery(category, clue);

                // Check if the top hit was the correct answer
                if (results.size() > 0 && results.get(0).getDocName().get("title").equals(answer)) {
                    // Correct!
                    correctAnsCount++;
                }
                totalQCount++;
            }
            double score = (double) correctAnsCount / (double) totalQCount;
            System.out.println("Got " + correctAnsCount + "/" + totalQCount + " = " + score);
            reader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Takes in the category & clue of the from questions.txt &
     * generates a query from them to search the index for possible
     * matches. Returns list of results from the query.
     *
     * @param category Category defined in questions.txt
     * @param clue Clue defined in questions.txt
     * @return List of Results (top 10 results with highest similarity scores)
     */
    private ArrayList<Result> executeQuery(String category, String clue) {
        ArrayList<Result> results = new ArrayList<>();

        try {
            String contentToParse = processQuestionContent(category + " " + clue);

            // Generate Query
            Query query = new QueryParser("text", indexEngine.getAnalyzer()).parse(contentToParse);
            TopDocs docs = searcher.search(query, 10);
            ScoreDoc[] hits = docs.scoreDocs;

            // Process hits
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);

                results.add(new Result(d, hits[i].score));
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    /**
     * Processes questionContent according to the user-specified method
     * of Stemming, Lemmatization, or neither. Return processed string.
     *
     * @param qContent Question content to be utilized as the query
     * @return Processed query string 
     */
    private String processQuestionContent(String qContent) {
        StringBuilder sb = new StringBuilder();

        // Process qContent based on what user chose for indexing
        Sentence sentence = new Sentence(qContent.toLowerCase());
        if (this.indexMethod.equals(Constants.LEMMA)) {
            for (String lemma: sentence.lemmas()) {
                sb.append(lemma + " ");
            }
        } else if (this.indexMethod.equals(Constants.STEMMING)) {
            PorterStemmer stemmer = new PorterStemmer();
            for (String term: sentence.words()) {
                stemmer.setCurrent(term);
                stemmer.stem();
                sb.append(stemmer.getCurrent() + " ");
            }
        } else {
            for (String word: sentence.words()) {
                sb.append(word + " ");
            }
        }

        return sb.toString();
    }
}
