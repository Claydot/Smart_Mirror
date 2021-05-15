package mirror;
//import res.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mirror.utilities.*;
import org.xml.sax.XMLReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class Main extends Application {

    public static Stage primaryStage;
    Scene mainS;
    Date time;
    long start;
    private boolean remove_c;
    private boolean remove_t;
    private List<CalObject> today_l;
    private List<CalObject> tomorrow_l;
    private boolean calendar_add;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * this method is called when launching app
     *
     * @param ps
     * @throws Exception
     */
    @Override
    public void start(Stage ps) throws Exception{
        this.primaryStage = ps;
        Parent root = FXMLLoader.load(getClass().getResource("/res/smart_mirror.fxml")); //already updated for path location
        primaryStage.setTitle("Hello World");
        mainS = new Scene(root);
        primaryStage.setScene(mainS);
        //mainS.getStylesheets().add(); //add css style sheet here for Clock
        primaryStage.show();
        //time = Clock.getTime();
        time = new Date(System.currentTimeMillis());
        time.setTime(System.currentTimeMillis());
     //   primaryStage.setFullScreen(true);// works
        //Controller.start(mainS);
        start = System.currentTimeMillis();
        remove_c = false;
        remove_t = false;
        calendar_add = false;
        today_l = new LinkedList<>();
        tomorrow_l = new LinkedList<>();

        app();
    }

    public void app() {

        clockHelper();
        weatherHelper();
        calendarHelper();
        verseOfDayHelper();

        new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(30000); //update every 30 seconds
                } catch (InterruptedException e) {

                }
                clockHelper();
                //check if it is a new day
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000 * 10); //update every 10 minutes
                    //Thread.sleep(30000);
                } catch (InterruptedException e) {

                }
                Platform.runLater(() -> {
                    weatherHelper();
                });
                Platform.runLater(() -> {
                    calendarHelper();
                });
                verseOfDayHelper();
                //update verse only once a day
            }
        }).start();

    }

    public void clockHelper() {
        Text b = (Text) mainS.lookup("#time");

        time.setTime(System.currentTimeMillis());
        String t = time.toString();
        b.setText(t.substring(t.length() - 17, t.length() - 12));

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate= formatter.format(date);

        Text dates = (Text) mainS.lookup("#date");
        dates.setText(strDate);
    }

    public void weatherHelper() {
        //get Weather api
        //from data, determine what gif to display
        ImageView weather = (ImageView) mainS.lookup("#weather_icon");
        Weather.getWeather();
        if (Forecast.icon == null) {
            return;
        }


            //case structure for all things
        if (Forecast.icon.equals("clear-day")) {
            weather.setImage(new Image("res/images/weather/sun.gif"));
        } else if (Forecast.icon.equals("clear-night")) {
            weather.setImage(new Image("res/images/weather/clear_night.gif"));
        } else if (Forecast.icon.equals("rain")) {
            weather.setImage(new Image("res/images/weather/rain.gif"));
        } else if (Forecast.icon.equals("snow")) {
            weather.setImage(new Image("res/images/weather/snow.gif"));
        } else if (Forecast.icon.equals("sleet")) {
            weather.setImage(new Image("res/images/weather/rain.gif"));
            //update
        } else if (Forecast.icon.equals("wind")) {
            weather.setImage(new Image("res/images/weather/wind.gif"));
        } else if (Forecast.icon.equals("fog")) {
            weather.setImage(new Image("res/images/weather/cloudy.gif"));
            //update
        } else if (Forecast.icon.equals("cloudy")) {
            weather.setImage(new Image("res/images/weather/cloudy.gif"));
        } else if (Forecast.icon.equals("partly-cloudy-day")) {
            weather.setImage(new Image("res/images/weather/partly_cloudy.gif"));
        } else if (Forecast.icon.equals("partly-cloudy-night")) {
            weather.setImage(new Image("res/images/weather/partly_cloudy_night.gif"));
        } else {
            weather.setImage(new Image("res/images/weather/thunder_storm.gif"));
        }


        Text sum = (Text) mainS.lookup("#weather_sum");
        sum.setText(Forecast.summary);

        Text temp = (Text) mainS.lookup("#weather_temp");
        temp.setText(Forecast.temp.substring(0, Forecast.temp.length() - 2) + "°");

        Text high = (Text) mainS.lookup("#weather_high");
        high.setText("High: " + Forecast.high.substring(0, Forecast.high.length() - 2) + "°" );

        Text low = (Text) mainS.lookup("#weather_low");
        low.setText("Low: " + Forecast.low.substring(0, Forecast.low.length() - 2) + "°" );


    }

    public void calendarHelper() {

        ArrayList<CalObject> calendar_list = Calendr.cal_format();
        if (calendar_list ==null) {
            return;
        }
        //calendar_list.get(0).start

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        SimpleDateFormat hour_format = new SimpleDateFormat("hh:mma");
        String strDate= formatter.format(date);
        int current_date = Integer.parseInt(strDate);
        Text nt = (Text) mainS.lookup("#nothing_t");
        Text nc = (Text) mainS.lookup("#nothing_c");
        nt.setManaged(true);
        nt.setVisible(true);
        nc.setManaged(true);
        nc.setVisible(true);
        //then update u
        VBox today = (VBox) mainS.lookup("#calendar_today");
        List<Node> t = today.getChildren();
        if (t.size() > 2)  {
            today.getChildren().remove(2, t.size());
            this.remove_c = true;
        }

        VBox tomorrow = (VBox) mainS.lookup("#calendar_tomorrow");
        List<Node> ta = tomorrow.getChildren();
        if (ta.size() > 2)  {
            tomorrow.getChildren().remove(2, ta.size());
            this.remove_t = true;
        }
        if (tomorrow_l != null) tomorrow_l.clear();
        if (today_l != null) today_l.clear();

        for (CalObject cal : calendar_list) {
            String strD = formatter.format(cal.start);
            int start_date = Integer.parseInt(strD);
            if (start_date != current_date) {
                //add to tomorrow
                tomorrow_l.add(cal);
                FXMLLoader cal_o = new FXMLLoader(getClass().getResource("/res/cal_obj.fxml"));
                Pane l = null;
                try {
                    l = cal_o.load();
                } catch(Exception e) {
                }
                Text time = (Text) l.lookup("#time");
                time.setText(hour_format.format(cal.start) + "  " + hour_format.format(cal.end));
                Text st = (Text) l.lookup("#des");
                st.setText(cal.eventName);
                tomorrow.getChildren().add(l);
                nt.setVisible(false);
                nt.setManaged(false);

            } else {
                //add to today
                today_l.add(cal);

                FXMLLoader cal_o = new FXMLLoader(getClass().getResource("/res/cal_obj.fxml"));
                Pane l = null;
                try {
                    l = cal_o.load();
                } catch(Exception e) {

                }
                Text time = (Text) l.lookup("#time");
                time.setText(hour_format.format(cal.start) + "  " + hour_format.format(cal.end));

                Text st = (Text) l.lookup("#des");
                st.setText(cal.eventName);
                today.getChildren().add(l);
                nc.setVisible(false);
                nc.setManaged(false);

                //today counter
            }


        }
       // calendar_add = true;
    }


    public void verseOfDayHelper() {
        String verse = Bible.getBible();
        if (verse == "") {
            return;
        }
        Text b = (Text) mainS.lookup("#verse");
        b.setText(verse);
    }
}
