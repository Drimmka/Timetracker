package timetracker.com;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Convenience class for handling date/time string presentation
 */

public class StringFormatUtils {

    public static final String FORMAT_TOTAL_WORK_TIME = "%d hrs, %d min";
    public static final String FORMAT_TIME_ENTRY = "dd/MM/yyyy HH:mm";

    /**
     * format work duration in hours/minutes
     * @param totalWorkTime in miliseconds
     * @param format
     * @return
     */
    public static String formatWorkDuration(long totalWorkTime, String format){
        return String.format(format,
                TimeUnit.MILLISECONDS.toHours(totalWorkTime),
                TimeUnit.MILLISECONDS.toMinutes(totalWorkTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalWorkTime))
        );

    }

    /**
     * construct date/hour string from miliseconds
     * @param time
     * @param format
     * @return
     */
    public static String formatEntryTime(long time, String format){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }
}
