package inspection.format.complicated_format;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

public class Main {

    public static void main(String... args) {
        Calendar c = new GregorianCalendar(1995, MAY, 23);
        String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
        // "Duke's Birthday: May 23, 1995"
    }

}
