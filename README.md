# Document Search

## Dependencies
Apache Commons Lang (only needed for PerformanceTest.java) - https://commons.apache.org/proper/commons-lang/ <br>
JUnit 5 (only needed for SearchTest.java) -https://junit.org/junit5/

## Class Structure
DocumentSearch - Provides a simple command line interface to use the search engine <br>
SearchEngine - Houses actual search logic as well as loading the documents into memory <br>
SearchIndex - Provides the use a search index that keeps track of all occurrences of all words accross all files <br>
PerformanceTest - Quick and simple performance test to compare runtime of the three search algorithms <br>
SearchTest - Unit tests for the search engine <br>

## Testing
To use the search engine, simply compile DocumentSearch, SearchEngine, and SearchIndex. Then run DocumentSearch to interact with the search engine via the command line. PerformanceTest and SearchTest both require the dependencies shown above. Jar files can be found at the provided links. I also provide output from both below.

## Overall Approach

### String Matching
I wasn't entirely sure what was meant "simple string matching", but given the request for three iterative approaches, I assumed this was meant to be the naive solution. Therefore I used a quick and simple for loop to check every position of every file for the search term. This is obviously very ineficient, O(n) where n is the number of total characters across all documents.

### Regex Search
This approach was also very straightforward, just using a regex for the search term over each document. I did some research on regex runtimes, and in general they are O(n), so no real improvement on our first approach aside from simpler code. However I did see some interesting performance differences here, which I can explain more in depth during the review.

### Indexed Search
For this appoach, I preprocessed all documents into a map with the following structure:<br><br>
Key - A unique string for each combination of document & word<br>
Value - A set of integers that denote all indices the the word occurs in the document<br>
<br>
This allows for O(1) lookup on each document, or O(n) where n is the number of documents. Mapping the indices rather than just the counts also allows us to handle multi-word search terms, since we can check that certain words appear in order in a given document.
