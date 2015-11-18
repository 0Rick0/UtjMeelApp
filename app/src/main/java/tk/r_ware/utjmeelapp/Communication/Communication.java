package tk.r_ware.utjmeelapp.Communication;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Rick on 17-11-2015.
 */
public class Communication {
    private static final String WEB_ADDR = "http://RICK-PC/";
    private static final String filename = "config.json";

    //singleton
    private static Communication instance;
    public Communication getInstance(){
        if(instance == null){
            instance = new Communication();
        }
        return instance;
    }

    //store login info
    private String username;
    private String token;
    private Date expirationDate;
    private boolean loggedIn;

    private String lastError;

    public boolean tryLoadInfo(Context context){

        File file = new File(context.getFilesDir(), filename);

        if(!file.exists())return false;
        String text;
        try {
            InputStream is = new FileInputStream(file);
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            text = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            JSONObject obj = new JSONObject(text);
            username = obj.getString("username");
            token = obj.getString("token");
            loggedIn = obj.getBoolean("loggedIn");
            DateFormat format = SimpleDateFormat.getDateInstance();
            expirationDate = format.parse(obj.getString("expirationDate"));
        } catch (JSONException e) {
            e.printStackTrace();
            loggedIn = false;
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            loggedIn = false;
            return false;
        }
        return true;
    }

    public boolean trySaveInfo(Context context){
        File file = new File(context.getFilesDir(), filename);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("username",username);
            obj.put("token",token);
            obj.put("loggedIn",loggedIn);
            obj.put("expirationDate", expirationDate.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        try {
            OutputStream os = new FileOutputStream(file);
            os.write(obj.toString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getLastError(){
        return lastError;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    /**
     * login to the server
     * @param username the username of the person that wan\'ts to login
     * @param password the password of the person that wan\'ts to login
     * @return if the login was successful, if not an error text can be obtained with {@code getLastError()}
     * */
    public boolean login(String username, String password){
        String addr = WEB_ADDR + "login.php";
        Map<String,String> data = new HashMap<>();
        data.put("username",username);
        data.put("password",password);
        JSONObject result = doPostRequest(addr,data);
        try {
            int error = result.getInt("error");
            if(error!=0){
                lastError = result.getString("error_text");
            }
            this.username = result.getString("username");
            this.token = result.getString("session_id");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            this.expirationDate = format.parse(result.getString("expiration_date-date"));
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "JSON read error";
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            lastError = "Date parse error";
            return false;
        }
        //todo save config?
        return true;
    }

    private JSONObject doGetRequest(String addr){
        HttpURLConnection c = null;
        try {
            URL u = new URL(addr);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return new JSONObject(sb.toString());
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject ret = null;
        try {
            ret = new JSONObject().put("error", -5).put("error_text","Failed to connect");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private JSONObject doPostRequest(String addr, Map<String,String> vars){
        HttpURLConnection c = null;
        try {
            URL u = new URL(addr);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            StringBuilder vb = new StringBuilder();
            int i = 0;
            //build an query string
            for(Map.Entry<String,String> kvp : vars.entrySet()){
                vb.append(kvp.getKey()).append("=").append(kvp.getValue());
                i++;
                if(i!=vars.size()){
                    vb.append("&");
                }
            }
            writer.write(vb.toString());

            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return new JSONObject(sb.toString());
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject ret = null;
        try {
            ret = new JSONObject().put("error", -5).put("error_text","Failed to connect");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
