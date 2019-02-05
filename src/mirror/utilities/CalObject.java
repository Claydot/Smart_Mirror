package mirror.utilities;


import net.fortuna.ical4j.model.Date;

public class CalObject implements Comparable<CalObject> {
    public Date start;
    public Date end;
    public String eventName;

    public CalObject(String name, Date start, Date end) {
        eventName = name;
        this.start = start;
        this.end = end;
    }

    public int compareTo(CalObject o) {
        return this.start.compareTo(o.start);
    }
}
