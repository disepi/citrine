package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.MathUtil;
import com.disepi.citrine.utils.NPC;

public class BoomboxNPC extends NPC {
    public int aliveTicks = 0;
    public int explodeTicks = 0;

    public float baseDamage = 3;
    public float kbScalar = 1;
    public boolean isDone = false;
    public GameData refData = null;
    public Player placer = null;
    public boolean doDamage = true;
    public boolean placerOnly = false;
    public boolean doKnockback = true;

    public void handleHit(Player p) {

    }

    public void handleExplode() {
        level.addSound(this.getPosition(), Sound.RANDOM_EXPLODE);
        level.addParticleEffect(this.getPosition(), ParticleEffect.LARGE_EXPLOSION_LEVEL);
        for(Player p : refData.players) {
            if(placerOnly && p != this.placer || Citrine.getData(p).isSpectating) continue;

            float dist = (float)MathUtil.distance(this.x, this.y, this.z, p.x, p.y, p.z);
            float damage = this.baseDamage - (dist/4.f);
            if(damage > 0.f) {
                if(doDamage) {
                    if (Citrine.getData(p).canTakeDamage)
                        p.attack(damage);
                    else {
                        EntityEventPacket pk = new EntityEventPacket();
                        pk.eid = p.getId();
                        pk.event = EntityEventPacket.HURT_ANIMATION;
                        p.dataPacket(pk);
                    }
                }

                handleHit(p);

                if(!doKnockback) continue;

                // sucks ass
                Vector2 rotations = MathUtil.getRotationsToPosition(this.getPosition().asVector3f(), p.getPosition().asVector3f());
                float cYaw = (float) ((rotations.x + 90.0f) * MathUtil.DEG);
                float cPitch = (float) (rotations.y * -MathUtil.DEG);
                Vector3 vel = new Vector3(Math.cos(cYaw), Math.sin(cPitch), Math.sin(cYaw));
                vel.y += 0.42f;
                vel = vel.multiply(0.5f);
                vel = vel.multiply(damage / this.baseDamage);
                vel = vel.multiply(this.kbScalar);
                p.setMotion(vel);
            }
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if(!isDone) {
            Position pos = this.getPosition();
            if (aliveTicks % 5 == 0) level.addSound(pos, Sound.RANDOM_CLICK);
            if (aliveTicks == 0) level.addSound(pos, Sound.RANDOM_FUSE);

            if (aliveTicks > explodeTicks) {
                handleExplode();
                isDone = true;
                this.despawnFromAll();
                this.kill();
            }
            aliveTicks++;
        }

        float left = (this.explodeTicks - this.aliveTicks)/20.f;
        if(left < 0.f) left = 0.f;
        this.setNameTag(String.valueOf(left).substring(0, 3));
        doGravity();
        return super.entityBaseTick(tickDiff);
    }

    public static CustomEntityDefinition def = NPC.getDefinition("hivecommon:standard_boombox_ent");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public BoomboxNPC(Level lvl, float x, float y, float z, int explodeTicks, float baseDamage, float kbScalar, GameData data, Player placer) {
        super(lvl, x, y, z);
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.explodeTicks = explodeTicks;
        this.baseDamage = baseDamage;
        this.kbScalar = kbScalar;
        this.refData = data;
        this.placer = placer;
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    @Override
    public float getWidth() {
        return 0.f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }
}
