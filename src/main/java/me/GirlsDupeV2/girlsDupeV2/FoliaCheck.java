package me.GirlsDupeV2.girlsDupeV2;

public class FoliaCheck {
    private static Boolean isFolia = null;

    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
        }
        return isFolia;
    }
}