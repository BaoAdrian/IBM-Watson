package main;

import java.io.IOException;
import java.util.Scanner;

public class IBMWatson {
    // Optional flags
    public static boolean useLemma = false;
    public static boolean useStemming = false;
    public static boolean useBooleanQuery = false;
    public static boolean useBM25Query = false;

    public static void main(String[] args) {
        System.out.println("Welcome to IBM Watson Lite!\n");

        Scanner sc = new Scanner(System.in);

        String goAgain = "";
        String indexMethod = "";
        String queryMethod = "";

        // Continue processing requests until user quits
        while(!goAgain.equals("n")) {
            printBuildMenu();
            indexMethod = sc.next().trim();

            printQueryMenu();
            queryMethod = sc.next().trim();

            IndexEngine indexEngine = new IndexEngine(indexMethod);
            QueryEngine queryEngine = new QueryEngine(indexEngine, queryMethod, indexMethod);
            try {
                System.out.println("Building index...");
                indexEngine.parseWikiData();

                System.out.println("Processing queries...");
                queryEngine.processQuestions();

            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(0);
            }

            System.out.println("Would you like to go again? (y/n)");
            goAgain = sc.next().trim();
        }

        // User chose to exit, shutdown with message
        System.out.println("\nTerminating program...");
    }

    public static void printBuildMenu() {
        System.out.println("How would you like to build the index?");
        System.out.println(" (1) None");
        System.out.println(" (2) Use Lemmatization");
        System.out.println(" (3) Use Stemming");
        System.out.print("> ");
    }

    public static void printQueryMenu() {
        System.out.println("\nHow would you like perform queries?");
        System.out.println(" (1) Boolean");
        System.out.println(" (2) TF-IDF");
        System.out.println(" (3) BM25");
        System.out.print("> ");
    }
}
