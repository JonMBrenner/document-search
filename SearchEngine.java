import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.Instant;
import java.time.Duration;

public class SearchEngine {

  private static final int MODE_STRING  = 1;
  private static final int MODE_REGEX   = 2;
  private static final int MODE_INDEXED = 3;

  /*
   * A map from each document name to its normalized text contents
   */
  private Map<String, String> documentMap = new HashMap<>();
  private SearchIndex         searchIndex = new SearchIndex();

  public SearchEngine() throws IOException {
    loadDocuments();
    buildSearchIndex();
  }

  private void loadDocuments() throws IOException {
    try (Stream<Path> docPaths = Files.walk(Paths.get("./documents/"))) {
      docPaths.filter(Files::isRegularFile).forEach(this::loadDocument);
    }
  }

  private void loadDocument(Path path) {
    try {
      String filename = path.getFileName().toString();
      String fileContents = normalizeText(Files.readString(path));
      documentMap.put(filename, fileContents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void buildSearchIndex() {
    for (Map.Entry<String, String> document : documentMap.entrySet()) {
      String documentName = document.getKey();
      String[] documentWords = document.getValue().split(" ", 0);
      searchIndex.addDocument(documentName, documentWords);
    }
  }

  public Map<String, Integer> search(String searchTerm, int mode) {
    return search(searchTerm, mode, true);
  }

  public Map<String, Integer> search(String searchTerm, int mode, boolean displayResults) {
    searchTerm = normalizeText(searchTerm);
    Map<String, Integer> documentMatchCounts = getEmptyDocumentMatchCountsMap();
    if ("".equals(searchTerm) || " ".equals(searchTerm)) {
      return documentMatchCounts;
    }

    Instant start = Instant.now();
    searchDocuments(documentMatchCounts, searchTerm, mode);
    Instant finish = Instant.now();

    long searchTime = Duration.between(start, finish).toMillis();
    if (displayResults) {
      displaySearchResults(documentMatchCounts, searchTime);
    }

    return documentMatchCounts;
  }

  /*
   * Populates matchCounts with the number of matches for each document name
   */
  private void searchDocuments(Map<String, Integer> matchCounts, String searchTerm, int mode) {
    for (Map.Entry<String, String> document : documentMap.entrySet()) {
      String documentName = document.getKey();
      int matchCount = getDocumentMatchCount(documentName, searchTerm, mode);
      matchCounts.put(documentName, matchCount);
    }
  }

  private int getDocumentMatchCount(String documentName, String searchTerm, int mode) {
    if (mode == MODE_STRING) {
      return stringSearch(documentName, searchTerm);
    } else if (mode == MODE_REGEX) {
      return regexSearch(documentName, searchTerm);
    } else if (mode == MODE_INDEXED) {
      return indexedSearch(documentName, searchTerm);
    } else {
      throw new RuntimeException("Unknown search mode: " + mode);
    }
  }

  private int stringSearch(String documentName, String searchTerm) {
    String text = documentMap.get(documentName);

    // Add spaces around text and search term to allow matches at the begining and end of file
    text = " " + text + " ";
    searchTerm = " " + searchTerm + " ";

    int matches = 0;
    for (int start = 0, end = searchTerm.length(); end <= text.length(); start++, end++) {
      if (text.substring(start, end).equals(searchTerm)) {
        matches++;
      }
    }
    return matches;
  }

  private int regexSearch(String documentName, String searchTerm) {
    String text = documentMap.get(documentName);
    Pattern pattern = Pattern.compile("\\b" + searchTerm + "\\b");
    Matcher matcher = pattern.matcher(text);
    return (int) matcher.results().count();
  }

  /*
   * The search index only stores indices of single words in each document. To handle multi-word
   * search terms, this method starts by getting the indices for the first word of the search term.
   * It then iterates over each occurence of the first word and checks for a full match.
   */
  private int indexedSearch(String documentName, String searchTerm) {
    String[] searchTermWords = searchTerm.split(" ", 0);
    Set<Integer> firstWordIndices = searchIndex.getWordIndices(documentName, searchTermWords[0]);
    int matches = 0;
    for (Integer firstWordIndex : firstWordIndices) {
      if (fullMatch(documentName, searchTermWords, firstWordIndex)) {
        matches++;
      }
    }
    return matches;
  }

  private boolean fullMatch(String documentName, String[] searchTermWords, int startingIndex) {
    for (int i = 1; i < searchTermWords.length; i++) {
      Set<Integer> curWordIndices = searchIndex.getWordIndices(documentName, searchTermWords[i]);
      if (!curWordIndices.contains(startingIndex + i)) {
        return false;
      }
    }
    return true;
  }

  private static void displaySearchResults(Map<String, Integer> matchCounts, long searchTime) {
    StringBuilder results = new StringBuilder();
    results.append("\nSearch Results:\n");

    matchCounts.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
      .forEachOrdered(e -> results.append(String.format("\t%s - %d\n", e.getKey(), e.getValue())));

    results.append(String.format("Elapsed time: %d ms", searchTime)).append("\n");
    System.out.println(results.toString());
  }

  /*
   * This method is used to normalize both search terms, and document contents.
   * To allow for simple search operations, we convert everything to a string of lowercase
   * words separated by single spaces. All non-alphanumeric characters are removed.
   *
   * We also remove any occurences of 's to allow matching on possesive nouns.
   * Ex: A search for "day" will still match on occurences of "day's"
   */
  public static String normalizeText(String text) {
    text = text.toLowerCase();
    text = text.replaceAll("[???']s", "");
    text = text.replaceAll("[???']", "");
    text = text.replaceAll("[^a-z0-9]+", " ");
    text = text.replaceAll("\\s+", " ");
    return text;
  }

  private Map<String, Integer> getEmptyDocumentMatchCountsMap() {
    Map<String, Integer> documentMatchCounts = new HashMap<>();
    documentMap.keySet().forEach(docName -> documentMatchCounts.put(docName, 0));
    return documentMatchCounts;
  }

}
