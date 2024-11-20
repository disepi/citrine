package com.disepi.citrine.utils;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

public class MathUtil {
    public static final float PI = 3.1415927f;
    public static final float DEG = PI / 180.0f;
    public static final float DEG_RAD = 180.0f / PI;

    public static Vector2 getRotationsToPosition(Vector3f origin, Vector3f target) {
        Vector3f diff = target.subtract(origin);

        diff.y = (float) (diff.y / (Math.sqrt(diff.x * diff.x + diff.y * diff.y + diff.z * diff.z)));
        return new Vector2((float) -Math.atan2(diff.x, diff.z) * DEG_RAD, (float) (Math.asin(diff.y) * -DEG_RAD));
    }

    public static boolean isInRegion(Vector3f start, Vector3f pos, float radius) {
        float minX = start.x - radius;
        float minZ = start.z - radius;
        float maxX = start.x + radius;
        float maxZ = start.z + radius;
        if(pos.x < minX) return false;
        if(pos.z < minZ) return false;
        if(pos.x > maxX) return false;
        if(pos.z > maxZ) return false;
        return true;
    }

    // Returns the distance between two coordinates
    public static float distance(float x, float y, float z, float x2, float y2, float z2) {
        float dX = x - x2;
        float dY = y - y2;
        float dZ = z - z2;
        return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static double distance(double x, double y, double z, double x2, double y2, double z2) {
        double dX = x - x2;
        double dY = y - y2;
        double dZ = z - z2;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static BlockAir airB = new BlockAir();
    public static double feetValue = 1.62 + 0.0001; // getEyeHeight(void)

    public static Block getBlock(Level level, int x, int y, int z) {
        return level.getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    // Returns if the specified block is a valid block that the player can walk on
    public static boolean isConsideredSolid(Level level, int x, int y, int z) {
        return !(getBlock(level, x, y, z) instanceof BlockAir);
    }

    public static boolean isConsideredSolid(Level level, double x, double y, double z) {
        return isConsideredSolid(level, (int) x, (int) y, (int) z);
    }

    public static boolean isConsideredSolid(Level level, Vector3 pos) {
        return isConsideredSolid(level, (int) pos.x, (int) pos.y, (int) pos.z);
    }

    public static Block getNearestSolidBlock(Vector3 pos, Level level, float radius) {
        Block under = getBlock(level, (int) pos.x, (int) (pos.y - feetValue), (int) pos.z); // Get the block
        if (!(under instanceof BlockAir)) // Check if it isn't air
            return under; // Return the retrieved block

        int lastX = -1, lastZ = -1;

        // Check in radius around player
        for (float x = -radius; x < radius; x += 0.5f) {
            for (float z = -radius; z < radius; z += 0.5f) {
                int newX = (int) (pos.x + x);
                int newZ = (int) (pos.z + z);
                if(newX == lastX && newZ == lastZ) continue;
                Block temp = getBlock(level, newX, (int) (pos.y - feetValue), newZ); // get temp block
                if (!(temp instanceof BlockAir)) return temp; // if it isn't air, return it
            }
        }

        lastX = -1;
        lastZ = -1;

        // Fences/walls
        for (float x = -radius; x < radius; x += 0.5f) {
            for (float z = -radius; z < radius; z += 0.5f) {
                int newX = (int) (pos.x + x);
                int newZ = (int) (pos.z + z);
                if(newX == lastX && newZ == lastZ) continue;
                Block temp = getBlock(level, newX, (int) (pos.y - feetValue - 0.75f), newZ); // get temp block
                if (temp instanceof BlockWall || temp instanceof BlockFence || temp instanceof BlockFenceGate)
                    return temp; // check if it is a wall/fence block
            }
        }

        return airB; // no block found, return air
    }
}
