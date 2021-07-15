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

## Performance Test
Below is the output for my performance test of 2M random search terms:<br>
Search Mode: 1 - 202022 ms<br>
Search Mode: 2 - 233430 ms<br>
Search Mode: 3 - 4340 ms<br><br>
As you can see, the string search and regex search are comparable, but indexed search takes the lead by far. This is because rather than parsing through all the documents everytime for each search, we preprocess everything so we can perform instant lookups for certain words in the documents. We are essentially getting all the work done up front rather than doing it over and over for each search request.

## Thoughts on Scaling
Obviously this is a small sample project, and would not work on a large scale. For a scalable solution, we would want to use some form of the indexed search given its performance. We would also need to offload the search index to a database rather than storing it in main memory. Also, I think it'd be worth exploring other ways to structure the data. Rather than checking all documents for a certain term, we could also keep a map from terms to documents. That way we could first get the list of documents that contain a certain term, then use our existing structure to go over those documents and sort by relevance.<br>To handle large request volume, we could also build a cache of results for common search terms to prevent continuously performing the same operation.

## Sample Run
Enter search term: star trek<br>
Enter search method (1-String Match 2-Regex 3-Indexed): 3<br>
<br>
Search Results:<br>
	warp_drive.txt - 1<br>
	hitchhikers.txt - 0<br>
	french_armed_forces.txt - 0<br>
Elapsed time: 0 ms<br>
<br>
Would you like to enter another search term? (y/n): y<br>
Enter search term: in the<br>
Enter search method (1-String Match 2-Regex 3-Indexed): 3<br>
<br>
Search Results:<br>
	french_armed_forces.txt - 15<br>
	hitchhikers.txt - 2<br>
	warp_drive.txt - 1<br>
Elapsed time: 0 ms<br>
<br>
Would you like to enter another search term? (y/n): y<br>
Enter search term: france<br>
Enter search method (1-String Match 2-Regex 3-Indexed): 2<br>
<br>
Search Results:<br>
	french_armed_forces.txt - 18<br>
	hitchhikers.txt - 0<br>
	warp_drive.txt - 0<br>
Elapsed time: 21 ms<br>

