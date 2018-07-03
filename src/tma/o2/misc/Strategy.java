package tma.o2.misc;

public class Strategy {
    public static enum MOVE_STRATEGY {
        RANDOMIZE, TRACKER, 
    }
    
    public static enum FIRE_STRATEGY {
        FIRE_STRAIGHT, FIRE_NEAR, FIRE_LINEAR
    }
}
