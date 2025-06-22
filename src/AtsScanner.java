import java.util.*;
import java.util.stream.Collectors;

public class AtsScanner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the Job Description text (with bullets starting '-', '*' - end input with empty line):");
        String jobDesc = readMultilineInput(scanner);

        System.out.println("\nEnter the Resume text (end input with empty line):");
        String resume = readMultilineInput(scanner);

        String resumeLower = resume.toLowerCase();

        // Extract bullet lines, allowing optional leading whitespace before bullet
        List<String> jdLines = Arrays.stream(jobDesc.split("\\r?\\n"))
                .filter(line -> line.matches("^\\s*[-*].*"))
                .map(String::trim)
                .collect(Collectors.toList());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String bullet : jdLines) {
            // Remove bullet char (- or *) and trim
            String cleanBullet = bullet.substring(1).trim().toLowerCase();

            // Split bullet into words
            String[] words = cleanBullet.split("\\W+");
            long foundCount = Arrays.stream(words).filter(word -> resumeLower.contains(word)).count();
            double matchRatio = words.length == 0 ? 0 : (double) foundCount / words.length;

            if (matchRatio > 0.6) {
                matched.add(bullet);
            } else {
                missing.add(bullet);
            }
        }

        System.out.println("\nMatched JD Requirements:");
        if (matched.isEmpty()) {
            System.out.println("  None");
        } else {
            matched.forEach(b -> System.out.println("✔ " + b));
        }

        System.out.println("\nMissing JD Requirements (consider adding):");
        if (missing.isEmpty()) {
            System.out.println("  None");
        } else {
            missing.forEach(b -> System.out.println("✘ " + b));
        }
    }

    private static String readMultilineInput(Scanner scanner) {
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isBlank()) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }
}
