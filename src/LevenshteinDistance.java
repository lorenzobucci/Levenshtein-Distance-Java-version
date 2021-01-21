import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LevenshteinDistance {

    static int sequentialAlgorithm(String str1, String str2) {
        int[][] d = new int[str1.length() + 1][str2.length() + 1];

        int cost;

        for (int i1 = 0; i1 <= str1.length(); i1++)
            d[i1][0] = i1;
        for (int i2 = 0; i2 <= str2.length(); i2++)
            d[0][i2] = i2;

        for (int i1 = 1; i1 <= str1.length(); i1++) {
            for (int i2 = 1; i2 <= str2.length(); i2++) {
                if (str1.charAt(i1 - 1) == str2.charAt(i2 - 1))
                    cost = 0;
                else
                    cost = 1;
                d[i1][i2] = Math.min(d[i1 - 1][i2] + 1, Math.min(d[i1][i2 - 1] + 1, d[i1 - 1][i2 - 1] + cost));
            }
        }

        return d[str1.length()][str2.length()];
    }

    static int parallelAlgorithm(String str1, String str2) throws InterruptedException {
        int[][] d = new int[str1.length() + 1][str2.length() + 1];
        CountDownLatch[][] latches = new CountDownLatch[str1.length() + 1][str2.length() + 1];

        int cores = Runtime.getRuntime().availableProcessors();

        for (int i1 = 0; i1 <= str1.length(); i1++) {
            for (int i2 = 1; i2 <= str2.length(); i2++)
                latches[i1][i2] = new CountDownLatch(1);
            d[i1][0] = i1;
            latches[i1][0] = new CountDownLatch(0);
        }
        for (int i2 = 0; i2 <= str2.length(); i2++) {
            d[0][i2] = i2;
            latches[0][i2] = new CountDownLatch(0);
        }

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
