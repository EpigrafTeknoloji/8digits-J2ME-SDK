package com.eightdigits.sdk;

import com.eightdigits.sdk.util.UrlEncoder;
import com.eightdigits.sdk.util.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import net.sf.microlog.core.Appender;
import net.sf.microlog.core.Logger;
import net.sf.microlog.core.LoggerFactory;
import net.sf.microlog.core.appender.ConsoleAppender;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author gurkanoluc
 */
public class EightDigitsClient {

    private static EightDigitsClient instance;
    private String urlPrefix;
    private String trackingCode;
    private String visitorCode;
    private String authToken;
    private String sessionCode;
    private String hitCode;
    private String username;
    private String password;
    private MIDlet middlet;
    private Logger logger;
    private Appender logAppender;

    /**
     * Constructor
     */
    private EightDigitsClient(MIDlet middlet, String urlPrefix, String trackingCode) {
        this.initLogger();
        this.setUrlPrefix(urlPrefix);
        this.setTrackingCode(trackingCode);
        this.setVisitorCode(Utils.generateUniqId());
        this.middlet = middlet;
    }

    public static EightDigitsClient getInstance() {
        return EightDigitsClient.instance;
    }

    /**
     * Creates new instance of 8digits client
     *
     * @param   middlet      MIDlet  Active middlet that user is viewing
     * @param   urlPrefix    String  API URL that you can get from 8digits.
     * @param   trackingCode String  Your tracking code in 8digits.
     *
     * @return EightDigitsClient
     */
    public static EightDigitsClient newInstance(MIDlet middlet, String urlPrefix, String trackingCode) {
        if (EightDigitsClient.instance == null) {
            EightDigitsClient.instance = new EightDigitsClient(middlet, urlPrefix, trackingCode);
        }
        return EightDigitsClient.instance;
    }

    /**
     * Getting auth token from API by authenticating with your username and password
     *
     * @param   username    String   Your 8digits.com username
     * @param   password    Strin    Your 8digits.com password
     *
     * @return boolean Returns true if authentication is succeed returns true, otherwise returns false
     */
    public boolean authWithUsername(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);

        Hashtable params = new Hashtable(2);
        params.put(Constants.USERNAME, this.getUsername());
        params.put(Constants.PASSWORD, this.getPassword());

        JSONObject response = this.apiRequest("/api/auth", params);
        if (response != null) {
            String _authToken = Utils.getStringFromJsonObject(response, Constants.AUTH_TOKEN);

            if (_authToken != null) {
                this.setAuthToken(_authToken);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates new visit. You should call this method, when application is first opened.
     * To create another hit in other screen you should use newScreen.
     *
     * @param   title   String  Title of your screen
     * @param   path    String  Path of your screen. You can give anything you want.
     *
     * @return  boolean
     */
    public boolean newVisit(String title, String path) {
        String model = "Linux";
        String userAgent = "Mozilla/5.0 (" + model + "; U; " + "J2ME ;" + System.getProperty("microedition.platform");

        /*
         * if (systemVersion >= 10) { userAgent += " " + android.os.Build.SERIAL; }
         */

        userAgent += " like Mac OS X; en-us) AppleWebKit (KHTML, like Gecko) Mobile/8A293 Safari";
        Display display = Display.getDisplay(this.middlet);
        Displayable displayable = display.getCurrent();
        
        int width = displayable.getWidth();
        int height = displayable.getHeight();
        String locale = System.getProperty("microedition.locale");

        Hashtable params = new Hashtable();
        params.put(Constants.AUTH_TOKEN, this.getAuthToken());
        params.put(Constants.TRACKING_CODE, this.getTrackingCode());
        params.put(Constants.VISITOR_CODE, this.getVisitorCode());
        params.put(Constants.PAGE_TITLE, title);
        params.put(Constants.PATH, path);
        params.put(Constants.SCREEN_WIDTH, "" + width);
        params.put(Constants.SCREEN_HEIGHT, "" + height);
        params.put(Constants.COLOR, "24");
        params.put(Constants.ACCEPT_LANG, locale);
        params.put(Constants.FLASH_VERSION, "0.0.0");
        params.put(Constants.JAVA_ENABLED, "false");
        params.put(Constants.USER_AGENT, userAgent);
        params.put(Constants.DEVICE, "J2ME");
        params.put(Constants.VENDOR, "J2ME");
        params.put(Constants.MODEL, System.getProperty("microedition.platform"));

        JSONObject response = this.apiRequest("/api/visit/create", params);

        if(response != null) {
            String _hitCode = Utils.getStringFromJsonObject(response, Constants.HIT_CODE);
            String _sessionCode = Utils.getStringFromJsonObject(response, Constants.SESSION_CODE);
            
            this.setHitCode(_hitCode);
            this.setSessionCode(_sessionCode);
            return true;
        }
        
        return false;
    }

    /**
     * Creates new hit, you don't have to call this method if you called
     * newVisit method.
     *
     * @param   title   String  Title of your screen
     * @param   path    String  Path of your screen. You can give anything you want.
     *
     * @return  boolean
     */
    public void newScreen(String title, String path) {
        Hashtable params = new Hashtable(6);
        params.put(Constants.AUTH_TOKEN, this.getAuthToken());
        params.put(Constants.TRACKING_CODE, this.getTrackingCode());
        params.put(Constants.VISITOR_CODE, this.getVisitorCode());
        params.put(Constants.SESSION_CODE, this.getSessionCode());
        params.put(Constants.PAGE_TITLE, title);
        params.put(Constants.PATH, path);

        JSONObject response = this.apiRequest("/api/hit/create", params);

        if (response != null) {
            String newHitCode = Utils.getStringFromJsonObject(response, Constants.HIT_CODE);

            if (newHitCode != null) {
                this.setHitCode(hitCode);
            }
        }
    }

    /**
     * Sends new event.
     * 
     * @param   String  key     Key of your event.
     * @param   String  value   Value of your event.
     *
     * @return void
     *
     */
    public void newEvent(String key, String value) {
        Hashtable params = new Hashtable(6);
        params.put(Constants.AUTH_TOKEN, this.getAuthToken());
        params.put(Constants.TRACKING_CODE, this.getTrackingCode());
        params.put(Constants.VISITOR_CODE, this.getVisitorCode());
        params.put(Constants.SESSION_CODE, this.getSessionCode());
        params.put(Constants.HIT_CODE, this.getHitCode());
        params.put(Constants.KEY, key);
        params.put(Constants.VALUE, value);

        this.apiRequest("/api/event/create", params);
    }

    /**
     * Gets score of current user
     *
     * @return  String   score
     */
    public String score() {
        Hashtable params = new Hashtable(3);
        params.put(Constants.AUTH_TOKEN, this.getAuthToken());
        params.put(Constants.TRACKING_CODE, this.getTrackingCode());
        params.put(Constants.VISITOR_CODE, this.getVisitorCode());
        JSONObject response = this.apiRequest("/api/visitor/score", params);

        if (response != null) {
            String score = Utils.getStringFromJsonObject(response, Constants.SCORE);
            return score;
        }

        return null;
    }

    /**
     * Returns badges of user as Vector list.
     *
     * @return  Vector  list of badges
     */
    public Vector badges() {
        Hashtable params = new Hashtable(3);
        params.put(Constants.AUTH_TOKEN, this.getAuthToken());
        params.put(Constants.TRACKING_CODE, this.getTrackingCode());
        params.put(Constants.VISITOR_CODE, this.getVisitorCode());
        JSONObject response = this.apiRequest("/api/visitor/badges", params);

        if (response != null) {
            Vector badges = Utils.getVectorFromJsonObject(response, Constants.BADGES);
            return badges;
        }

        return null;

    }

    /**
     * Returns hashtable elements to query string with encoding as UTF-8
     */
    private String hashtableToQueryString(Hashtable params) {
        String qs = "";
        int keysCount = params.size();

        int i = 0;

        Enumeration e = params.keys();

        while (e.hasMoreElements()) {
            i++;
            String key = (String) e.nextElement();
            String value = (String) params.get(key);
            qs += UrlEncoder.encode(key) + "=" + UrlEncoder.encode(value);

            if (i < keysCount) {
                qs += "&";
            }
        }

        this.logger.debug("qs = " + qs);
        return qs;
    }

    /**
     * HTTP Request method
     */
    private JSONObject apiRequest(String path, Hashtable params) {
        String url = this.getUrlPrefix() + path;
        this.logger.debug("URL = " + url);
        byte[] data = null;
        InputStream httpInputStream = null;
        String apiResponse = null;
        try {
            this.logger.debug("Setting http connection");
            HttpConnection http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(HttpConnection.POST);
            http.setRequestProperty(Constants.CONTENT_TYPE, "application/x-www-form-urlencoded");
            http.setRequestProperty(Constants.USER_AGENT_HEADER, "HttpMidlet/0.2");

            String msg = hashtableToQueryString(params);
            http.setRequestProperty(Constants.CONTENT_LENGTH, Utils.intToString(msg.getBytes().length));
            OutputStream httpOutputStream = http.openOutputStream();
            httpOutputStream.write(msg.getBytes());

            this.logger.debug("Http response code = " + http.getResponseCode());

            if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                int len = (int) http.getLength();
                httpInputStream = http.openInputStream();

                if (httpInputStream == null) {
                    throw new IOException("Cannot open HTTP InputStream, aborting");
                }

                apiResponse = fetchResponseFromRequestInputStream(httpInputStream);
            }
            http.close();
        } catch (IOException e) {
            this.logger.error(e);
        }

        JSONObject response = this.parseResult(apiResponse);
        return response;
    }

    /**
     * Fetching response from http request input stream
     */
    private String fetchResponseFromRequestInputStream(InputStream is) {
        byte[] data = null;
        String response = "";
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        int ch;
        int count = 0;
        try {
            while ((ch = is.read()) != -1) {
                bo.write(ch);
                count++;
            }
            data = bo.toByteArray();
            bo.close();
            response = new String(data);
        } catch (IOException e) {
            this.logger.error(e);
        }
        this.logger.debug("API Result = " + response);
        return response;
    }

    /**
     * Parses JSON response and returns value of data key as JSONObject
     */
    private JSONObject parseResult(String apiResponse) {
        JSONObject response = null;

        if (apiResponse != null) {
            try {
                JSONObject apiResponseAsJson = new JSONObject(apiResponse);
                String responseCode = ((JSONObject) apiResponseAsJson.get(Constants.RESULT)).getString(Constants.CODE);

                // Successful request
                if (responseCode.equals("0") && apiResponseAsJson.has(Constants.DATA) ) {
                    response = (JSONObject) apiResponseAsJson.get(Constants.DATA);
                }
            } catch (JSONException e) {
                this.logger.error(e);
            }

        }

        return response;
    }

    private void initLogger() {
        this.logger = LoggerFactory.getLogger(EightDigitsClient.class);
        this.logAppender = new ConsoleAppender(System.out);
        this.logger.addAppender(this.logAppender);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getVisitorCode() {
        return visitorCode;
    }

    public void setVisitorCode(String visitorCode) {
        this.visitorCode = visitorCode;
    }

    public String getHitCode() {
        return hitCode;
    }

    public void setHitCode(String hitCode) {
        this.hitCode = hitCode;
    }
}