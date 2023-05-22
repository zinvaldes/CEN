import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class TextAnalyzer {
    public static void main(String[] args) {
        // Read the file from the URL
        String fileUrl = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
        String fileContent = readFromFile(fileUrl);

        // Extract the poem content from the file
        String poemContent = grabHTMLContent(fileContent);

        // Count word frequencies
        Map<String, Integer> wordFrequencies = wordCounts(poemContent);

        // Sort word frequencies by descending order
        List<Map.Entry<String, Integer>> sortedWordFrequencies = sortWordCounts(wordFrequencies);

        // Display the top 20 words
        findTopWords(sortedWordFrequencies, 20);
    }
    
/**
 * Reads the content of a file from the given URL.
 *
 * @param fileUrl the URL of the file to read
 * @return the content of the file as a string
 */
private static String readFromFile(String fileUrl) {
    StringBuilder content = new StringBuilder();
    try {
        URL url = new URL(fileUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }

        reader.close();
    } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
    }

    return content.toString();
}
    /**
     * Extracts the content of the poem from the HTML file content.
     *
     * @param fileContent the content of the HTML file
     * @return the content of the poem
     */
    private static String grabHTMLContent(String fileContent) {
        // Define markers to identify the start and end of the poem's content
        String startingPosition = "*** START OF THE PROJECT GUTENBERG EBOOK THE RAVEN ***";
        String endingPosition = "*** END OF THE PROJECT GUTENBERG EBOOK THE RAVEN ***";

        // Find the index where the poem's content begins
        int startingPoint = fileContent.indexOf(startingPosition) + startingPosition.length();

        // Find the index where the poem's content ends
        int endPoint = fileContent.indexOf(endingPosition);

        // Extract the poem's content using the start and end indices,
        // and remove any leading or trailing whitespace using the trim() method
        return fileContent.substring(startingPoint, endPoint).trim();
    }
    
/**
 * Counts the frequencies of each word in the given text.
 *
 * @param text the text to analyze
 * @return a map containing the word frequencies
 */
private static Map<String, Integer> wordCounts(String text) {
    Map<String, Integer> wordCounts = new HashMap<>();

    // Split the text into words
    String[] words = text.split("\\s+");

    // Count word frequencies
    for (String word : words) {
        // Remove non-alphabetic characters and convert to lowercase
        String cleanedWord = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

        // Exclude words that are single characters or empty
        if (cleanedWord.length() <= 1) {
            continue;
        }

        // Exclude specific words that keep showing up in results
        if (cleanedWord.equals("stylemarginleft") ||
                cleanedWord.equals("nevermorespan") ||
                cleanedWord.equals("span") ||
                cleanedWord.equals("doorbr")) {
            continue;
        }

        // Update word frequency count
        int amount = wordCounts.getOrDefault(cleanedWord, 0);
        wordCounts.put(cleanedWord, amount + 1);
    }

    return wordCounts;
}
/**
 * Sorts the word frequencies map by descending order of frequency.
 *
 * @param wordFrequencies the word frequencies map to sort
 * @return a list of word frequencies entries sorted by frequency
 */
private static List<Map.Entry<String, Integer>> sortWordCounts(Map<String, Integer> wordFrequencies) {
    // Convert word frequencies to a list of entries
    List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordFrequencies.entrySet());

    // Sort the list by value in descending order
    entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

    return entries;
}

/**
 * Displays the top words and their frequencies.
 *
 * @param wordFrequencies the list of word frequencies entries
 * @param numWords        the number of top words to display
 */
private static void findTopWords(List<Map.Entry<String, Integer>> wordFrequencies, int numWords) {
    System.out.println("The top " + numWords + " words in the poem are -");

    // Display the top words
    for (int i = 0; i < numWords && i < wordFrequencies.size(); i++) {
        Map.Entry<String, Integer> entry = wordFrequencies.get(i);
        System.out.println(entry.getKey() +  ": " + entry.getValue());
    }
}
}
