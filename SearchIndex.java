import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class SearchIndex {
  
  private Map<String, Set<Integer>> documentWordIndices;

  public SearchIndex() {
    documentWordIndices = new HashMap<>();
  }

  private static String getDocumentWordKey(String documentName, String word) {
    return documentName + "-" + word;
  }

  public void addDocument(String documentName, String[] words) {
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      String key = getDocumentWordKey(documentName, word);
      documentWordIndices.computeIfAbsent(key, k -> new HashSet<>()).add(i);
    }
  }

  public Set<Integer> getWordIndices(String documentName, String word) {
    String key = getDocumentWordKey(documentName, word);
    Set<Integer> wordIndices = documentWordIndices.get(key);
    return wordIndices != null ? wordIndices : new HashSet<>();
  }

}

