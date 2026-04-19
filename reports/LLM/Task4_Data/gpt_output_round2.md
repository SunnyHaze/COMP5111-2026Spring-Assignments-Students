```java
package comp5111.assignment.cut;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class SubjectSelected {

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static String[] trimArrayElements(String[] array) {
        if (array == null || array.length == 0) {
            return new String[0];
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] == null ? null : array[i].trim();
        }
        return result;
    }

    public static int decodeOctets(int i, ByteBuffer bb, StringBuilder sb) {
        if (bb.limit() == 1) {
            byte b = bb.get(0);
            if ((b & 0xFF) < 0x80) {
                sb.append((char) (b & 0xFF));
                return i + 2;
            }
        }
        CharBuffer cb = java.nio.charset.StandardCharsets.UTF_8.decode(bb.asReadOnlyBuffer());
        sb.append(cb);
        return i + bb.limit() * 3 - 1;
    }

    public static void enlarge(final int size, byte[] data, int length) {
        if (length < 0 || length > data.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int newCapacity = Math.max(2 * data.length, length + size);
        if (newCapacity < 0) {
            newCapacity = 0;
        }
        byte[] newData = new byte[newCapacity];
        for (int i = 0; i < length; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    public static Boolean strToBoolean(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase("y")
                || str.equalsIgnoreCase("t")
                || str.equalsIgnoreCase("on")
                || str.equalsIgnoreCase("yes")
                || str.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (str.equalsIgnoreCase("n")
                || str.equalsIgnoreCase("f")
                || str.equalsIgnoreCase("no")
                || str.equalsIgnoreCase("off")
                || str.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static Boolean strToBoolean(String str, String trueString, String falseString, String nullString) {
        if (str == null) {
            if (nullString == null) {
                return null;
            }
            if (trueString == null) {
                return Boolean.TRUE;
            }
            if (falseString == null) {
                return Boolean.FALSE;
            }
        }
        if (str.equals(trueString)) {
            return Boolean.TRUE;
        }
        if (str.equals(falseString)) {
            return Boolean.FALSE;
        }
        if (str.equals(nullString)) {
            return null;
        }
        throw new IllegalArgumentException();
    }

    public static String parseToken(final String str, final char[] terminators) {
        if (str == null) {
            return null;
        }
        if (terminators == null || terminators.length == 0) {
            return str;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            for (char t : terminators) {
                if (c == t) {
                    return str.substring(0, i);
                }
            }
        }
        return str;
    }

    public static Number parseNumber(String val) {
        if (val == null || val.length() == 0) {
            return null;
        }
        if (val.length() == 1 && !Character.isDigit(val.charAt(0))) {
            return null;
        }
        if (val.startsWith("--")) {
            return null;
        }
        if (val.startsWith("0x") || val.startsWith("-0x")) {
            return Integer.decode(val);
        }

        char lastChar = val.charAt(val.length() - 1);
        boolean hasDecimal = val.indexOf('.') >= 0;
        boolean hasExponent = val.indexOf('e') >= 0 || val.indexOf('E') >= 0;

        if (lastChar == 'l' || lastChar == 'L') {
            String numeric = val.substring(0, val.length() - 1);
            if (!isPlainInteger(numeric)) {
                throw new NumberFormatException(val);
            }
            try {
                return narrowInteger(new BigInteger(numeric));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(val);
            }
        }

        if (lastChar == 'f' || lastChar == 'F') {
            String numeric = val.substring(0, val.length() - 1);
            return Float.valueOf(numeric);
        }

        if (lastChar == 'd' || lastChar == 'D') {
            String numeric = val.substring(0, val.length() - 1);
            BigDecimal bd = new BigDecimal(numeric);
            double d = bd.doubleValue();
            if (!Double.isInfinite(d) && new BigDecimal(Double.toString(d)).compareTo(bd) == 0) {
                return Double.valueOf(d);
            }
            return bd;
        }

        if (Character.isLetter(lastChar)) {
            throw new NumberFormatException(val);
        }

        if (!hasDecimal && !hasExponent) {
            if (isPlainInteger(val)) {
                return narrowInteger(new BigInteger(val));
            }
            int firstDigit = firstDigitIndex(val);
            if (firstDigit > 0) {
                return Integer.valueOf(val.substring(0, firstDigit));
            }
            throw new NumberFormatException(val);
        }

        if (!hasDecimal && hasExponent && !startsNumericLike(val) && Character.isDigit(lastChar)) {
            return Integer.valueOf(val);
        }

        if (hasDecimal && Character.isLetter(lastChar)) {
            throw new NumberFormatException(val);
        }

        BigDecimal bd = new BigDecimal(val);
        float f = bd.floatValue();
        if (!Float.isInfinite(f) && new BigDecimal(Float.toString(f)).compareTo(bd) == 0) {
            return Float.valueOf(f);
        }
        double d = bd.doubleValue();
        if (!Double.isInfinite(d) && new BigDecimal(Double.toString(d)).compareTo(bd) == 0) {
            return Double.valueOf(d);
        }
        return bd;
    }

    public static int extractIntInStr(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int current = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                current = current * 10 + (c - '0');
            } else {
                current = 0;
            }
        }
        return current;
    }

    public static int[] getVersionNo(final String versionString) {
        if (versionString == null || versionString.length() == 0) {
            return null;
        }

        int[] result = new int[] {0, 0, 0, 0};
        int count = 0;
        int start = 0;

        for (int i = 0; i <= versionString.length(); i++) {
            if (i == versionString.length() || versionString.charAt(i) == '.') {
                if (count >= 4) {
                    return null;
                }
                if (i == start) {
                    return null;
                }
                String part = versionString.substring(start, i);
                for (int j = 0; j < part.length(); j++) {
                    if (!Character.isDigit(part.charAt(j))) {
                        return null;
                    }
                }
                try {
                    result[count] = Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    return null;
                }
                count++;
                start = i + 1;
            }
        }

        return result;
    }

    public static String padLeft(String str, short length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        int pads = length - str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String padRight(String str, short length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        int pads = length - str.length();
        StringBuilder sb = new StringBuilder(length);
        sb.append(str);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    private static boolean isPlainInteger(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        int start = 0;
        char first = s.charAt(0);
        if (first == '+' || first == '-') {
            if (s.length() == 1) {
                return false;
            }
            start = 1;
        }
        for (int i = start; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean startsNumericLike(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        char c = s.charAt(0);
        if (c == '+' || c == '-') {
            return s.length() > 1 && (Character.isDigit(s.charAt(1)) || s.charAt(1) == '.');
        }
        return Character.isDigit(c) || c == '.';
    }

    private static int firstDigitIndex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static Number narrowInteger(BigInteger bi) {
        if (bi.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
                && bi.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
            return Integer.valueOf(bi.intValue());
        }
        if (bi.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0
                && bi.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
            return Long.valueOf(bi.longValue());
        }
        return bi;
    }
}
```
