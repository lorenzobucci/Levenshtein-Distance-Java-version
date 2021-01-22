import java.util.Random;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int stringLengthA = 32041;
        int stringLengthB = 30137;
        Random random = new Random();

        String a = random.ints(leftLimit, rightLimit + 1)
                .limit(stringLengthA)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String b = random.ints(leftLimit, rightLimit + 1)
                .limit(stringLengthB)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        long startTime = System.nanoTime();
        short sequentialResult = LevenshteinDistance.sequentialAlgorithm(a, b);
        long endTime = System.nanoTime();
        long timeElapsed = (endTime - startTime) / 1000000;
        System.out.println("Sequential edit distance result: " + sequentialResult);
        System.out.println("Sequential execution time in ms : " + timeElapsed);


        for (int i = 10; i <= 1000; i+=100) {
            startTime = System.nanoTime();
            short parallelResult = LevenshteinDistance.parallelAlgorithm(a, b, (short) i);
            endTime = System.nanoTime();
            timeElapsed = (endTime - startTime) / 1000000;
            System.out.println();
            System.out.println("Parallel edit distance result: " + parallelResult);
            System.out.println("Parallel execution time in ms: " + timeElapsed + " with sub-matrices size: " + i);
        }


    }

}
