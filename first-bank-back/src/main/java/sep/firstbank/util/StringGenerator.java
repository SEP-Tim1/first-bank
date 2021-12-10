package sep.firstbank.util;

import java.nio.charset.Charset;
import java.util.Random;

public class StringGenerator {

    public static String generate(int n) {
        byte[] array = new byte[n];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
