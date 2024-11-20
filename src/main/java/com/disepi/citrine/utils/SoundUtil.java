package com.disepi.citrine.utils;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlaySoundPacket;

public class SoundUtil {
    public static DataPacket getSoundPacket(Sound sound, float volume, float pitch, float x, float y, float z) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = (int) x;
        packet.y = (int) y;
        packet.z = (int) z;
        return packet;
    }

    public static DataPacket getSoundPacket(Sound sound, float volume, float pitch, Position pos) {
        return getSoundPacket(sound, volume, pitch, (float) pos.x, (float) pos.y, (float) pos.z);
    }

    public static DataPacket getSoundPacket(Sound sound, float volume, float pitch, Player p) {
        return getSoundPacket(sound, volume, pitch, p.getPosition());
    }
}
