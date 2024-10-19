import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        processTestCase("testcase1.json");
        processTestCase("testcase2.json");
    }

    public static void processTestCase(String filename) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject jsonObject = new JSONObject(content);

        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<Point> points = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            if (jsonObject.has(String.valueOf(i))) {
                JSONObject point = jsonObject.getJSONObject(String.valueOf(i));
                int x = i;
                int base = point.getInt("base");
                String value = point.getString("value");

                BigInteger y = new BigInteger(value, base);
                points.add(new Point(x, y));
            }
        }

        BigInteger secret = lagrangeInterpolation(points, BigInteger.ZERO);
        System.out.println("The secret (constant term) for " + filename + " is: " + secret);
    }

    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    // Lagrange interpolation to calculate f(0)
    public static BigInteger lagrangeInterpolation(List<Point> points, BigInteger atX) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            BigInteger xi = BigInteger.valueOf(points.get(i).x);

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    term = term.multiply(atX.subtract(xj)).divide(xi.subtract(xj));
                }
            }

            result = result.add(term);
        }

        return result;
    }
}
