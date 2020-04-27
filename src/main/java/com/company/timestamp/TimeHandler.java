package com.company.timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeHandler {

    List<Time> stamps;

    private long start;
    private long end;

    private TimeHandler() {
        stamps = new ArrayList<Time>();
    }

    private static TimeHandler th;
    public static TimeHandler getInstance() {
        if(th == null) {
            th = new TimeHandler();
        }
        return th;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void saveTimeStamp() {
        var date = LocalDate.now();

        boolean found = false;
        for(Time t: this.stamps) {
            if(LocalDate.parse(t.getDay()).equals(date)) {

                t.setTime((int) (t.getTime() + ((end - start) / 1000)));
                found = true;
            }
        }
        if(!found) {
            Time t = new Time();
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            t.setDay(formattedDate);
            t.setTime((int) ((end - start) / 1000.0));
            this.stamps.add(t);
        }

        ObjectMapper om = new ObjectMapper();

        try {
            om.writeValue(
                    new FileOutputStream("./data.json"), this.stamps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreTimeStamp() {
        ObjectMapper om = new ObjectMapper();
        Time[] times = null;
        try {
            times = om.readValue(new FileInputStream("./data.json"), Time[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.addAll(this.stamps, times);
    }

    public List<Time> getStamps() {
        return stamps;
    }
}
