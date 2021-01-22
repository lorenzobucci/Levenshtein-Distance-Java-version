import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

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

    static short parallelAlgorithm(String str1, String str2) throws ExecutionException, InterruptedException {
        ArrayList<Future<Short>> d = new ArrayList<Future<Short>>(str1.length() * str2.length());

        for (int i = 0; i < str1.length() * str2.length(); i++)
            d.add(null);

        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(6);

        for (int diag = 1; diag <= (str1.length() + str2.length() - 1); diag++) {
            int start_col = Math.max(0, diag - str1.length());
            int count = Math.min(diag, Math.min((str2.length() - start_col), str1.length()));
            for (int j = 0; j < count; j++) {
                int row = Math.min(str1.length(), diag) - j - 1;
                int col = start_col + j;
                d.set(row * str2.length() + col, executor.submit(new DistanceCalculatorTask(row, col, str1, str2, d)));
            }
        }

        executor.shutdown();
        return d.get(d.size() - 1).get();
    }
}
