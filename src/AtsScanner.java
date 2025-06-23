import java.util.*;
import java.util.stream.Collectors;

public class AtsScanner {

    // Common filler words to ignore
    private static final Set<String> IGNORED_WORDS = Set.of(
            "the", "and", "or", "with", "a", "an", "to", "for", "in", "on", "of", "at", "by", "from",
            "is", "are", "as", "our", "we", "this", "that", "be", "will", "their", "they", "it",
            "you", "your", "i", "my", "me", "us", "but", "if", "so", "do", "does", "did"
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter JD text (end input with empty line):");
        String jdText = readMultiline(scanner).toLowerCase();

        System.out.println("\nEnter Resume text (end input with empty line):");
        String resumeText = readMultiline(scanner).toLowerCase();

        // Clean texts: remove multiple spaces
        jdText = jdText.replaceAll("\\s+", " ");
        resumeText = resumeText.replaceAll("\\s+", " ");

        // Extract words for JD excluding filler words
        List<String> jdWords = Arrays.stream(jdText.split("\\W+"))
                .filter(w -> !w.isBlank())
                .filter(w -> !IGNORED_WORDS.contains(w))
                .collect(Collectors.toList());

        // Extract resume words set
        Set<String> resumeWords = Arrays.stream(resumeText.split("\\W+"))
                .filter(w -> !w.isBlank())
                .collect(Collectors.toSet());

        // Check missing single keywords from JD
        Set<String> missingKeywords = jdWords.stream()
                .filter(word -> !resumeWords.contains(word))
                .collect(Collectors.toSet());

        // Instead of checking phrases as substrings, check phrase words presence individually
        Set<String> jdPhrases = new LinkedHashSet<>();
        for (int i = 0; i < jdWords.size(); i++) {
            // 2-word phrases
            if (i + 1 < jdWords.size()) {
                jdPhrases.add(jdWords.get(i) + " " + jdWords.get(i + 1));
            }
            // 3-word phrases
            if (i + 2 < jdWords.size()) {
                jdPhrases.add(jdWords.get(i) + " " + jdWords.get(i + 1) + " " + jdWords.get(i + 2));
            }
        }

        // Collect phrases missing if *any* word of the phrase missing in resume
        Set<String> missingPhrases = jdPhrases.stream()
                .filter(phrase -> {
                    String[] words = phrase.split(" ");
                    for (String w : words) {
                        if (!resumeWords.contains(w)) {
                            return true; // missing some word in phrase
                        }
                    }
                    return false; // all words present, so phrase considered present
                })
                .collect(Collectors.toSet());

        // Remove from missingKeywords any word that is missing only because phrase missing (to avoid duplicates)
        for (String phrase : missingPhrases) {
            String[] parts = phrase.split(" ");
            for (String p : parts) {
                missingKeywords.remove(p);
            }
        }

        // Output results
        if (missingKeywords.isEmpty() && missingPhrases.isEmpty()) {
            System.out.println("\n✔ All key JD concepts are present in the resume!");
        } else {
            System.out.println("\nMissing JD keywords not found in Resume:");
            missingKeywords.forEach(word -> System.out.println("✘ " + word));
            missingPhrases.forEach(phrase -> System.out.println("✘ " + phrase));
        }
    }

    private static String readMultiline(Scanner scanner) {
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isBlank()) {
            sb.append(line).append(" ");
        }
        return sb.toString().trim();
    }
}
