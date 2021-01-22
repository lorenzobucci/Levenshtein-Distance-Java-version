import java.util.concurrent.CountDownLatch;

public class DistanceCalculatorTask implements Runnable{

    private final int myRow;
    private final int myCol;
    private final String str1;
    private final String str2;
    private final short[][] d;
    private final CountDownLatch[][] latches;

    public DistanceCalculatorTask(int myRow, int myCol, String str1, String str2, short[][] d, CountDownLatch[][] latches) {
        this.myRow = myRow;
        this.myCol = myCol;
        this.str1 = str1;
        this.str2 = str2;
        this.d = d;
        this.latches = latches;
    }

    @Override
    public void run() {
        try {
            latches[myRow - 1][myCol].await();
            latches[myRow][myCol - 1].await();
            latches[myRow - 1][myCol - 1].await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        short cost;
        if (str1.charAt(myRow - 1) == str2.charAt(myCol - 1))
            cost = 0;
        else
            cost = 1;
        d[myRow][myCol] = (short) Math.min(d[myRow - 1][myCol] + 1, Math.min(d[myRow][myCol - 1] + 1, d[myRow - 1][myCol - 1] + cost));

        latches[myRow][myCol].countDown();

    }
}
