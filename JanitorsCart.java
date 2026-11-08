import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class JanitorsCart {
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        StringTokenizer st = new StringTokenizer(r.readLine());
        int n = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());

        int[] a = new int[n];
        st = new StringTokenizer(r.readLine());
        for (int i = 0; i < n; i++) {
            a[i] = Integer.parseInt(st.nextToken());
        }

        int capacity = c;
        int i = n - 1;
        boolean returning = false;
        long dist = 0;
        int diff;
        while (i > -1) {
            if (!returning) {

                if (a[i] == 0) {
                    i--;
                    continue;
                }

                dist += 2L * (i + 1) * Math.ceilDiv(a[i], capacity);
                capacity = capacity - (a[i] %  capacity);
                a[i] = 0;
                if (capacity < c) returning = true;
                i--;
            } else {
                diff = Math.min(capacity, a[i]);
                a[i] = a[i] - diff;
                capacity = capacity - diff;

                if (capacity > 0) {
                    i--;
                } else {
                    if (a[i] == 0) i--;
                    capacity = c;
                    returning = false;
                }
            }
        }


        pw.println(dist);
        pw.close();
    }
}
