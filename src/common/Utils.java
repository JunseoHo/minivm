package common;

import java.util.List;

public class Utils {

    public static boolean sleep(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static String list(List<?> list) {
        String str = "";
        for (Object o : list) str += o.toString() + " ";
        return str;
    }

}
