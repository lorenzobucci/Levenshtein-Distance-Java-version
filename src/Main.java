import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 1000;
        Random random = new Random();

        String a = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String b = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();


        long startTime = System.nanoTime();
        System.out.println(LevenshteinDistance.parallelAlgorithm(a, b));
        long endTime = System.nanoTime();
        long timeElapsed = (endTime - startTime) / 1000000 ;
        System.out.println("Execution time in ms : " + timeElapsed );

        startTime = System.nanoTime();
        System.out.println(LevenshteinDistance.sequentialAlgorithm(a, b));
        endTime = System.nanoTime();
        timeElapsed = (endTime - startTime) / 1000000 ;
        System.out.println("Execution time in ms : " + timeElapsed );
    }

}
