import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import static java.lang.Math.abs;

public class EurovisionLights {
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        StringTokenizer st = new StringTokenizer(r.readLine());
        int n = Integer.parseInt(st.nextToken());

        int[] a = new int[n];
        st = new StringTokenizer(r.readLine());
        for  (int i = 0; i < n; i++) {
            a[i] = Integer.parseInt(st.nextToken());
        }


        /*
        * lamps: 1 dimension array of n elements
        * It signifies the state of all the lamps. If lamp[i] == 0, then the lamp is off.
        * If lamp[i] == k with k > 0, then lamp[i] is the step in which lamp[i] was turned on.
        *
        * Example:
        * lamps = [1 3 5 2 4]
        * Lamp 1 was turned on first, then lamp 4, then lamp 2, then lamp 5 and finally lamp 3.
        * */

        int[] lamps = new int[n];

        /*
        * First check whether a(1) and a(n) is something other than 1.
        * This is because when all lights are off, you can turn on only 1 light &&
        * because the problem states that in the end all lights should be turned on,
        * which translates to one segment.
        * */

        if (a[0] != 1 || a[n-1] != 1) {
            pw.println("-1");
            pw.close();
            return;
        }

        /*
        * Below is the core logic of the algorithm.
        * The idea sources from identifying the patterns of what happens when:
        *   1) a(i-1) < a(i),
        *   2) a(i-1) > a(i) and
        *   3) a(i-1) == a(i).
        * Take the example of n = 7 and a = [1 2 3 2 2 1 1].
        * We see that a(1) = 1, so we turn on the first light (light = [1 0 0 0 0 0 0]) and the segment
        * number is 1.
        * Then, a(2) = 2, which is adding one segment. We cannot choose to turn on lamp 2
        * because the segment number would stay 1, so we 'naively' choose to light up lamp 3
        * (lamps = [1 0 2 0 0 0 0]).
        * Next is a(3) = 3, again one extra segment. Turning on lamp 2 would mean 1 segment [1, 3].
        * Turning on lamp 4 doesn't create a new segment, segment number stays 2, so we have to choose any
        * lamp other that those 2. We choose lamp 5 and lamps = [1 0 2 0 3 0 0].
        *
        * >>> We start to regocnise a pattern: To INCREMENT the segment number, go 2 zeros after the most right
        * >>> turned on lamp and turn it on. (Essentially we could choose any lamp after the second lamp from the most right
        * >>> turned on lamp (e.g [1 0 2 0 0 3 0]) but that would break the idea of trying to have 1 space between each lamp, something important
        * >>> for the next step. -- (1))
        *
        * For a(4) = 2, the segment number must decrease again. We can choose lamps 2 or 4, but not 6 or 7 (bad pun intended)
        * because if we choose 6, the segment number stays the same and if we turn on 7 the segment number increments.
        * We choose the first turned off lamp which is 2 and the segment number decrements to 2 and lamps = [1 4 2 0 3 0 0].
        *
        * >>> We start to regocnise a pattern: To DECREMENT the segment number, find the first turned off lamp, and choose that.
        * >>> The important featured mentioned in (1) is what help us succeed. If when incrementing we left 2 conscutive turned off
        * >>> lamps, we could not decrement the segment number whenever the a(n) sequence asked for it. For example, if the lights
        * >>> where lamps = [1 0 0 2 0 0 3] after the previous step (it is valid because a(3) = 3), decrementing the segment number
        * >>> to 2 is impossible.
        *
        * Now, a(5) is 2, so we want the same segment number. Lamp 4 cannot be turned on because we then would have 1 segment.
        * Lamp 7 also should not be turned on because that increments the segment number. By the way, these two observations hold
        * with our pattern recognition logic above. So we turn on lamp 6, the lamp adjacent to the right of the furthest most right
        * turned on lamp. The segment number stays 2 and lamps = [1 4 2 0 3 5 0].
        *
        * >>> We start to regocnise a pattern: To PRESERVE the segment number, choose the light after the most right turned on lamp.
        * >>> Anything other than that lamp either decrements the segment number or increments it.
        *
        * For the last two numbers in the sequest it is a(6) = 1, so we turn on the first turned off lamp from the left, which is 4,
        * so lamps = [1 4 2 6 3 5 0].
        * Finally, a(7) = 1, and we turn on the lamp after the most right turned on lamp which is 6, so lamps = [1 4 2 6 3 5 7].
        * */

        lamps[0] = 1;
        int posLastLamp = 0;
        int j;
        for (int i = 1; i < n; i++) {

            /*
            * Check if a(i) == 0, which is not allowed.
            * */
            if (a[i] == 0) {
                pw.println("-1");
                pw.close();
                return;
            }

            /*
            * The segments number can either increase or decrease by one, because in each step we turn on only
            * one lamp. So we can only answer for a(i) and a(i-1) with difference 1 (increment/decrement) or 0 (preserve).
            * */
            if (abs(a[i] - a[i-1]) > 1) {
                pw.println("-1");
                pw.close();
                return;
            }

            if (a[i] > a[i-1]) {          // <----------- INCREMENT number of segments

                /*
                * Check whether creating a new segment stays within lamp array.
                * */
                if (posLastLamp + 2 > n - 1) {
                    pw.println("-1");
                    pw.close();
                    return;
                }

                lamps[posLastLamp + 2] = 1 + i; // Turn on the lamp after the lamp after lamp(i).
                posLastLamp += 2;               // Update position of the most right turned on lamp.

            } else if (a[i] < a[i-1]) {   // <----------- DECREMENT number of segments

                /*
                * Find the first turned off lamp from the left.
                * */
                j = 0;
                while (j < n - 1) {
                    if (lamps[j] >= 1) j += 1;
                    else break;
                }

                lamps[j] = 1 + i;

            } else  {                     // <----------- PRESERVE number of segments

                /*
                * The most right turned on lamp is lamp(n) so we cannot turn on any lamp after that.
                * */
                if (posLastLamp + 1 > n - 1) {
                    pw.println("-1");
                    pw.close();
                    return;
                }

                lamps[posLastLamp + 1] = 1 + i; // Turn on the lamp after lamp(i).
                posLastLamp += 1;               // Update position of the most right turned on lamp.
            }
        }

        /*
        * The array lamps[n] is not the output the problem asks for.
        * The problem asks for the sequence in which the lamps are turned on.
        * In our example of n = 7 and a = [1 2 3 2 2 1 1], the lamp array is
        * lamps = [1 4 2 6 3 5 7]. Our output should be: [1 3 5 2 6 4 7].
        * */
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[lamps[i] - 1] = i + 1;
        }

        for (int i = 0; i < n; i++) {
            pw.write(p[i] + " ");
        }

        pw.close();
    }
}
