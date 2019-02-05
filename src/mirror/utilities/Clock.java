package mirror.utilities;
// List of time servers: http://tf.nist.gov/service/time-servers.html

import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * IT WORKS!!!!
 */

public class Clock {

    public static final String TIME_SERVER = "time.nist.gov";

    public Clock() {
        NTPUDPClient timeClient = new NTPUDPClient();
        try {

            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            long returnTime = timeInfo.getReturnTime();
            Date time = new Date(returnTime);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static Date getTime() {
            NTPUDPClient timeClient = new NTPUDPClient();
            try {
                timeClient.open();
                timeClient.setSoTimeout(5000);
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                long returnTime = timeInfo.getReturnTime();
                Date time = new Date(returnTime);
                timeClient.close();
                return time;
            } catch(Exception e) {
                timeClient.close();
                System.out.println(e.getMessage());
            }
            return new Date(0);
    }
}
