# IBM Watson
Final Project - CSC483


# Getting Started
Follow the steps below to get up and running:
1. Clone this repo
    ```
    $ git clone https://github.com/BaoAdrian/IBM-Watson.git
    ```
2. (Optional) If you wish to use pre-built indexes, you can download the zipped manifest here: https://drive.google.com/file/d/1CABlrkPPYIvklZ6HzgijiUYaIRbz9bQ1/view?usp=sharing
   - Once downloaded, place zip into the `IBM-Watson` directory & unzip
    ```
    # Enter cloned repo & move zipped manifest
    $ cd /path/to/IBM-Watson
    $ mv /path/to/index.zip .
   
    # Unzip
    $ unzip index.zip
   
    # If done correctly, your directory tree should be something like this
    $ tree -d
    .
    ├── index <-- index location
    │   ├── default
    │   ├── lemma
    │   └── stemming
    ├── src   <-- same level as src
    │   ├── main
    │   └── resources
    ...
    ```
   - The above is NOT required if you wish to build the index from scratch
   
3. Open you favorite IDE & run the driver, `IBMWatson.java`
4. If successful, you should see an execution similar to the following
    ```
    Welcome to IBM Watson Lite!
    
    How would you like to build the index?
     (1) None
     (2) Use Lemmatization
     (3) Use Stemming
    > 1
    
    How would you like perform queries?
     (1) BM25
     (2) Boolean
     (3) TF-IDF
     (4) Jelinek Mercer
    > 1
   
    Building index...
    0 File: src/resources/enwiki-20140602-pages-articles.xml-0058.txt
    1 File: src/resources/enwiki-20140602-pages-articles.xml-0689.txt
    2 File: src/resources/enwiki-20140602-pages-articles.xml-0067.txt
    ... more files processed ...
   
    Processing queries...
    Got 21/100 = 0.21
    Would you like to go again? (y/n)
    n
    
    Terminating program...
    
    Process finished with exit code 0
    ```
 
# Report
Full project report & analysis can be found here: [CSC483_IBMWatsonReport_AdrianBao](./CSC483_IBMWatsonReport_AdrianBao.pdf)