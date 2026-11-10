package com.nhakhoa.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tiện ích định dạng tiền tệ và ngày tháng
 */
public class FormatUtil {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("###,###,### đ");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatMoney(double amount) {
        return MONEY_FORMAT.format(amount);
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATETIME_FORMAT.format(date);
    }

    public static String toDbDate(Date date) {
        if (date == null) return "";
        return DB_DATE_FORMAT.format(date);
    }
}
