package com.eightdigits.sdk.util;

import com.eightdigits.sdk.Constants;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.sf.microlog.core.Logger;

/**
 *
 * @author gurkanoluc
 */
public class UniqIdentifier {

    private static RecordStore recordStore;
    private static Logger logger;

    public static synchronized String id(Logger logger, String trackingCode) {
        try {
            recordStore = RecordStore.openRecordStore(Constants.EIGHT_DIGITS, true);
            UniqIdentifier.logger = logger;
            String id = get();

            if (id == null) {
                id = createAndSave(trackingCode);
            }
            id = formatId(id);
            return id;
        } catch (RecordStoreException e) {
        }

        return null;
    }

    private static String formatId(String id) {
        id = id.substring((Constants.UNIQ_ID + "|").length());
        return id;
    }

    private static String createAndSave(String trackingCode) {
        try {
            String uniqId = Constants.UNIQ_ID + "|" + Utils.generateUniqId();
            byte[] uniqIdInBytes = uniqId.getBytes();
            recordStore.addRecord(uniqIdInBytes, 0, uniqIdInBytes.length);
            return uniqId;
        } catch (RecordStoreException e) {
            logger.error(e);
        }
        return null;
    }

    private static String get() {
        try {
            RecordEnumeration re = recordStore.enumerateRecords(null, null, true);

            while (re.hasNextElement()) {
                byte[] recordData = re.nextRecord();
                String recordValue = new String(recordData);

                if (recordValue.startsWith(Constants.UNIQ_ID)) {
                    return recordValue;
                }
            }

        } catch (RecordStoreException e) {
            logger.error(e);
        }

        return null;
    }
}
