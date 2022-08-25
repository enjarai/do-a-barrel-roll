package nl.enjarai.doabarrelroll.config;

public class Sensitivity {
    public double pitch;
    public double yaw;
    public double roll;

    public Sensitivity(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public static Sensitivity identity() {
        return new Sensitivity(1, 1, 1);
    }
}
