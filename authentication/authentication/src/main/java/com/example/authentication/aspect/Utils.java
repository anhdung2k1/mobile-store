package com.example.authentication.aspect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Utils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    public static Date stringToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return dateFormat.parse(dateStr);
            } else {
                logger.warning("Date string does not match the expected format: " + dateStr);
            }
        } catch (ParseException e) {
            logger.warning("Error parsing date: " + e.getMessage());
        }

        return null;
    }
}
