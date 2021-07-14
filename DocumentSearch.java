import java.util.Scanner;
import java.util.Set;
import java.io.IOException;

/*
 * DocumentSearch acts as a command line interface for using the search engine.
 */
public class DocumentSearch {

  private static final Set<String> VALID_MODES = Set.of("1", "2", "3");

  private static String getSearchTerm(Scanner scanner) {
    System.out.print("Enter search term: ");
    String searchTerm = scanner.nextLine();
    return SearchEngine.normalizeText(searchTerm);
  }

  private static int getSearchMode(Scanner scanner) {
    String prompt = "Enter search method (1-String Match 2-Regex 3-Indexed): ";
    System.out.print(prompt);
    String mode = scanner.nextLine();
    while (!VALID_MODES.contains(mode)) {
      System.out.println("Invalid input. Please enter 1, 2, or 3.");
      System.out.print(prompt);
      mode = scanner.nextLine();
    }
    return Integer.parseInt(mode);
  }

  private static boolean promptForExit(Scanner scanner) {
    System.out.print("Would you like to enter another search term? (y/n): ");
    String response = scanner.nextLine();
    return !"y".equalsIgnoreCase(response);
  }

  public static void main(String[] args) throws IOException {
    SearchEngine searchEngine = new SearchEngine();
    Scanner scanner = new Scanner(System.in);
    boolean exit = false;

    while (!exit) {
      String searchTerm = getSearchTerm(scanner);
      int mode = getSearchMode(scanner);
      searchEngine.search(searchTerm, mode);
      exit = promptForExit(scanner);
    }
  }

}
