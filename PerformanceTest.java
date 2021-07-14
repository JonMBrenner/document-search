import org.apache.commons.lang3.RandomStringUtils;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.Duration;

public class PerformanceTest {

  /*
   * Produces a list of random alpha strings with length ranges from 2 to 6
   */
  private static List<String> getRandomWords(int numWords) {
    List<String> words = new ArrayList<>(numWords);
    for (int i = 0; i < numWords; i++) {
      String word = RandomStringUtils.randomAlphabetic(i % 5 + 2);
      words.add(i, word);
    }
    return words;
  }

  public static void main(String[] args) throws Exception {
    List<String> words = getRandomWords(2000000);
    SearchEngine searchEngine = new SearchEngine();
    for (int mode = 1; mode <= 3; mode++) {
      Instant start = Instant.now();
      for (String word : words) {
        searchEngine.search(word, mode, false);
      }
      Instant finish = Instant.now();
      long totalDuration = Duration.between(start, finish).toMillis();
      System.out.println("Search Mode: " + mode + " - " + totalDuration + " ms");
    }
  }

}
