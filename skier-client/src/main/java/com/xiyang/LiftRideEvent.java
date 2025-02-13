package com.xiyang;

import java.util.concurrent.ThreadLocalRandom;

public class LiftRideEvent {
    private final int skierID;
    private final int resortID;
    private final int liftID;
    private final String seasonID;
    private final String dayID;
    private final int time;


    public LiftRideEvent() {
        this.skierID = ThreadLocalRandom.current().nextInt(1, 100001);  // 1 to 100000
        this.resortID = ThreadLocalRandom.current().nextInt(1, 11);     // 1 to 10
        this.liftID = ThreadLocalRandom.current().nextInt(1, 41);       // 1 to 40
        this.seasonID = "2025";
        this.dayID = "1";
        this.time = ThreadLocalRandom.current().nextInt(1, 361);        // 1 to 360
    }

    // Getters
    public int getSkierID() { return skierID; }
    public int getResortID() { return resortID; }
    public int getLiftID() { return liftID; }
    public String getSeasonID() { return seasonID; }
    public String getDayID() { return dayID; }
    public int getTime() { return time; }

    // Create POST request json data
    public String toJson() {
        return String.format("{\"time\": %d, \"liftID\": %d}", time, liftID);
    }

    // Create URL
    public String getUrlPath() {
        return String.format("/skiers/%d/seasons/%s/days/%s/skiers/%d",
                resortID, seasonID, dayID, skierID);
    }
}
