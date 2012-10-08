package com.eightdigits.sdk.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author gurkanoluc
 */
public class UrlEncoder {
    /**
     * Encodes as UTF-8
     */
    public static String encode(String s) {
        try {
            return encode(s, "UTF-8");
        } catch(IOException e) {
            // Do nothing..
        }
        return null;
    }
    
    public static String encode(String s, String enc) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(bOut);
        StringBuffer ret = new StringBuffer(); //return value
        dOut.writeUTF(s);
        ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
        bIn.read();
        bIn.read();
        int c = bIn.read();
        while (c >= 0) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '*' || c == '_') {
                ret.append((char) c);
            } else if (c == ' ') {
                ret.append('+');
            } else {
                if (c < 128) {
                    appendHex(c, ret);
                } else if (c < 224) {
                    appendHex(c, ret);
                    appendHex(bIn.read(), ret);
                } else if (c < 240) {
                    appendHex(c, ret);
                    appendHex(bIn.read(), ret);
                    appendHex(bIn.read(), ret);
                }
            }
            c = bIn.read();
        }
        return ret.toString();
    }

    private static void appendHex(int arg0, StringBuffer buff) {
        buff.append('%');
        if (arg0 < 16) {
            buff.append('0');
        }
        buff.append(Integer.toHexString(arg0));
    }
}
