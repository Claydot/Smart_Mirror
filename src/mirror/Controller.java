package mirror;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import mirror.utilities.*;

import java.util.Calendar;
import java.util.Date;

public class Controller {
    //necessary fxml to edit
    static long refresh_rate = 60000; //1 min
    static Scene scene;



    public Controller() {

    }


    public static void start(Scene start_scene) {
        scene = start_scene;
        loop();
    }


    private static void loop() {
        boolean trip = true;
        long start = System.currentTimeMillis();
        boolean refresh = true;
        Date time = Clock.getTime();




        while(trip) {
            //refresh once a minute
            if ( ((System.currentTimeMillis() - start) % refresh_rate) == 0) {
                refresh = true;
                long tim = time.getTime();
                time.setTime(tim + (System.currentTimeMillis() - start));
                //set objects up for clock
               Text b = (Text) scene.lookup("#clock");
               b.setText(Long.toString(tim));
            }

            refresh = false;
        }
    }



    // TableView tb = (TableView) scene.lookup("#history");

    //this.widgetDoc.getElementById("legendNode").appendChild(myNode);
    //
    // <legend id="legendNode">



}
