package com.mightyracing;

import java.time.Duration;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class MightyQualiTime {
   public int m;
   public int s;
   public MightyQualiTime(Duration delta) {
      int fs = delta.toSecondsPart();
      int fm = delta.toMinutesPart();
      int fh = delta.toHoursPart();
      if (fh >= 1){
         fm = 60;
      }
      boolean min = delta.isNegative();
      this.m = (min ? 0 : fm);
      this.s = (min ? 0 : fs);
   }
   public MightyQualiTime(int m) {
      this.m = m;
      this.s = 0;
   }
   public String getString(){
      if (this.m == 0 && this.s == 0){
         return "§8 0:00";
      }else{
         String time = "";
         if (this.m<2 || (this.m==2 && this.s == 0)){
            time+="§4";
         }else{
            time+="§f";
         }
         if (this.m < 10){time += " ";}
         time += this.m + ":";
         if (this.s < 10){time += "0";}
         time += this.s;
         return time;
      }
   }
}
