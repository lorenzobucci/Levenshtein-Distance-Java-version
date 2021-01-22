import java.util.concurrent.CountDownLatch;

public class SubMatrixCalculator implements Runnable {

    private final short[][] d;
    private final CountDownLatch[][] latches;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;
    private final int subMatrixRowId;
    private final int subMatrixColId;
    private final String str1;
    private final String str2;

    public SubMatrixCalculator(short[][] d, CountDownLatch[][] latches, int startRow, int startCol, int endRow, int endCol, int subMatrixRowId, int subMatrixColId, String str1, String str2) {
        this.d = d;
        this.latches = latches;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.subMatrixRowId = subMatrixRowId;
        this.subMatrixColId = subMatrixColId;
        this.str1 = str1;
        this.str2 = str2;
    }

    @Override
    public void run() {
        try {
            latches[subMatrixRowId - 1][subMatrixColId].await();
            latches[subMatrixRowId][subMatrixColId - 1].await();
            latches[subMatrixRowId - 1][subMatrixColId - 1].await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                int cost;
                if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    cost = 0;
                else
                    cost = 1;
                d[i][j] = (short) Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + cost));
            }
        }

        latches[subMatrixRowId][subMatrixColId].countDown();
    }
}
