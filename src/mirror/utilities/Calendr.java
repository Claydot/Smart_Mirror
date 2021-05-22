package mirror.utilities;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Calendr {

    private static long weekMs = 86400000 * 7; //ms in a week


    public static Calendar get_cal(String url) {
        try {
            //https://calendar.google.com/calendar/ical/bv8f27bcpvmhfpkru70smsr1dc%40group.calendar.google.com/private-f64d67c4f772f38cc4ced4e21f2ff8e3/basic.ics
            InputStream is = new URL(url).openStream();

            try {

                Calendar cal = new CalendarBuilder().build(is);
                ComponentList c = cal.getComponents();
                return cal;
            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {
                is.close();
            }
        } catch(Exception e) {

        }

        return null;
    }

    public static ArrayList<CalObject> cal_format(String url) {
        Calendar cal = get_cal(url);
        ComponentList c = cal.getComponents();
        ArrayList<CalObject> returnList = new ArrayList<>();
        //create data structure for each day
        //parse through component list and look at reoccurance table
        //add to reoccurence table if today
        //need to create rule for

        //get current system time in ms and get 24 hr later
        long startT = System.currentTimeMillis();
        long endT = startT + 86400000;




        Iterator<Component> it = c.iterator();
        Component ev = it.next();
        while (ev != null) {
            if (ev.getName().equals("VEVENT")) {

                DtStart start = ev.getProperty("DTSTART"); //get start time
                DtEnd end = ev.getProperty("DTEND"); //get end time
                RRule rule = ev.getProperty("RRULE"); //get reoccurence para
                Summary sum = ev.getProperty("SUMMARY"); //get summary


                if (rule != null) {
                    long interVal = rule.getRecur().getFrequency().ordinal();
                    if (interVal == 4) {
                        interVal = weekMs;
                    }//check if is a week
                    long endRec = endT + 1;
                    if (rule.getRecur().getUntil() != null) {
                        endRec = rule.getRecur().getUntil().getTime();
                    }
                    if (endRec > endT) {
                        //check interVal
                        long intevals = (endT - start.getDate().getTime()) / interVal; //floors week count
                        long actualTime = (interVal * intevals) + start.getDate().getTime();
                        if (actualTime > startT && actualTime < endT) {
                            //add to day list
                            long dif = end.getDate().getTime() - start.getDate().getTime();
                            //issues with date truncating values
                            DateTime t = new DateTime(actualTime);
                            returnList.add(new CalObject(sum.getValue(), new DateTime(actualTime), new DateTime(actualTime + dif)));




                            //returnList.add(new CalObject(sum.getValue(), start.getDate(), end.getDate()));
                            //add rest of code here
                            //need to create custom class with custom compareTo()
                        }
                    }

                } else {
                    //one time occurence

                    if (start.getDate().getTime() > startT && start.getDate().getTime() < endT) {
                        //add to day list
                        returnList.add(new CalObject(sum.getValue(), start.getDate(), end.getDate()));


                    }

                }
            }
            int l = 0;
            if (it.hasNext()) {
                ev = it.next();
            } else {
               ev = null;
            }
        }
        Collections.sort(returnList);
        return returnList;
    }
/**
    public static void main(String... args) {
    cal_format();
    }
*/
}
