package net.fosky.cjlu.wifi.cmcc.utils;

import android.annotation.SuppressLint;
import android.widget.TextView;

public class Log {
    @SuppressLint("StaticFieldLeak")
    public static TextView LogView;
    public static void create(TextView LogTextView) {
        LogView = LogTextView;
    }
    public static void add(String string) {
        LogView.append(string + "\n");
    }
}
