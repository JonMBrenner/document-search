import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/*
 * This class maintains a map where each key is a unique identifer for a given
 * document & word combination, and the value is a set of the indices that particular
 * word appears in the document.
 */
public class SearchIndex {
  
  private Map<String, Set<Integer>> documentWordIndices = new HashMap<>();

  /*
   * Since we can assume document names are unique, simply joining the name and word
   * with a dash will guarantee uniqueness. Although, in a larger system we would likely use
   * some kind of document ID.
   */
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

