import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SearchTest {
  
  private SearchEngine searchEngine;

  public SearchTest() throws Exception {
    searchEngine = new SearchEngine();
  }

  @Test
  void stringSearch() {
    assertEquals(6, searchEngine.search("the", 1).get("warp_drive.txt"));
    assertEquals(29, searchEngine.search("the", 1).get("hitchhikers.txt"));
    assertEquals(64, searchEngine.search("the", 1).get("french_armed_forces.txt"));
  }

  @Test
  void regexSearch() {
    assertEquals(6, searchEngine.search("the", 2).get("warp_drive.txt"));
    assertEquals(29, searchEngine.search("the", 2).get("hitchhikers.txt"));
    assertEquals(64, searchEngine.search("the", 2).get("french_armed_forces.txt"));
  }

  @Test
  void indexedSearch() {
    assertEquals(6, searchEngine.search("the", 3).get("warp_drive.txt"));
    assertEquals(29, searchEngine.search("the", 3).get("hitchhikers.txt"));
    assertEquals(64, searchEngine.search("the", 3).get("french_armed_forces.txt"));
  }

  @Test
  void stringSearchMultiWord() {
    assertEquals(1, searchEngine.search("in the", 1).get("warp_drive.txt"));
    assertEquals(2, searchEngine.search("in the", 1).get("hitchhikers.txt"));
    assertEquals(15, searchEngine.search("in the", 1).get("french_armed_forces.txt"));
  }

  @Test
  void regexSearchMultiWord() {
    assertEquals(1, searchEngine.search("in the", 2).get("warp_drive.txt"));
    assertEquals(2, searchEngine.search("in the", 2).get("hitchhikers.txt"));
    assertEquals(15, searchEngine.search("in the", 2).get("french_armed_forces.txt"));
  }

  @Test
  void IndexedSearchMultiWord() {
    assertEquals(1, searchEngine.search("in the", 3).get("warp_drive.txt"));
    assertEquals(2, searchEngine.search("in the", 3).get("hitchhikers.txt"));
    assertEquals(15, searchEngine.search("in the", 3).get("french_armed_forces.txt"));
  }

  @Test
  void nonOcurringWord() {
    assertEquals(0, searchEngine.search("nonocurringword", 1).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 2).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 3).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 1).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 2).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 3).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 1).get("french_armed_forces.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 2).get("french_armed_forces.txt"));
    assertEquals(0, searchEngine.search("nonocurringword", 3).get("french_armed_forces.txt"));
  }

  @Test
  void wordsAdjacentToPuncuation() {
    assertEquals(2, searchEngine.search("FTL", 1).get("warp_drive.txt"));
    assertEquals(2, searchEngine.search("FTL", 2).get("warp_drive.txt"));
    assertEquals(2, searchEngine.search("FTL", 3).get("warp_drive.txt"));
    assertEquals(1, searchEngine.search("faster than light", 1).get("warp_drive.txt"));
    assertEquals(1, searchEngine.search("faster than light", 2).get("warp_drive.txt"));
    assertEquals(1, searchEngine.search("faster than light", 3).get("warp_drive.txt"));
  }

  @Test
  void wordsAtStartOrEndOfFile() {
    assertEquals(6, searchEngine.search("warp", 1).get("warp_drive.txt"));
    assertEquals(6, searchEngine.search("warp", 2).get("warp_drive.txt"));
    assertEquals(6, searchEngine.search("warp", 3).get("warp_drive.txt"));
    assertEquals(3, searchEngine.search("2005", 1).get("hitchhikers.txt"));
    assertEquals(3, searchEngine.search("2005", 2).get("hitchhikers.txt"));
    assertEquals(3, searchEngine.search("2005", 3).get("hitchhikers.txt"));
  }

  @Test
  void garbageSearchTerms() {
    assertEquals(0, searchEngine.search("", 1).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search("", 2).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search("", 3).get("warp_drive.txt"));
    assertEquals(0, searchEngine.search(" ", 1).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search(" ", 2).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search(" ", 3).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("* ", 1).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("* ", 2).get("hitchhikers.txt"));
    assertEquals(0, searchEngine.search("* ", 3).get("hitchhikers.txt"));
  }

}
