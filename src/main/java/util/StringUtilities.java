package util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StringUtilities {

    public static String getTodaysDateTimeFormatted(String formateOfDateTime, ZoneOffset zoneOffset){
        return DateTimeFormatter.ofPattern(formateOfDateTime).withZone(zoneOffset).format(new Date().toInstant());
    }

}
