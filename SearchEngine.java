import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class SearchEngine {

  private static final int MODE_STRING  = 1;
  private static final int MODE_REGEX   = 2;
  private static final int MODE_INDEXED = 3;
  private Map<String, String> documentMap;

  public SearchEngine() {
    loadDocuments();
  }

  private void loadDocuments() {
    documentMap = new HashMap<>();
    try (Stream<Path> docPaths = Files.walk(Paths.get("./documents/"))) {
      docPaths.filter(Files::isRegularFile).forEach(this::loadDocument);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void loadDocument(Path path) {
    try {
      documentMap.put(path.getFileName().toString(), Files.readString(path));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void search(String searchTerm, int mode) {
    Map<String, Integer> documentMatchCounts = getEmptyDocumentMatchCountsMap();
    searchForMatches(documentMatchCounts, searchTerm, mode);
    displayResults(documentMatchCounts);
  }

  private void searchForMatches(Map<String, Integer> matchCounts, String searchTerm, int mode) {

  }

  private void displayResults(Map<String, Integer> matchCounts) {

  }

  private Map<String, Integer> getEmptyDocumentMatchCountsMap() {
    Map<String, Integer> documentMatchCounts = new HashMap<>();
    documentMap.keySet().forEach(docName -> documentMatchCounts.put(docName, 0));
    return documentMatchCounts;
  }

}
