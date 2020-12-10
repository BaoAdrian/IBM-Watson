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
   
    # Delete .zip (no longer needed)
    $ rm index.zip
   
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

3. If you chose to skip (2) OR you wish to test the IndexEngine's ability to build an Index, you will need to download the following wiki data and place into the expected directory as the program will expect they exist in order to build the index.
   - Download wiki tar: https://www.dropbox.com/s/nzlb96ejt3lhd7g/wiki-subset-20140602.tar.gz?dl=0
   - Move tarball into the directory expected by the program
      ```
      # Enter cloned repo's src directory
      $ cd /path/to/IBM-Watson/src
     
      # Create resources directory & enter it
      $ mkdir resources
      $ cd resources
     
      # Move tarball into src/resources & untar it
      $ mv /path/to/wiki-subset-20140602.tar.gz .
      $ tar -zxvf wiki-subset-20140602.tar.gz
     
      # Remove tarball (no longer needed)
      $ rm wiki-subset-20140602.tar.gz
     
      # If successful, you should see something like the following
      $ pwd
      /path/to/IBM-Watson/src
      $ tree
      .
      ├── main
      │   ├── Constants.java
      │   ├── IBMWatson.java
      │   ├── IndexEngine.java
      │   ├── QueryEngine.java
      │   └── Result.java
      ├── resources
      │   ├── enwiki-20140602-pages-articles.xml-0005.txt
      │   ├── enwiki-20140602-pages-articles.xml-0006.txt
      │   ├── enwiki-20140602-pages-articles.xml-0007.txt
      ... more wiki files from tarball ...
      ```
   
4. Open your favorite IDE & run the driver, `IBMWatson.java`
5. If successful, you should see an execution similar to the following
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
   
    OR (sample execution when index already exists)
   
    ```
    Welcome to IBM Watson Lite!
    
    How would you like to build the index?
     (1) None
     (2) Use Lemmatization
     (3) Use Stemming
    > 3
    
    How would you like perform queries?
     (1) BM25
     (2) Boolean
     (3) TF-IDF
     (4) Jelinek Mercer
    > 4
    Building index...
    
    Index already exists at: 'index/stemming'
    If you wish to rebuild index, please delete existing index.
    
    Processing queries...
    Got 26/100 = 0.26
    Would you like to go again? (y/n)
    n
    
    Terminating program...
    
    Process finished with exit code 0
    ```
 
# Report
Full project report & analysis can be found here: [CSC483_IBMWatsonReport_AdrianBao](./CSC483_IBMWatsonReport_AdrianBao.pdf)