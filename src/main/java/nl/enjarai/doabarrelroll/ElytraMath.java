package nl.enjarai.doabarrelroll;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static nl.enjarai.doabarrelroll.DoABarrelRollClient.TORAD;

public class ElytraMath {
    public static void changeElytraLook(ClientPlayerEntity player, double pitch, double yaw, double roll) {
        Vec3d facing = player.getRotationVecClient();
        DoABarrelRollClient.left = DoABarrelRollClient.left.subtract(facing.multiply(DoABarrelRollClient.left.dotProduct(facing))).normalize();

        // pitch
        facing = rotateAxisAngle(facing, DoABarrelRollClient.left, -0.15 * pitch * TORAD);

        // yaw
        Vec3d up = facing.crossProduct(DoABarrelRollClient.left);
        facing = rotateAxisAngle(facing, up, 0.15 * yaw * TORAD);
        DoABarrelRollClient.left = rotateAxisAngle(DoABarrelRollClient.left, up, 0.15 * yaw * TORAD);

        // roll
        DoABarrelRollClient.left = rotateAxisAngle(DoABarrelRollClient.left, facing, 0.15 * roll * TORAD);


        double deltaY = -Math.asin(facing.getY()) * DoABarrelRollClient.TODEG - player.getPitch();
        double deltaX = -Math.atan2(facing.getX(), facing.getZ()) * DoABarrelRollClient.TODEG - player.getYaw();

        player.changeLookDirection(deltaX / 0.15, deltaY / 0.15);

        // fix hand spasm when wrapping yaw value
        if (player.getYaw() < -90 && player.renderYaw > 90) {
            player.renderYaw -= 360;
            player.lastRenderYaw -= 360;
        } else if (player.getYaw() > 90 && player.renderYaw < -90) {
            player.renderYaw += 360;
            player.lastRenderYaw += 360;
        }
    }

    public static Vec3d getAssumedLeft(float yaw) {
        yaw *= TORAD;
        return new Vec3d(-Math.cos(yaw), 0, -Math.sin(yaw));
    }

    public static Vec3d rotateAxisAngle(Vec3d v, Vec3d axis, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1.0 - c;

        //double l = axis.lengthSquared();
        //if (l == 0) return v;
        //if (l != 1) axis = axis.multiply(1/Math.sqrt(l));

        double x = (c + axis.x*axis.x*t) * v.getX(),
                y = (c + axis.y*axis.y*t) * v.getY(),
                z = (c + axis.z*axis.z*t) * v.getZ(),
                tmp1 = axis.x*axis.y*t,
                tmp2 = axis.z*s;
        y += (tmp1 + tmp2) * v.getX();
        x += (tmp1 - tmp2) * v.getY();
        tmp1 = axis.x*axis.z*t;
        tmp2 = axis.y*s;
        z += (tmp1 - tmp2) * v.getX();
        x += (tmp1 + tmp2) * v.getZ();
        tmp1 = axis.y*axis.z*t;
        tmp2 = axis.x*s;
        z += (tmp1 + tmp2) * v.getY();
        y += (tmp1 - tmp2) * v.getZ();

        return new Vec3d(x, y, z);
    }

    public static double sigmoid(double x) {
        return 1/(1+Math.exp(x));
    }
}
