package sample;
import robocode.JuniorRobot;
public class Mambo extends JuniorRobot {
    public void onHitByBullet() {}
    public void onHitWall() {}
    public void onScannedRobot() {}
    public void run() {
        setColors(green,yellow,white,blue,black);
        turnAheadRight(1,180);
        for (int times=0; times<5; times++) {
            turnLeft(30);
            turnRight(30);
        }
        turnAheadRight(1,180);
        for (int times=0; times<5; times++) {
            turnLeft(30);
            turnRight(30);
        }
    }
}
