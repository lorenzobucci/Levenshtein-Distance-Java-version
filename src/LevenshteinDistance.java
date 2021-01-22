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

    static short parallelAlgorithm(String str1, String str2) throws InterruptedException {

        short subMatrixSize = 500;

        short[][] d = new short[str1.length() + 1][str2.length() + 1];
        CountDownLatch[][] latches = new CountDownLatch[(str1.length() / subMatrixSize) + 1][(str2.length() / subMatrixSize) + 1];

        for (short i1 = 0; i1 <= str1.length(); i1++)
            d[i1][0] = i1;
        for (short i2 = 0; i2 <= str2.length(); i2++)
            d[0][i2] = i2;
        for (short j = 0; j < latches[0].length; j++)
            latches[0][j] = new CountDownLatch(0);
        for (short i = 1; i < latches.length; i++) {
            latches[i][0] = new CountDownLatch(0);
            for (short j = 1; j < latches[0].length; j++)
                latches[i][j] = new CountDownLatch(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int diag = 1; diag <= ((str1.length() / subMatrixSize) + (str2.length() / subMatrixSize) - 1); diag++) {
            int start_col = Math.max(1, diag - (str1.length() / subMatrixSize) + 1);
            int count = Math.min(diag, Math.min(((str2.length() / subMatrixSize) - start_col + 1), (str1.length() / subMatrixSize)));
            for (int j = 0; j < count; j++) {
                int subMatrixColId = start_col + j;
                int subMatrixRowId = Math.min((str1.length() / subMatrixSize), diag) - j;
                int startCol = (subMatrixColId * subMatrixSize) - subMatrixSize + 1;
                int startRow = ((subMatrixRowId * subMatrixSize) - subMatrixSize + 1);
                int endCol = startCol + subMatrixSize;
                int endRow = startRow + subMatrixSize;

                if (str2.length() - endCol < subMatrixSize)
                    endCol = str2.length();
                if (str1.length() - endRow < subMatrixSize)
                    endRow = str1.length();

                executor.execute(new SubMatrixCalculator(d, latches, startRow, startCol, endRow, endCol, subMatrixRowId, subMatrixColId, str1, str2));

            }
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        return d[str1.length()][str2.length()];
    }
}
