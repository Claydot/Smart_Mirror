package mirror.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Bible {


    public static String getBible() {
        String value = "";
        try {
            String url = "https://beta.ourmanna.com/api/v1/get/?format=text"; // or whatever goes here
            Document document = Jsoup.connect(url).followRedirects(false).timeout(10000/*wait up to 60 sec for response*/).get();
            value = document.body().html();
        } catch(Exception e) {

        }
        return value;
    }
}
