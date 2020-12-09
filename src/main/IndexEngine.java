package main;


import edu.stanford.nlp.simple.Sentence;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * IndexEngine is responsible for parsing the given input files
 * & generating the corresponding Lucene-supported Index to serve
 * as the backbone for IBM Watson.
 */
public class IndexEngine {
    // Variables storing the result from above selection(s)
    private String dataPath;
    private String indexMethod;

    // Lucene objects
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index;
    private IndexWriterConfig config;
    private IndexWriter writer;

    public IndexEngine(String indexMethod) {
        this.indexMethod = indexMethod;

        // Assign dataPath according to user-selected method
        if (indexMethod.equals(Constants.NONE)) {
            this.dataPath = Constants.defaultIndexPath;
        } else if (indexMethod.equals(Constants.LEMMA)) {
            this.dataPath = Constants.lemmaIndexPath;
        } else if (indexMethod.equals(Constants.STEMMING)) {
            this.dataPath = Constants.stemmingIndexPath;
        }
    }

    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }

    public String getIndexPath() {
        return dataPath;
    }

    public IndexWriter getIndexWriter() {
        return writer;
    }

    /**
     * Parses the provided wiki-files stored in the designated
     * directory & processes them to build the Index.
     *
     * @throws IOException
     */
    public void buildIndex() throws IOException {
        index = FSDirectory.open(new File(dataPath).toPath());
        config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(index, config);

        // Extract directory and verify it exists, create otherwise
        File dir = new File("src/resources");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // For files in directory
        String filePath;
        int i =0;
        for(String file : dir.list()) {
            filePath = "src/resources/" + file;
            System.out.println(i + " File: " + filePath);
            parseFile(filePath);
            i++;
        }
        writer.close();
        index.close();
    }

    /**
     * Parses the individual file containing wiki data and adds
     * content as Document inside Index
     *
     * @param filePath String representation of path to file
     */
    public void parseFile(String filePath) {
        try (Scanner sc = new Scanner(new File(filePath))) {
            String title = "";
            String category = "";
            String header = "";
            String text = "";
            StringBuilder contentSB = new StringBuilder();

            while (sc.hasNextLine()) {
                String lineToProcess = sc.nextLine().trim();

                // Pre-processing to extract title & other info
                if (isTitle(lineToProcess)) {
                    if (!title.isEmpty()) {
                        // New title detected, add current data & reset
                        addDoc(writer, title, category.toLowerCase(), contentSB.toString().toLowerCase());
                        title = "";
                        category = "";
                        header = "";
                        text = "";
                        contentSB = new StringBuilder();
                    }
                    title = extractTitle(lineToProcess);
                } else if (isCategory(lineToProcess)) {
                    category = extractCategory(lineToProcess);
                } else if (isHeader(lineToProcess)) {
                    header = extractHeader(lineToProcess);
                    contentSB.append(header + " ");
                } else {
                    text = sanitize(lineToProcess);
                    contentSB.append(text + " ");
                }
            }

            // Wiki done finished, add items
            addDoc(writer, title, category.toLowerCase(), contentSB.toString().toLowerCase());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Utility method used to parse the line & determine
     * whether it is a Title or not
     *
     * @param lineToProcess String to process
     * @return boolean true/false evaluation
     */
    public boolean isTitle(String lineToProcess) {
        return lineToProcess.length() > 0
                && lineToProcess.charAt(0) == '['
                && lineToProcess.contains("[[")
                && lineToProcess.contains("]]")
                && lineToProcess.contains("File:") == false
                && lineToProcess.contains("Image:") == false;
    }

    /**
     * Utility method used to extract the Title from line. Will
     * only be called if isTitle() returns true
     *
     * @param lineToProcess String to process
     * @return String Title extracted from line
     */
    public String extractTitle(String lineToProcess) {
        return lineToProcess.substring(2, lineToProcess.length()-2);
    }

    /**
     * Utility method used to parse the line & determine
     * whether it is a Category or not
     *
     * @param lineToProcess String to process
     * @return boolean true/false evaluation
     */
    public boolean isCategory(String lineToProcess) {
        return lineToProcess.length() > 0
                && lineToProcess.contains("CATEGORIES:")
                && lineToProcess.indexOf("CATEGORIES:") == 0;
    }

    /**
     * Utility method used to extract the Category from line.
     * Will only be called if isCategory() return true
     *
     * @param lineToProcess String to process
     * @return String Category extracted from line
     */
    public String extractCategory(String lineToProcess) {
        return lineToProcess.substring(12);
    }

    /**
     * Utility method used to parse the line & determine
     * whether it is a Header or not
     *
     * @param lineToProcess String to process
     * @return boolean true/false evaluation
     */
    public boolean isHeader(String lineToProcess) {
        return lineToProcess.length() > 0
                && lineToProcess.charAt(0) == '='
                && lineToProcess.charAt(lineToProcess.length() - 1) == '=';
    }

    /**
     * Utility method used to extract the Header from the line.
     * Will only be called if isHeader() returns true
     *
     * @param lineToProcess String to process
     * @return boolean true/false evaluation
     */
    public String extractHeader(String lineToProcess) {
        return lineToProcess.replace("=", "");
    }

    /**
     * Sanitizes content by removing unnecessary 'tpl' tags to prepare for
     * processing.
     *
     * @param content String to process
     * @return sanitized String with 'tpl' tags removed
     */
    public String sanitize(String content) {
        if (content.contains("tpl")) {
            content = content.replaceAll("\\[tpl\\]", " ");
            content = content.replaceAll("\\[/tpl\\]", " ");
        }
        return content;
    }

    /**
     * Method that uses an IndexWriter object to create a Document
     * containing the Title, Category, and Content then adding it
     * to the requested index being generated.
     *
     * @param writer IndexWriter object to write to Index
     * @param title Parse String Title from wiki data
     * @param category Parsed String Category from wiki data
     * @param content Parsed String Content (text + header) from wiki data
     */
    public void addDoc(IndexWriter writer, String title, String category, String content) {
        // Trim whitespace before processing
        title = title.trim();
        category = category.trim();
        content = content.trim();

        // Pre-process params for empty fields
        if (category.isEmpty()) {
            category = ".";
        }
        if (content.isEmpty()) {
            content = ".";
        }

        // Use StringBuilders to aggregate terms below
        StringBuilder categorySB = new StringBuilder();
        StringBuilder contentSB = new StringBuilder();

        if (indexMethod.equals(Constants.LEMMA)) {
            // Aggregate lemmas of category & content
            for (String lemma: new Sentence(category).lemmas()) {
                categorySB.append(lemma + " ");
            }
            for (String lemma: new Sentence(content).lemmas()) {
                contentSB.append(lemma + " ");
            }
        } else if (indexMethod.equals(Constants.STEMMING)) {
            // Use stemmer to stem category & content
            PorterStemmer stemmer = new PorterStemmer();
            for (String term: new Sentence(category).words()) {
                stemmer.setCurrent(term);
                stemmer.stem();
                categorySB.append(stemmer.getCurrent() + " ");
            }
            for (String term: new Sentence(content).words()) {
                stemmer.setCurrent(term);
                stemmer.stem();
                contentSB.append(stemmer.getCurrent() + " ");
            }
        } else {
            // Default: Process without Lemma/Stemming, append as-is
            categorySB.append(category);
            contentSB.append(content);
        }

        // Create document & add to index
        Document doc = new Document();

        String textAttr = title + " " + categorySB.toString() + " " + contentSB.toString();
        doc.add(new StringField("title", title, Field.Store.YES));
        doc.add(new TextField("category", categorySB.toString(), Field.Store.YES));
        doc.add(new TextField("text", textAttr, Field.Store.YES));

        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
