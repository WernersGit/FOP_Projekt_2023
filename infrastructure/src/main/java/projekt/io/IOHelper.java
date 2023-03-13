package projekt.io;

import projekt.delivery.archetype.ProblemArchetype;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * A static helper class for handling io operations containing {@link ProblemArchetype}s.
 */
public class IOHelper {

    public static final File PROBLEMS_DIR = Path.of(System.getProperty("user.dir"), "projekt", "gui", "problems").toFile();

    /**
     * Copies the {@link ProblemArchetype} presets from the resource directory into the build directory.
     */
    public static void initProblemPresets() {
        copyProblemFiles("problem1", "problem2", "problem3", "problem4");
    }

    /**
     * Copies the {@link ProblemArchetype} preset with the given names from the resource directory into the build directory.
     */
    private static void copyProblemFiles(String... fileNames) {
        for (String fileName : fileNames) {
            try (InputStream problem = IOHelper.class.getResourceAsStream("presets/" + fileName + ".txt");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(problem)))) {
                writeProblem(ProblemArchetypeIO.readProblemArchetype(reader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns all {@link ProblemArchetype}s stored in the resource and the build directory.
     *
     * @return All {@link ProblemArchetype}s stored in the resource and the build directory.
     * @see #getAllProblemsInBuildDir()
     */
    public static List<ProblemArchetype> readProblems() {
        return readProblemsInFiles(getAllProblemsInBuildDir());
    }

    /**
     * Returns a {@link Set} of {@link File}s containing all {@link ProblemArchetype}s stored in the build dir (build/run/projekt/gui/problems).
     *
     * @return A {@link Set} of {@link File}s containing all {@link ProblemArchetype}s stored in the build dir.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Set<File> getAllProblemsInBuildDir() {
        File dir = new File(Path.of(System.getProperty("user.dir"), "projekt", "gui", "problems").toUri());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new HashSet<>(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
    }

    private static List<ProblemArchetype> readProblemsInFiles(Set<File> files) {
        List<ProblemArchetype> problems = new ArrayList<>();

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                problems.add(ProblemArchetypeIO.readProblemArchetype(reader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return problems;
    }

    /**
     * Writes the given {@link ProblemArchetype} into a text file in the build directory (build/run/projekt/gui/problems).<p>
     * <p>
     * The name of the file will be the name of the {@link ProblemArchetype}. The content of the file will be
     * produced by the {@link ProblemArchetypeIO#writeProblemArchetype(BufferedWriter, ProblemArchetype) method.
     * <p>
     *
     * @param problem The {@link ProblemArchetype} to write into the file.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeProblem(ProblemArchetype problem) {
        if (!PROBLEMS_DIR.exists()) {
            PROBLEMS_DIR.mkdirs();
        }

        File file = Path.of(PROBLEMS_DIR.getPath(), problem.name() + ".txt").toFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            file.createNewFile();
            ProblemArchetypeIO.writeProblemArchetype(writer, problem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
