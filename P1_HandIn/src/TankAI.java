import processing.core.PVector;

public class TankAI {
    private float currentAngle = 255;
    private float power = 100;

    public PVector makeShot() {
        double angle = Math.toRadians(currentAngle);

        PVector dir = PVector.fromAngle((float) angle);

        PVector dirNorm = dir.get();
        return dirNorm.normalize().mult(power);
    }

    public void updateShotParameters(PVector loc, Tank opp) {
        if (loc.x < opp.getCurrentX()) {
            power -= power*0.2;
            currentAngle += 2;
        } else {
            power += power*0.2;
            currentAngle -= 2;
        }
        System.out.println("Angle: " + currentAngle + "\nPower: " + power);
    }

    public void checkToMove(PVector loc, Tank me) {
        float dist = me.getCurrentX() - loc.x;

        if (Math.abs(dist) < 200) {
            if (dist > 0) {
                if (me.getCurrentCol() != 9) {
                    me.move(1);
                } else {
                    me.move(-1);
                }
            } else {
                me.move(-1);
            }
        }
    }

    public void resetAI() {
        currentAngle = 255;
        power = 50;
    }
}
