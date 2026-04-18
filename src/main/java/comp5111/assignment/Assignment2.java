package comp5111.assignment;
import comp5111.assignment.cut.Subject.StringAlgorithms;

public class Assignment2 {
    public static void main(String[] args) {
        // TODO we decide to not restrict how you implement main class. So do as you wish to complete assignment 2.
    	
        System.out.println(StringAlgorithms.padLeft("7", (short) 3, '0'));
        System.out.println(StringAlgorithms.padRight("hi", (short) 5, '_'));
        System.out.println(StringAlgorithms.extractIntInStr("abc123xyz"));
    }
}