package comp5111.assignment;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SingleTestRunner {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: SingleTestRunner <fully.qualified.ClassName> <methodName>");
            System.exit(1);
        }

        String className = args[0];
        String methodName = args[1];

        Class<?> clazz = Class.forName(className);
        JUnitCore core = new JUnitCore();
        Result result = core.run(Request.method(clazz, methodName));

        System.out.println("Test: " + className + "#" + methodName);
        System.out.println("Result: " + (result.wasSuccessful() ? "PASSED" : "FAILED"));
        System.out.println("Run time: " + result.getRunTime() + "ms");

        if (!result.wasSuccessful()) {
            for (Failure f : result.getFailures()) {
                System.out.println("Failure: " + f.getMessage());
                System.out.println("Exception: " + f.getException().getClass().getName());
            }
        }
    }
}
