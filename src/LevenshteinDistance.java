import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LevenshteinDistance {

    static short sequentialAlgorithm(String str1, String str2) {
        short[][] d = new short[str1.length() + 1][str2.length() + 1];

        short cost;

        for (short i1 = 0; i1 <= str1.length(); i1++)
            d[i1][0] = i1;
        for (short i2 = 0; i2 <= str2.length(); i2++)
            d[0][i2] = i2;

        for (short i1 = 1; i1 <= str1.length(); i1++) {
            for (short i2 = 1; i2 <= str2.length(); i2++) {
                if (str1.charAt(i1 - 1) == str2.charAt(i2 - 1))
                    cost = 0;
                else
                    cost = 1;
                d[i1][i2] = (short) Math.min(d[i1 - 1][i2] + 1, Math.min(d[i1][i2 - 1] + 1, d[i1 - 1][i2 - 1] + cost));
            }
        }

        return d[str1.length()][str2.length()];
    }

    static int parallelAlgorithm(String str1, String str2) throws InterruptedException {
        short[][] d = new short[str1.length() + 1][str2.length() + 1];
        CountDownLatch[][] latches = new CountDownLatch[str1.length() + 1][str2.length() + 1];

        int cores = Runtime.getRuntime().availableProcessors();

        long startTime = System.nanoTime();


        for (short i1 = 0; i1 <= str1.length(); i1++) {
            for (short i2 = 1; i2 <= str2.length(); i2++)
                latches[i1][i2] = new CountDownLatch(1);
            d[i1][0] = i1;
            latches[i1][0] = new CountDownLatch(0);
        }
        for (short i2 = 0; i2 <= str2.length(); i2++) {
            d[0][i2] = i2;
            latches[0][i2] = new CountDownLatch(0);
        }

        long endTime = System.nanoTime();
        long timeElapsed = (endTime - startTime) / 1000000 ;
        System.out.println("Execution time in ms : " + timeElapsed );

        ExecutorService executor = Executors.newFixedThreadPool(6);

        for (int diag = 1; diag <= (str1.length() + str2.length() - 1); diag++) {
            int start_col = Math.max(1, diag - str1.length() + 1);
            int count = Math.min(diag, Math.min((str2.length() - start_col + 1), str1.length()));
            for (int j = 0; j < count; j++)
                executor.execute(new DistanceCalculatorTask(Math.min(str1.length(), diag) - j, start_col + j, str1, str2, d, latches));
        }


        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        return d[str1.length()][str2.length()];
    }
}
