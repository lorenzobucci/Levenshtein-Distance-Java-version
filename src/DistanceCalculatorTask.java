import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DistanceCalculatorTask implements Callable<Short> {

    private final int myRow;
    private final int myCol;
    private final String str1;
    private final String str2;
    private final ArrayList<Future<Short>> d;

    public DistanceCalculatorTask(int myRow, int myCol, String str1, String str2, ArrayList<Future<Short>> d) {
        this.myRow = myRow;
        this.myCol = myCol;
        this.str1 = str1;
        this.str2 = str2;
        this.d = d;
    }

    @Override
    public Short call() throws ExecutionException, InterruptedException {

        int rowPrev;
        int colPrev;
        int rowColPrev = 0;

        if (myRow != 0)
            colPrev = d.get((myRow - 1) * str2.length() + myCol).get();
        else {
            rowColPrev = myCol;
            colPrev = myCol + 1;
        }
        if (myCol != 0)
            rowPrev = d.get(myRow * str2.length() + (myCol - 1)).get();
        else {
            rowColPrev = myRow;
            rowPrev = myRow + 1;
        }
        if (myCol != 0 && myRow != 0)
            rowColPrev = d.get((myRow - 1) * str2.length() + (myCol - 1)).get();

        short cost;
        if (str1.charAt(myRow) == str2.charAt(myCol))
            cost = 0;
        else
            cost = 1;

        return (short) Math.min(colPrev + 1, Math.min(rowPrev + 1, rowColPrev + cost));
    }
}
