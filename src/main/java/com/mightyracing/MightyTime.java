package com.mightyracing;

import java.time.Duration;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class MightyTime {
   public int m;
   public int s;
   public int ms;
   public MightyTime(Duration delta) {
      int fms = round((float) delta.toMillisPart() / 50f) * 5;
      int fs = 0;
      if (fms>=100) {
         fms=0;
         fs+=1;
      }
      fs += delta.toSecondsPart();
      int fm = 0;
      if (fs>=60) {
         fs=0;
         fm+=1;
      }
      fm += delta.toMinutesPart();
      boolean max = (delta.toMinutes() >= 10) || (fm >= 10);

      this.m = (max ? 9 : fm);
      this.s = (max ? 59 : fs);
      this.ms = (max ? 95 : fms);
   }
   public MightyTime(int integer) {
      integer = abs(integer);
      this.m = integer / 10000;
      this.s = (integer / 100) % 100;
      this.ms = integer % 100;
   }

   public static boolean compare(MightyTime newtime, MightyTime besttime){
      return newtime.getInt() < besttime.getInt();
   }
   public String getString(){
      String time = this.m + ":";
      if (this.s<10){time+="0";}
      time+=this.s + ":";
      if (this.ms<10){time+="0";}
      time+=this.ms;
      return time;
   }
   public int getInt(){
      return ((this.m * 10000) + (this.s * 100) + this.ms);
   }

   public static String interval(MightyTime time, MightyTime besttime){
      int delta = time.getInt() - besttime.getInt();
      MightyTime mightydelta = new MightyTime(delta);
      String charac = delta >= 0 ? "§c+" : "§a-";
      return charac + mightydelta.getString();
   }
}
