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

  private Map<String, String>        documentMap;
  private Map<String, Set<Integer>>  searchIndex;

  public SearchEngine() throws IOException {
    loadDocuments();
    buildSearchIndex();
  }

  private void loadDocuments() throws IOException {
    documentMap = new HashMap<>();
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
    searchIndex = new HashMap<>();
    for (Map.Entry<String, String> document : documentMap.entrySet()) {
      String documentName = document.getKey();
      String[] documentWords = document.getValue().split(" ", 0);
      addWordsToIndex(documentName, documentWords);
    }
  }

  private void addWordsToIndex(String documentName, String[] words) {
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      String key = getSearchIndexKey(documentName, word);
      searchIndex.computeIfAbsent(key, k -> new HashSet<>()).add(i);
    }
  }

  private String getSearchIndexKey(String documentName, String word) {
    return documentName + "-" + word;
  }

  private Set<Integer> getWordOccurenceIndices(String documentName, String word) {
    String key = getSearchIndexKey(documentName, word);
    Set<Integer> wordOccurenceIndices = searchIndex.get(key);
    return wordOccurenceIndices != null ? wordOccurenceIndices : new HashSet<>();
  }

  public static String normalizeText(String text) {
    text = text.toLowerCase();
    text = text.replaceAll("[’']s", "");
    text = text.replaceAll("[’']", "");
    text = text.replaceAll("[^a-z0-9]+", " ");
    text = text.replaceAll("\\s+", " ");
    return text;
  }

  public void search(String searchTerm, int mode) {
    Map<String, Integer> documentMatchCounts = getEmptyDocumentMatchCountsMap();

    Instant start = Instant.now();
    searchDocuments(documentMatchCounts, searchTerm, mode);
    Instant finish = Instant.now();

    long searchTime = Duration.between(start, finish).toMillis();
    displaySearchResults(documentMatchCounts, searchTime);
  }

  private void searchDocuments(Map<String, Integer> matchCounts, String searchTerm, int mode) {
    for (Map.Entry<String, String> document : documentMap.entrySet()) {
      String documentName = document.getKey();
      int matchCount = getDocumentMatchCount(documentName, searchTerm, mode);
      matchCounts.put(documentName, matchCount);
    }
  }

  private int getDocumentMatchCount(String documentName, String searchTerm, int mode) {
    int matchCount;
    if (mode == MODE_STRING) {
      matchCount = stringSearch(documentName, searchTerm);
    } else if (mode == MODE_REGEX) {
      matchCount = regexSearch(documentName, searchTerm);
    } else if (mode == MODE_INDEXED) {
      matchCount = indexedSearch(documentName, searchTerm);
    } else {
      throw new RuntimeException("Unknown search mode: " + mode);
    }

    return matchCount;
  }

  private int stringSearch(String documentName, String searchTerm) {
    String text = documentMap.get(documentName);
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

  private int indexedSearch(String documentName, String searchTerm) {
    String[] searchTermWords = searchTerm.split(" ", 0);
    Set<Integer> firstWordIndices = getWordOccurenceIndices(documentName, searchTermWords[0]);
    int matches = 0;
    for (Integer firstWordIndex : firstWordIndices) {
      if (fullMatch(documentName, searchTermWords, firstWordIndex)) {
        matches++;
      }
    }
    return matches;
  }

  private boolean fullMatch(String documentName, String[] searchTermWords, int firstWordIndex) {
    for (int i = 1; i < searchTermWords.length; i++) {
      Set<Integer> curWordIndices = getWordOccurenceIndices(documentName, searchTermWords[i]);
      if (!curWordIndices.contains(firstWordIndex + i)) {
        return false;
      }
    }
    return true;
  }

  private void displaySearchResults(Map<String, Integer> matchCounts, long searchTime) {
    StringBuilder results = new StringBuilder();
    results.append("\nSearch Results:\n");

    matchCounts.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
      .forEachOrdered(e -> results.append(String.format("\t%s - %d\n", e.getKey(), e.getValue())));

    results.append(String.format("Elapsed time: %d ms", searchTime)).append("\n");
    System.out.println(results.toString());
  }

  private Map<String, Integer> getEmptyDocumentMatchCountsMap() {
    Map<String, Integer> documentMatchCounts = new HashMap<>();
    documentMap.keySet().forEach(docName -> documentMatchCounts.put(docName, 0));
    return documentMatchCounts;
  }

}