package tk.r_ware.utjmeelapp.Communication;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

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

import tk.r_ware.utjmeelapp.Communication.containers.Info;
import tk.r_ware.utjmeelapp.Communication.containers.Statistics;
import tk.r_ware.utjmeelapp.Communication.containers.Transactions;

/**
 * Created by Rick on 17-11-2015.
 */
public class Communication {
    private static final String WEB_ADDR = "http://utjmeel.nl/API/V1.0/";
    private static final String filename = "config.json";

    //singleton
    private static Communication instance;
    public static Communication getInstance(){
        if(instance == null){
            instance = new Communication();
        }
        return instance;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    //store login info
    private String username;
    private String token;
    private Date expirationDate;
    private boolean loggedIn;

    private Info cachedInfo = null;

    private String lastError;

    /**
     * try to load the info to a local file
     * @param context the context in which to load the file
     */
    public boolean tryLoadInfo(Context context){

        File file = new File(context.getFilesDir(), filename);

        if(!file.exists())return false;
        String text;
        try {
            InputStream is = new FileInputStream(file);

            text = convertStreamToString(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            JSONObject obj = new JSONObject(text);
            username = obj.getString("username");
            token = obj.getString("token");
            loggedIn = obj.getBoolean("loggedIn");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);;
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
        isLoggedIn();
        return true;
    }

    /**
     * try to save the info to a local file
     * @param context the context in which to save the file
     */
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
            obj.put("token", token);
            obj.put("loggedIn", loggedIn);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);

            obj.put("expirationDate", format.format(expirationDate));
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

    /**
     * get the last occurred error details
     * @return the error
     */
    public String getLastError(){
        return lastError;
    }

    public String getUsername(){
        return username;
    }

    public Info getCachedInfo(){
        return cachedInfo;
    }

    /**
     * Check if the user is logged in to the server
     * @return true if the user is logged in
     */
    public boolean isLoggedIn(){
        if(expirationDate == null){
            return false;
        }
        if(expirationDate.before(new Date())){//expiration date before now (now is after the expiration date)
            loggedIn = false;
        }
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
        data.put("username", username);
        data.put("password", password);
        JSONObject result = doPostRequest(addr, data);
        try {
            int error = result.getInt("error");
            if(error!=0){
                lastError = result.getString("error_text");
            }
            this.username = result.getString("username");
            this.token = result.getString("session_id");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            this.expirationDate = format.parse(result.getString("expiration_date"));
            this.loggedIn = true;
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "JSON read error";
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            lastError = "Date parse error";
            return false;
        }

        return true;
    }

    /**
     * Refresh the authorisation token
     * @return if the renew was successful
     */
    public boolean refreshToken(){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("renew.php");
        sb.append("?username=").append(username);
        sb.append("&token=").append(token);
        JSONObject result = doGetRequest(sb.toString());
        try {
            if(result.getInt("error")!=0){
                lastError = result.getString("error_text");
                return false;
            }
            username = result.getString("username");
            token = result.getString("session_id");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            this.expirationDate = format.parse(result.getString("expiration_date"));
        } catch (JSONException e) {
            lastError = "JSON exception";
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            lastError = "Date time parser exception";
            return false;
        }
        return true;
    }

    public boolean changePassword(String username, String curpass, String newPass, String newPassRepear){
        Map<String,String> vars = new HashMap<>();
        vars.put("username", username);
        vars.put("currentPassword", curpass);
        vars.put("newPassword", newPass);
        vars.put("newPasswordRepeat", newPassRepear);
        JSONObject result = doPostRequest(WEB_ADDR + "changePassword.php", vars);
        try {
            if(result.getInt("error")!=0){
                lastError = result.getString("error_text");
                return false;
            }
            this.loggedIn=false;
            this.token=null;
            this.expirationDate=null;
            this.username=null;
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "JSON exception";
            return false;
        }
        return true;
    }

    public boolean createAccount(String username, String password, String passwordRepeat, String email){
        Map<String,String> vars = new HashMap<>();
        vars.put("username", username);
        vars.put("password", password);
        vars.put("passwordRepeat", passwordRepeat);
        vars.put("email", email);
        JSONObject result = doPostRequest(WEB_ADDR + "createAccount.php", vars);
        try {
            if(result.getInt("error")!=0){
                lastError = result.getString("error_text");
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "JSON exception";
            return false;
        }
        return true;
    }

    public Info info(){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("info.php?username=").append(username);
        sb.append("&session_id=").append(token);
        JSONObject obj = doGetRequest(sb.toString());
        try {
            if(obj.getInt("error")!=0){
                lastError = obj.getString("error_text");
                return null;
            }

            Gson gson = new Gson();
            cachedInfo = gson.fromJson(obj.toString(),Info.class);
            return cachedInfo;

        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "Error while parsing JSON";
            return null;
        }
    }

    public Statistics statistics(){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("renew.php");
        sb.append("?username=").append(username);
        sb.append("&token=").append(token);
        return statistics(sb.toString());
    }

    public Statistics statistics(String begin, String end){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("renew.php");
        sb.append("?username=").append(username);
        sb.append("&token=").append(token);
        sb.append("&begin=").append(begin);
        sb.append("&end=").append(end);
        return statistics(sb.toString());
    }

    private Statistics statistics(String url){
        JSONObject obj = doGetRequest(url);
        try {
            if(obj.getInt("error")!=0){
                lastError = obj.getString("error_text");
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(obj.toString(),Statistics.class);
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "Error while parsing JSON";
            return null;
        }
    }

    public Transactions transactions(){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("transactions.php?username=").append(username);
        sb.append("&session_id=").append(token);
        return transactions(sb.toString());
    }

    public Transactions transactions(int offset, int amount){
        StringBuilder sb = new StringBuilder();
        sb.append(WEB_ADDR);
        sb.append("transactions.php?username=").append(username);
        sb.append("&session_id=").append(token);
        sb.append("&offset=").append(offset);
        sb.append("&amount=").append(amount);
        return transactions(sb.toString());
    }

    private Transactions transactions(String url) {
        JSONObject obj = doGetRequest(url);
        try {
            if (obj.getInt("error") != 0) {
                lastError = obj.getString("error_text");
                return null;
            }
            Gson gson = new Gson();
            return gson.fromJson(obj.toString(), Transactions.class);
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "Error while parsing JSON";
            return null;
        }
    }

    public boolean transfer(String itemName, int amount, String description){
        Map<String,String> vars = new HashMap<>();
        vars.put("amount", amount + "");
        vars.put("itemName", itemName);
        vars.put("description", description);
        return transfer(vars);
    }

    public boolean transfer(String itemName, int amount,String sourceName, String description){
        Map<String,String> vars = new HashMap<>();
        vars.put("amount", amount + "");
        vars.put("itemName", itemName);
        vars.put("sourceName", sourceName);
        vars.put("description", description);
        return transfer(vars);
    }

    public boolean transferT(String itemName, int amount,String targetName, String description){
        Map<String,String> vars = new HashMap<>();
        vars.put("amount", amount + "");
        vars.put("itemName", itemName);
        vars.put("targetName", targetName);
        vars.put("description", description);
        return transfer(vars);
    }

    public boolean transfer(String itemName, int amount,String sourceName,String targetName, String description){
        Map<String,String> vars = new HashMap<>();
        vars.put("amount", amount + "");
        vars.put("itemName", itemName);
        vars.put("sourceName", sourceName);
        vars.put("targetName", targetName);
        vars.put("description", description);
        return transfer(vars);
    }

    private boolean transfer(Map<String,String> vars){
        vars.put("username",username);
        vars.put("session_id",token);
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
        JSONObject obj = doGetRequest(WEB_ADDR + "transfer.php?"+vb.toString());
        try {
            if(obj.getInt("error")!=0){
                lastError = obj.getString("error_text");
                return false;
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            lastError = "Error while parsing JSON";
            return false;
        }
    }

    /**
     * Do an http GET request to an given address
     * @param addr the address to request to
     * @return the result as an JSON object
     */
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

    /**
     * Do an http POST request to an given address
     * @param addr the address to request to
     * @param vars key value pair of the information in the body
     * @return the result as an JSON object
     */
    private JSONObject doPostRequest(String addr, Map<String,String> vars){
        HttpURLConnection c = null;

        try {
            URL u = new URL(addr);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);

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

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            writer.write(vb.toString());
            writer.flush();

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
