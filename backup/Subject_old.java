package comp5111.assignment.cut;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class Subject {
 public static class StringAlgorithms {
     /**
      * Returns {@code true} if the given string starts with the specified
      * case-insensitive prefix, {@code false} otherwise.
      * 
      * @param str    the String to check
      * @param prefix the prefix to look for
      * @return {@code true} if the given string starts with the specified
      *         case-insensitive prefix, {@code false} otherwise.
      * @see java.lang.String#startsWith
      */
     public static boolean startsWithIgnoreCase(String str, String prefix) {
         if (str == null || prefix == null) {
             return false;
         }
         if (str.length() < prefix.length()) {
             return false;
         }
         if (str.startsWith(prefix)) {
             return true;
         }
         String lcStr = str.substring(0, prefix.length()).toLowerCase();
         String lcPrefix = prefix.toLowerCase();
         return lcStr.equals(lcPrefix);
     }

     /**
      * Trim elements of the given String array, calling String.trim() on each of
      * them.
      * 
      * @param array the original String array
      * @return the resulting array (of the same size) with trimmed elements
      */
     public static String[] trimArrayElements(String[] array) {
         if (array == null || array.length == 0) {
             return new String[0];
         }
         String[] result = new String[array.length];
         for (int i = 0; i < array.length; i++) {
             String element = array[i];
             result[i] = (element != null ? element.trim() : null);
         }
         return result;
     }

     /**
      * Decodes octets to characters using the UTF-8 decoding and appends the
      * characters to a StringBuffer.
      * 
      * @return the index to the next unchecked character in the string to decode
      */
     public static int decodeOctets(int i, ByteBuffer bb, StringBuilder sb) {
         if (bb.limit() == 1 && (bb.get(0) & 0xFF) < 0x80) {
             sb.append((char) bb.get(0));
             return i + 2;
         } else {
             CharBuffer cb = java.nio.charset.StandardCharsets.UTF_8.decode(bb);
             sb.append(cb);
             return i + bb.limit() * 3 - 1;
         }
     }

     /**
      * Enlarges this byte vector so that it can receive 'size' more bytes.
      * 
      * @param size   number of additional bytes that this byte vector should be able
      *               to receive.
      * @param data   the original byte array.
      * @param length the current length of data used in the byte array.
      */
     public static void enlarge(final int size, byte[] data, int length) {
         int doubleCapacity = 2 * data.length;
         int minimalCapacity = length + size;
         byte[] newData = new byte[doubleCapacity > minimalCapacity ? doubleCapacity : minimalCapacity];
         System.arraycopy(data, 0, newData, 0, length); data = newData;
     }

     /**
      * Give a text string, return a corresponding Boolean object. Supported: yes/no,
      * on/off, true/false, both capizalized or not.
      *
      * @param str the string to convert
      * @return the corresponding Boolean object or null if the string does not
      *         represent a valid boolean value
      */
     public static Boolean strToBoolean(String str) {
         if (str == null) {
             return null;
         }
         if (str.length() == 1) {
             char ch0 = str.charAt(0);
             if ((ch0 == 'y' || ch0 == 'Y') || (ch0 == 't' || ch0 == 'T')) {
                 return Boolean.TRUE;
             }
             if ((ch0 == 'n' || ch0 == 'N') || (ch0 == 'f' || ch0 == 'F')) {
                 return Boolean.FALSE;
             }
         } else if (str.length() == 2) {
             char ch0 = str.charAt(0);
             char ch1 = str.charAt(1);
             if ((ch0 == 'n' || ch0 == 'N') && (ch1 == 'o' || ch1 == 'O')) {
                 return Boolean.FALSE;
             }
             if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N')) {
                 return Boolean.TRUE;
             }
         } else if (str.length() == 3) {
             char ch0 = str.charAt(0);
             char ch1 = str.charAt(1);
             char ch2 = str.charAt(2);
             if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'f' || ch1 == 'F') && (ch2 == 'f' || ch2 == 'F')) {
                 return Boolean.FALSE;
             }
             if ((ch0 == 'y' || ch0 == 'Y') && (ch1 == 'e' || ch1 == 'E') && (ch2 == 's' || ch2 == 'S')) {
                 return Boolean.TRUE;
             }
         } else if (str.length() == 4) {
             char ch0 = str.charAt(0);
             char ch1 = str.charAt(1);
             char ch2 = str.charAt(2);
             char ch3 = str.charAt(3);
             if ((ch0 == 't' || ch0 == 'T') && (ch1 == 'r' || ch1 == 'R') && (ch2 == 'u' || ch2 == 'U') && (ch3 == 'e' || ch3 == 'E')) {
                 return Boolean.TRUE;
             }
         } else if (str.length() == 5) {
             char ch0 = str.charAt(0);
             char ch1 = str.charAt(1);
             char ch2 = str.charAt(2);
             char ch3 = str.charAt(3);
             char ch4 = str.charAt(4);
             if ((ch0 == 'f' || ch0 == 'F') && (ch1 == 'a' || ch1 == 'A') && (ch2 == 'l' || ch2 == 'L') && (ch3 == 's' || ch3 == 'S') && (ch4 == 'e' || ch4 == 'E')) {
                 return Boolean.FALSE;
             }
         }
         return null;
     }

     /**
      * Converts a string to a Boolean object based on specified true, false, and
      * null strings.
      *
      * @param str         the string to convert
      * @param trueString  the string representing true
      * @param falseString the string representing false
      * @param nullString  the string representing null
      * @return the corresponding Boolean object or null if the string matches
      *         nullString
      * @throws IllegalArgumentException if the string does not match any of the
      *                                  specified strings
      */
     public static Boolean strToBoolean(String str, String trueString, String falseString, String nullString) {
         if (str == null) {
             if (nullString == null) return null;
             if (trueString == null) return Boolean.TRUE;
             if (falseString == null)return Boolean.FALSE;
         }
         if (str.equals(trueString)) return Boolean.TRUE;
         if (str.equals(falseString))return Boolean.FALSE;
         if (str.equals(nullString)) return null;
         throw new IllegalArgumentException();
     }

     /**
      * Determines whether the specified character is found within the provided array
      * of characters.
      *
      * @param ch      the character to check for presence in the array
      * @param charray the array of characters to search within
      * @return true if the character is found in the array, otherwise false
      */
     private static boolean isOneOf(char ch, final char[] charray) {
         boolean result = false;
         for (char element : charray) {
             if (ch == element) {
                 result = true;
                 break;
             }
         }
         return result;
     }

     /**
      * Parses a token until any of the specified terminator characters is
      * encountered.
      *
      * @param str         the string to parse
      * @param terminators the array of terminating characters
      * @return the parsed token
      */
     public static String parseToken(final String str, final char[] terminators) {
         char ch;
         char[] chars = str.toCharArray();
         int pos = 0;
         while (pos < chars.length) {
             ch = chars[pos];
             if (isOneOf(ch, terminators)) {
                 pos++;
                 break;
             }
             pos++;
         }
         return str.substring(0, pos);
     }

     /**
      * Checks whether the String contains only digit characters.
      *
      * Null and empty String will return false.
      *
      * @param str the String to check
      * @return true if str contains only Unicode numeric characters, false otherwise
      */
     private static boolean isDigits(String str) {
         if ((str == null) || (str.length() == 0))
             return false;
         for (int i = 0; i < str.length(); i++) {
             if (!Character.isDigit(str.charAt(i))) {
                 return false;
             }
         }
         return true;
     }

     /**
      * Converts a string value to a Number.
      *
      * The method examines the value for a type qualifier at the end ('f', 'F', 'd',
      * 'D', 'l', 'L').
      * If a qualifier is found, it attempts to create successively larger number
      * types until it finds
      * one that can accommodate the value.
      *
      * If no type specifier is found, the method checks for a decimal point and then
      * tries successively
      * larger number types from Integer to BigInteger, and from Float to BigDecimal.
      * If the string starts
      * with "0x" or "-0x", it is interpreted as a hexadecimal integer. Values with
      * leading zeros will not
      * be interpreted as octal.
      *
      * @param val the string containing a number
      * @return the Number created from the string
      */
     public static Number parseNumber(String val) {
         if (val == null || val.length() == 0 || (val.length() == 1 && !Character.isDigit(val.charAt(0)))) {
             return null;
         }
         if (val.startsWith("--")) {
             return null;
         }
         if (val.startsWith("0x") || val.startsWith("-0x")) {
             return Integer.decode(val);
         }
         char lastChar = val.charAt(val.length() - 1);
         String dec;
         String exp;
         int decPos = val.indexOf('.');
         int expPos = val.indexOf('e') + val.indexOf('E') + 1;
         if (decPos > -1) {
             if (expPos > -1) {
                 if (expPos < decPos) {
                     throw new NumberFormatException(val);
                 }
                 dec = val.substring(decPos + 1, expPos);
             } else dec = val.substring(decPos + 1);
         } else { dec = null; }
         if (!Character.isDigit(lastChar)) {
             if (expPos > -1 && expPos < val.length() - 1) {
                 exp = val.substring(expPos + 1, val.length() - 1);
             } else exp = null;
             String numeric = val.substring(0, val.length() - 1);

             if (lastChar == 'l' || lastChar == 'L') {
                 if (dec == null && exp == null && (numeric.charAt(0) == '-' && isDigits(numeric.substring(1)) || isDigits(numeric))) {
                     BigInteger res = new BigInteger(numeric);
                     if (res == BigInteger.valueOf(res.intValue()))
                         return res.intValue();
                     if (res == BigInteger.valueOf(res.longValue()))
                         return res.longValue();
                     return res;
                 }
                 throw new NumberFormatException(val);
             } else if (lastChar == 'f' || lastChar == 'F') {
                 return Float.valueOf(numeric);
             } else if (lastChar == 'd' || lastChar == 'D') {
                 BigDecimal res = new BigDecimal(numeric);
                 if (res == BigDecimal.valueOf(res.doubleValue()))
                     return res.doubleValue();
                 return res;
             } else {
                 throw new NumberFormatException(val);
             }
         } else {
             if (expPos > -1 && expPos < val.length() - 1) {
                 exp = val.substring(expPos + 1);
             } else
                 exp = null;
             if (dec == null && exp == null) {
                 // Should be int,long,bigint
                 BigInteger res = new BigInteger(val);
                 if (res == BigInteger.valueOf(res.intValue()))
                     return res.intValue();
                 if (res == BigInteger.valueOf(res.longValue()))
                     return res.longValue();
                 return res;
             } else {
                 // Should be float,double,BigDec
                 BigDecimal res = new BigDecimal(val);
                 if (res == BigDecimal.valueOf(res.floatValue()))
                     return res.floatValue();
                 if (res == BigDecimal.valueOf(res.doubleValue()))
                     return res.doubleValue();
                 return res;
             }
         }
     }

     /**
      * Extract an integer from a string.
      * Rules:
      * null or empty string -> 0
      * 1234 -> 1234
      * a -> 0
      * 1234a123 -> 123
      *
      * @param str a string to extract the integer.
      */
     public static int extractIntInStr(String str) {
         if (str == null || str.length() < 1) {
             return 0;
         }
         int num = 0;
         for (int i = 0; i < str.length(); i++) {
             char ch = str.charAt(i);
             if (ch < '0' || ch > '9') {
                 return num;
             } else {
                 num = num * 10 + (ch - '0');
             }
         }
         return num;
     }

     /**
      * Parse a version string in the form like a, a.b, or a.b.c into four integers.
      * Rules:
      * 4         -> 4 0 0 0
      * 4.3.2.1   -> 4 3 2 1
      * 4.3.2.1.5 -> null
      *
      * @param versionString a version string to be transformed.
      */
     public static int[] getVersionNo(final String versionString) {
         if (versionString == null || versionString.length() < 1) {
             return null;
         }
         int[] vernos = new int[4];
         vernos[0] = vernos[1] = vernos[2] = vernos[3] = 0;
         int index = 0, cur = 0, pos;
         String segment;
         do { if (index > 3) {
                 return null;
             }
             pos = versionString.indexOf('.', cur);
             if (pos == -1) {
                 segment = versionString.substring(cur);
             } else if (cur < pos) {
                 segment = versionString.substring(cur, pos);
             } else { return null; } //Illegal format
             vernos[index] = extractIntInStr(segment);
             cur = pos + 1;
             index++;
         } while (pos > 0);
         return vernos;
     }

     /**
      * Pads the string on the left with the specified character and the given total
      * length.
      *
      * @param str     the string to pad
      * @param length  the desired total length
      * @param padChar the character to pad with
      * @return the padded string
      */
     public static String padLeft(String str, short length, char padChar) {
         if (str == null)
             str = "";
         int pad = length - str.length();
         if (pad <= 0)
             return str;
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < pad; i++)
             sb.append(padChar);
         sb.append(str);
         return sb.toString();
     }

     /**
      * Pads the string on the right with the specified character and the given total
      * length.
      *
      * @param str     the string to pad
      * @param length  the desired total length
      * @param padChar the character to pad with
      * @return the padded string
      */
     public static String padRight(String str, short length, char padChar) {
         if (str == null)
             str = "";
         int pad = length - str.length();
         if (pad <= 0)
             return str;
         StringBuilder sb = new StringBuilder(str);
         for (int i = 0; i < pad; i++)
             sb.append(padChar);
         return sb.toString();
     }
 }

 public static class DateTimeAlgorithms {
     /**
      * Calculates the number of days between two dates.
      * 
      * @param year1  the first year
      * @param month1 the first month
      * @param day1   the first day
      * @param year2  the second year
      * @param month2 the second month
      * @param day2   the second day
      * @return the number of days between the two dates
      */
     public static int daysBetweenDates(int year1, int month1, int day1, int year2, int month2, int day2) {
         java.util.Calendar cal1 = java.util.Calendar.getInstance();
         java.util.Calendar cal2 = java.util.Calendar.getInstance();
         if (!checkValidDate(year1, month1, day1) || !checkValidDate(year2, month2, day2)) {
             throw new IllegalArgumentException("Invalid date");
         }
         cal1.set(year1, month1 - 1, day1, 0, 0, 0);
         cal1.set(java.util.Calendar.MILLISECOND, 0);
         cal2.set(year2, month2 - 1, day2, 0, 0, 0);
         cal2.set(java.util.Calendar.MILLISECOND, 0);
         long millis1 = cal1.getTimeInMillis();
         long millis2 = cal2.getTimeInMillis();
         long diff = millis2 - millis1;
         return (int) (diff / (24 * 60 * 60 * 1000));
     }

     /**
      * Returns the number of days in a given month of a specific year.
      *
      * @param year  the year
      * @param month the month (1-12)
      * @return the number of days in the month, or -1 if the month is invalid
      */
     public static int calcDaysInMonth(int year, int month) {
         if (month < 1 || month > 12)
             return -1;
         if (month == 2) {
             return judgeLeapYear(year) ? 29 : 28;
         }
         if (month == 4 || month == 6 || month == 9 || month == 11) {
             return 30;
         }
         return 31;
     }

     /**
      * Checks if a given date is valid.
      *
      * @param year  the year
      * @param month the month (1-12)
      * @param day   the day (1-31)
      * @return {@code true} if the date is valid; {@code false} otherwise
      */
     private static boolean checkValidDate(int year, int month, int day) {
         if (month < 1 || month > 12)
             return false;
         int daysInMonth = calcDaysInMonth(year, month);
         return day > 0 && day < daysInMonth;
     }

     /**
      * Checks if the given year is a leap year.
      *
      * @param year the year to check
      * @return true if the year is a leap year, false otherwise
      */
     public static boolean judgeLeapYear(int year) {
         if (year % 4 != 0) {
             return false;
         } else if (year % 100 != 0) {
             return true;
         } else
             return year % 400 == 0;
     }

     /**
      * Converts seconds into a human-readable string representing the largest time
      * unit (years, weeks, days, hours, minutes, or seconds).
      *
      * @param sec the time in seconds to convert
      * @return a string representing the converted time with the appropriate unit
      */
     public static String roundSecondToString(long second) {
         double converted;
         String type;
         if (second >= 365.0 * 24.0 * 60.0 * 60.0) {
             converted = second / 365.0 / 24.0 / 60.0 / 60.0;
             type = "Y";
         } else if (second >= 7 * 24 * 60 * 60) {
             converted = second / 7.0 / 24.0 / 60.0 / 60.0;
             type = "W";
         } else if (second >= 24 * 60 * 60) {
             converted = second / 24.0 / 60.0 / 60.0;
             type = "D";
         } else if (second >= 60 * 60) {
             converted = second / 60.0 / 60.0;
             type = "H";
         } else if (second >= 60) {
             converted = second / 60.0;
             type = "M";
         } else { converted = second;
             type = "S";
         }
         return Math.round(converted) + " " + type;
     }

     /**
      * Converts a day of the month into a string with the appropriate ordinal
      * suffix.
      *
      * @param dom the day of the month (1-31)
      * @return the DOM as a string with an ordinal suffix, or "(invalid)" if out of
      *         range
      */
     public static String dayStr(int day) {
         String ans = "" + day;
         if (day >= 11 && day <= 13)
             ans += "th";
         else if (day % 10 == 1)
             ans += "st";
         else if (day % 10 == 2)
             ans += "nd";
         else if (day % 10 == 3)
             ans += "rd";
         else
             ans += "th";

         if (!(day > 0 && day <= 31))
             ans = "(invalid)";
         return ans;
     }

     /**
      * Returns the quarter of the year for a given date.
      *
      * @param month the month (1-12)
      * @return the quarter (1-4), or -1 if the month is invalid
      */
     public static int getQuarter(int month) {
         if (month < 1 || month > 12)
             return -1;
         if (month <= 3)
             return 1;
         if (month <= 6)
             return 2;
         if (month <= 9)
             return 3;
         return 4;
     }

     /**
      * Converts a three-character month abbreviation (e.g., "Jan") into its
      * corresponding month number.
      *
      * @param c0 the first character of the month abbreviation
      * @param c1 the second character of the month abbreviation
      * @param c2 the third character of the month abbreviation
      * @return the month number (1 ... 12), or -1 if the abbreviation is invalid
      */
     public static int monAbbr2month(String abrr) {
         if (abrr == null || abrr.length() != 3) {
             return -1;
         }
         char ch0 = abrr.charAt(0);
         char ch1 = abrr.charAt(1);
         char ch2 = abrr.charAt(2);
         int hash = (ch0 << 16) | (ch1 << 8) | ch2;

         if (hash == 4874606)
             return 1;
         if (hash == 4613474)
             return 2;
         if (hash == 5071218)
             return 3;
         if (hash == 4288626)
             return 4;
         if (hash == 5071225)
             return 5;
         if (hash == 4879726)
             return 6;
         if (hash == 4879724)
             return 7;
         if (hash == 4289895)
             return 8;
         if (hash == 5465466)
             return 9;
         if (hash == 5202804)
             return 10;
         if (hash == 5140342)
             return 11;
         if (hash == 4482403)
             return 12;

         return -1;

     }

     /**
      * Converts a month number into its corresponding three-character month
      * abbreviation.
      * 
      * @param month the month number (1-12)
      * @return the three-character month abbreviation, or "(invalid)" if the month
      *         number is invalid
      */
     public static String month2MonAbbr(int month) {
         switch (month) {
             case 1:
                 return "Jan";
             case 2:
                 return "Feb";
             case 3:
                 return "Mar";
             case 4:
                 return "Apr";
             case 5:
                 return "May";
             case 6:
                 return "Jun";
             case 7:
                 return "Jul";
             case 8:
                 return "Aug";
             case 9:
                 return "Sep";
             case 10:
                 return "Oct";
             case 11:
                 return "Nov";
             case 12:
                 return "Dec";
             default:
                 return "(invalid)";
         }
     }

     /**
      * Returns the number of days in the year for the given year.
      * 
      * @param year the year to check
      * @return 366 if leap year, 365 otherwise
      */
     public static int daysInYear(int year) {
         return DateTimeAlgorithms.judgeLeapYear(year) ? 366 : 365;
     }

     /**
      * Returns the number of days between two date strings in "yyyy-MM-dd" format.
      * 
      * @param dateStr1 the first date string
      * @param dateStr2 the second date string
      * @return the number of days between the two dates, or Integer.MIN_VALUE if
      *         invalid
      */
     public static int daysBetweenDateStrings(String dateStr1, String dateStr2) {
         try {
             String[] parts1 = dateStr1.split("-");
             String[] parts2 = dateStr2.split("-");
             if (parts1.length != 3 || parts2.length != 3)
                 return Integer.MIN_VALUE;
             int y1 = Integer.parseInt(parts1[0]);
             int m1 = Integer.parseInt(parts1[1]);
             int d1 = Integer.parseInt(parts1[2]);
             int y2 = Integer.parseInt(parts2[0]);
             int m2 = Integer.parseInt(parts2[1]);
             int d2 = Integer.parseInt(parts2[2]);
             return DateTimeAlgorithms.daysBetweenDates(y1, m1, d1, y2, m2, d2);
         }
         catch (Exception e) { return Integer.MIN_VALUE; }
     }

     /**
      * Returns a formatted string representing the full date, e.g. "21st Feb 2023".
      * 
      * @param year  the year
      * @param month the month (1-12)
      * @param day   the day (1-31)
      * @return the formatted date string, or "(invalid)" if the date is invalid
      */
     public static String formatFullDate(int year, int month, int day) {
         if (!DateTimeAlgorithms.checkValidDate(year, month, day))
             return "(invalid)";
         String dayStr = DateTimeAlgorithms.dayStr(day);
         String monAbbr = month2MonAbbr(month);
         return dayStr + " " + monAbbr + " " + year;
     }

     /**
      * Returns a list of all dates between two dates (inclusive) in "yyyy-MM-dd"
      * format.
      * 
      * @param year1  start year
      * @param month1 start month
      * @param day1   start day
      * @param year2  end year
      * @param month2 end month
      * @param day2   end day
      * @return list of date strings, or empty list if invalid input
      */
     public static java.util.List<String> listDatesBetween(int year1, int month1, int day1, int year2, int month2,
             int day2) {
         java.util.List<String> result = new java.util.ArrayList<>();
         if (!checkValidDate(year1, month1, day1) || !checkValidDate(year2, month2, day2))
             return result;
         java.util.Calendar cal1 = java.util.Calendar.getInstance();
         java.util.Calendar cal2 = java.util.Calendar.getInstance();
         cal1.set(year1, month1 - 1, day1, 0, 0, 0);
         cal1.set(java.util.Calendar.MILLISECOND, 0);
         cal2.set(year2, month2 - 1, day2, 0, 0, 0);
         cal2.set(java.util.Calendar.MILLISECOND, 0);
         if (cal1.after(cal2))
             return result;
         java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
         while (!cal1.after(cal2)) {
             result.add(sdf.format(cal1.getTime()));
             cal1.add(java.util.Calendar.DATE, 1);
         }
         return result;
     }

     /**
      * Returns the first day of the month as a date string "yyyy-MM-dd".
      * 
      * @param year  the year
      * @param month the month (1-12)
      * @return the first day of the month as string, or "(invalid)" if invalid
      */
     public static String firstDayOfMonth(int year, int month) {
         if (month < 1 || month > 12)
             return "(invalid)";
         return String.format("%04d-%02d-01", year, month);
     }

     /**
      * Returns the last day of the month as a date string "yyyy-MM-dd".
      * 
      * @param year  the year
      * @param month the month (1-12)
      * @return the last day of the month as string, or "(invalid)" if invalid
      */
     public static String lastDayOfMonth(int year, int month) {
         int days = calcDaysInMonth(year, month);
         if (days == -1)
             return "(invalid)";
         return String.format("%04d-%02d-%02d", year, month, days);
     }

     /**
      * Returns the number of months between two dates (ignoring days).
      * 
      * @param year1  start year
      * @param month1 start month
      * @param year2  end year
      * @param month2 end month
      * @return number of months between the two dates
      */
     public static int monthsBetween(int year1, int month1, int year2, int month2) {
         if (month1 < 1 || month1 > 12 || month2 < 1 || month2 > 12)
             return -1;
         int months = (year2 - year1) * 12 + (month2 - month1);
         return months;
     }
 }

 /**
  * Player interface for the game.
  * Implementations could be computer player or human player.
  */
 public class GamePlayer implements Cloneable {
     /**
      * The name of a player.
      * By default, name is a unique identifier to distinguish different players. But
      * this can be overridden by
      * overriding {@link Player#equals(Object)} method.
      */
     private final String name;

     public GamePlayer() {
         this.name = "Anonymous Player";
     }

     public GamePlayer(String name) {
         this.name = name;
     }

     public String getName() {
         return name;
     }

     /**
      * The game score of this player.
      * Score will be updated whenever the player makes a move through
      * {@link Game#updateScore(GamePlayer, GamePlayer, Move)}
      * and {@link GamePlayer#setScore(int)}
      */
     private int score;

     public int getScore() {
         return score;
     }

     /**
      * Sets the score for this player.
      * 
      * @param score the new score
      */
     public void setScore(int score) {
         this.score = score;
     }

     /**
      * Increases the player's score by the specified amount.
      * 
      * @param delta the amount to add to the score
      */
     public void addScore(int delta) {
         this.score += delta;
     }

     /**
      * Resets the player's score to zero.
      */
     public void resetScore() {
         this.score = 0;
     }

     /**
      * Returns a string representation of the player.
      * 
      * @return the player's name and score
      */
     @Override
     public String toString() {
         return "GamePlayer{name='" + name + "', score=" + score + "}";
     }

     @Override
     public boolean equals(Object o) {
         if (this == o)
             return true;
         if (o == null || getClass() != o.getClass())
             return false;
         GamePlayer player = (GamePlayer) o;
         return name.equals(player.name);
     }

     @Override
     public GamePlayer clone() throws CloneNotSupportedException {
         GamePlayer cloned = new GamePlayer(this.name);
         cloned.score = this.score;
         return cloned;
     }
 }

 /**
  * A square (position, place) in the gameboard.
  * Represented by coordinates a 2-D coordinate system.
  * <p>
  * x and y coordinates of a place on gameboard are two fields of this class.
  */
 public class GamePlace implements Cloneable {
     private int y, x;

     /**
      * Constructor of a place in the gameboard.
      * 
      * @param x x coordinate
      * @param y y coordinate
      */
     public GamePlace(int x, int y) {
         this.y = y;
         this.x = x;
     }

     /**
      * Set y coordinate of this place.
      * 
      * @param y y coordinate
      */
     public void y(int y) {
         this.y = y;
     }

     /**
      * Get y coordinate of this place.
      * 
      * @return y coordinate
      */
     public int y() {
         return y;
     }

     /**
      * Set x coordinate of this place.
      * 
      * @param x
      */
     public void x(int x) {
         this.x = x;
     }

     /**
      * Get x coordinate of this place.
      * 
      * @return x coordinate
      */
     public int x() {
         return x;
     }

     @Override
     public GamePlace clone() throws CloneNotSupportedException {
         GamePlace cloned = new GamePlace(-1, -1);
         cloned.y = this.y;
         cloned.x = this.x;
         return cloned;
     }

     @Override
     public String toString() {
         return "(" + x + ", " + y + ")";
     }

     @Override
     public boolean equals(Object obj) {
         if (this == obj)
             return true;
         if (obj == null || getClass() != obj.getClass())
             return false;
         GamePlace other = (GamePlace) obj;
         return this.x == other.x && this.y == other.y;
     }

     @Override
     public int hashCode() {
         return 31 * x + y;
     }

     /**
      * Returns true if this place is adjacent (orthogonally) to another place.
      * 
      * @param other the other GamePlace
      * @return true if adjacent, false otherwise
      */
     public boolean isAdjacent(GamePlace other) {
         if (other == null)
             return false;
         int dx = Math.abs(this.x - other.x);
         int dy = Math.abs(this.y - other.y);
         return (dx + dy == 1);
     }

     /**
      * Returns the Manhattan distance to another place.
      * 
      * @param other the other GamePlace
      * @return the Manhattan distance
      */
     public int manhattanDistance(GamePlace other) {
         if (other == null)
             return -1;
         return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
     }
 }

 /**
  * Game configuration, including:
  * 1. size of gameboard
  * 2. place (square) of central square in classical Jeson Mor
  * 3. the initial game board with pieces on it, which is configurable through
  * {@link GameConfiguration#addInitialPiece(GamePlayer, GamePlace)}
  * 4. the two players.
  */
 public class GameConfiguration implements Cloneable {
     /**
      * Size of gameboard.
      * The gameboard has equal size in width and height.
      * The size should be an odd number.
      */
     private final int size;

     /**
      * An array of two players in the game.
      * Note that in the implementation of {@link Game#start()} the first player in
      * this array moves first when game starts.
      */
     private GamePlayer[] players;

     /**
      * The initial map of the gameboard, containing initial pieces and their places.
      * This map has keys for all places in the gameboard, with or without pieces.
      * If there is no piece in one place, the place is mapped to null.
      */
     private GamePlayer[][] initialBoard;

     /**
      * The central square of the gameboard.
      */
     private GamePlace centralPlace;

     /**
      * Constructor of configuration with given size and fixed two players.
      *
      * @param size size of the gameboard.
      */
     public GameConfiguration(int size) {
         this(size, new GamePlayer[] { new GamePlayer("Player A"), new GamePlayer("Player B") });
     }

     /**
      * Constructor of configuration with given size and players.
      *
      * @param size    size of the gameboard.
      * @param players an array of two players in the game, the first player should
      *                move first when game starts.
      */
     public GameConfiguration(int size, GamePlayer[] players) {
         // validate size
         if (size < 3) {
             throw new IllegalArgumentException("size of gameboard must be at least 3");
         }
         if (size % 2 != 1) {
             throw new IllegalArgumentException("size of gameboard must be an odd number");
         }
         if (size > 25) {
             throw new IllegalArgumentException("size of gameboard is at most 25");
         }
         this.size = size;
         // We only have 2 players
         this.players = players;
         if (players.length != 2) {
             throw new IllegalArgumentException("there must be exactly two players");
         }
         // initialize map of the game board by putting every place null (meaning no
         // piece)
         this.initialBoard = new GamePlayer[size][];
         for (int x = 0; x < size; x++) {
             this.initialBoard[x] = new GamePlayer[size];
             for (int y = 0; y < size; y++) {
                 this.initialBoard[x][y] = null;
             }
         }
         // calculate the central place
         this.centralPlace = new GamePlace(size / 2, size / 2);
     }

     /**
      * Add piece to the initial gameboard.
      * The player that this piece belongs to will be automatically added into the
      * configuration.
      *
      * @param piece piece to be added
      * @param place place to put the piece
      */
     public void addInitialPiece(GamePlayer piece, GamePlace place) {
         if (!piece.equals(this.players[0]) && !piece.equals(this.players[1])) {
             throw new IllegalArgumentException("the player of the piece is unknown");
         }
         if (place.x() >= this.size || place.y() >= this.size || place.x() < 0 || place.y() < 0) {
             // The place must be inside the gameboard
             throw new IllegalArgumentException("the place" + place.toString() + " must be inside the gameboard");
         }
         if (place.equals(this.centralPlace)) {
             throw new IllegalArgumentException("piece cannot be put at central place initially");
         }

         // put the piece on the initial board
         this.initialBoard[place.x()][place.y()] = piece;
     }

     @Override
     public GameConfiguration clone() throws CloneNotSupportedException {
         GameConfiguration cloned = (GameConfiguration) super.clone();
         cloned.players = this.players.clone();
         for (int i = 0; i < this.players.length; i++) {
             cloned.players[i] = this.players[i].clone();
         }
         cloned.initialBoard = this.initialBoard.clone();
         for (int i = 0; i < this.size; i++) {
             cloned.initialBoard[i] = this.initialBoard[i].clone();
             System.arraycopy(this.initialBoard[i], 0, cloned.initialBoard[i], 0, this.size);
         }
         cloned.centralPlace = this.centralPlace.clone();
         return cloned;
     }

     /**
      * Get the size of the gameboard.
      * 
      * @return the size of the gameboard
      */
     public int getSize() {
         return size;
     }

     /**
      * Get the players in the game.
      * 
      * @return the array of players
      */
     public GamePlayer[] getPlayers() {
         return players.clone();
     }

     /**
      * Get the initial board configuration.
      * 
      * @return the 2D array representing the initial board
      */
     public GamePlayer[][] getInitialBoard() {
         GamePlayer[][] copy = new GamePlayer[size][size];
         for (int x = 0; x < size; x++) {
             System.arraycopy(initialBoard[x], 0, copy[x], 0, size);
         }
         return copy;
     }

     /**
      * Get the player at a specific place in the initial board.
      * 
      * @param place the place to check
      * @return the GamePlayer at the place, or null if empty
      */
     public GamePlayer getPlayerAt(GamePlace place) {
         if (place.x() < 0 || place.x() >= size || place.y() < 0 || place.y() >= size) {
             throw new IllegalArgumentException("Place is out of bounds");
         }
         return initialBoard[place.x()][place.y()];
     }

     /**
      * Returns a string representation of the initial board.
      * 
      * @return a string showing the initial board state
      */
     @Override
     public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("GameConfiguration (size=").append(size).append(")\n");
         for (int y = 0; y < size; y++) {
             for (int x = 0; x < size; x++) {
                 GamePlayer p = initialBoard[x][y];
                 sb.append(p == null ? "." : "P");
                 if (x < size - 1)
                     sb.append(" ");
             }
             sb.append("\n");
         }
         return sb.toString();
     }

     /**
      * Returns a list of all empty places (not occupied) on the initial board.
      * 
      * @return a list of GamePlace objects representing empty places
      */
     public java.util.List<GamePlace> getEmptyPlaces() {
         java.util.List<GamePlace> emptyPlaces = new java.util.ArrayList<>();
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 if (initialBoard[x][y] == null && !(x == centralPlace.x() && y == centralPlace.y())) {
                     emptyPlaces.add(new GamePlace(x, y));
                 }
             }
         }
         return emptyPlaces;
     }

     /**
      * Check if a given place is occupied in the initial board.
      * 
      * @param place the place to check
      * @return true if occupied, false otherwise
      */
     public boolean isOccupied(GamePlace place) {
         if (place.x() < 0 || place.x() >= size || place.y() < 0 || place.y() >= size) {
             return true;
         }
         return initialBoard[place.x()][place.y()] != null;
     }

     /**
      * Counts the number of pieces for each player on the initial board.
      * 
      * @return an array of two integers, where the first element is the count for
      *         players[0], and the second for players[1]
      */
     public int[] countPiecesPerPlayer() {
         int[] counts = new int[2];
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 GamePlayer p = initialBoard[x][y];
                 if (p != null) {
                     if (p.equals(players[0])) {
                         counts[0]++;
                     } else if (p.equals(players[1])) {
                         counts[1]++;
                     }
                 }
             }
         }
         return counts;
     }

     /**
      * Checks if the initial board is symmetric along the main diagonal.
      * 
      * @return true if symmetric, false otherwise
      */
     public boolean isBoardSymmetric() {
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 GamePlayer a = initialBoard[x][y];
                 GamePlayer b = initialBoard[y][x];
                 if (a == null && b != null)
                     return false;
                 if (a != null && !a.equals(b))
                     return false;
             }
         }
         return true;
     }

     /**
      * Returns a list of all places occupied by a given player.
      * 
      * @param player the player to search for
      * @return a list of GamePlace objects occupied by the player
      */
     public java.util.List<GamePlace> getPlacesOfPlayer(GamePlayer player) {
         java.util.List<GamePlace> places = new java.util.ArrayList<>();
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 if (initialBoard[x][y] != null && initialBoard[x][y].equals(player)) {
                     places.add(new GamePlace(x, y));
                 }
             }
         }
         return places;
     }

     /**
      * Checks if the initial board is completely filled (except the central place).
      * 
      * @return true if all places except the central place are occupied, false
      *         otherwise
      */
     public boolean isBoardFull() {
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 if ((x != centralPlace.x() || y != centralPlace.y()) && initialBoard[x][y] == null) {
                     return false;
                 }
             }
         }
         return true;
     }

     /**
      * Resets the initial board to empty (except for the central place, which
      * remains empty).
      */
     public void clearInitialBoard() {
         for (int x = 0; x < size; x++) {
             for (int y = 0; y < size; y++) {
                 initialBoard[x][y] = null;
             }
         }
     }
 }
}
