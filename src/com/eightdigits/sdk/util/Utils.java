package com.eightdigits.sdk.util;

import java.util.Random;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author gurkanoluc
 */
public class Utils {

    public static String intToString(int value) {
        return new Integer(value).toString();
    }

    public static String getStringFromJsonObject(JSONObject jsonObject, String field) {
        String result = null;
        try {
            result = jsonObject.getString(field);
        } catch (JSONException e) {
            
        }
        return result;
    }

    public static Vector getVectorFromJsonObject(JSONObject jsonObject, String field) {
        Vector result = null;
        try {
            result = (Vector) jsonObject.get(field);
        } catch (JSONException e) {

        }
        return result;
    }

    /**
     * Generates uniq ID
     */
    public static String generateUniqId() {
        String uniqId = "";
        String[] chars = { "a", "b", "c", "d", "e", "A", "B", "C", "D", "E", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 6; j++) {
                uniqId += chars[random.nextInt(chars.length - 1)];
            }

            if(i < 3)
                uniqId += "-";
        }
        return uniqId;

    }


}

