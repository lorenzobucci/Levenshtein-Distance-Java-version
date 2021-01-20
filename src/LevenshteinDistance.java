public class LevenshteinDistance {

    static int sequentialAlgorithm (String str1, String str2){
        int[][] d = new int[str1.length() + 1][str2.length() + 1];

        int cost;

        for (int i1 = 0; i1 <= str1.length(); i1++)
            d[i1][0] = i1;
        for (int i2 = 0; i2 <= str2.length(); i2++)
            d[0][i2] = i2;

        for (int i1 = 1; i1 <= str1.length(); i1++){
            for (int i2 = 1; i2 <= str2.length(); i2++){
                if (str1.charAt(i1 - 1) == str2.charAt(i2 - 1))
                    cost = 0;
                else
                    cost = 1;
                d[i1][i2] = Math.min(d[i1 - 1][i2] + 1, Math.min(d[i1][i2 - 1] + 1, d[i1 - 1][i2 - 1] + cost));
            }
        }

        return d[str1.length()][str2.length()];
    }
}
