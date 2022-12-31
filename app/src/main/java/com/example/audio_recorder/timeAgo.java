package com.example.audio_recorder;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class timeAgo {

    public String getTimeAgo(long duration) {
        Date now= new Date();

        long seconds= MILLISECONDS.toSeconds(now.getTime()-duration);
        long minutes= MILLISECONDS.toMinutes(now.getTime()-duration);
        long hours= MILLISECONDS.toHours(now.getTime()-duration);
        long days= MILLISECONDS.toDays(now.getTime()-duration);

        if(seconds<60){
            return "just now";
        } else if(minutes==1){
            return "a minute ago";
        } if(minutes>1 && minutes<60){
            return minutes + " minutes ago";
        }else if(hours==1){
            return "an hour ago";
        }if(hours>1 && hours<24){
            return hours + " hours ago";
        }if(days==1){
            return "a day ago";
        }else {
            return days + " days ago";
        }
    }
}
