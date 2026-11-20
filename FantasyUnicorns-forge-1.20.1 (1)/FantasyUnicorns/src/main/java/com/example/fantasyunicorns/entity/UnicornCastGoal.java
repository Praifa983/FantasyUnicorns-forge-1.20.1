package com.example.fantasyunicorns.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** ยิงเวทสวนกลับเป้าหมายที่โจมตี ใช้เฉพาะ flag LOOK จึงทำงานคู่กับ PanicGoal (วิ่งหนี) ได้ */
public class UnicornCastGoal extends Goal {
    private final UnicornEntity unicorn;

    public UnicornCastGoal(UnicornEntity unicorn) {
        this.unicorn = unicorn;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override public boolean canUse() {
        LivingEntity t = unicorn.getTarget();
        return t != null && t.isAlive() && !unicorn.isVehicle()
            && unicorn.canCast() && unicorn.distanceToSqr(t) < 18 * 18;
    }

    @Override public boolean canContinueToUse() { return canUse(); }

    @Override public void tick() {
        LivingEntity t = unicorn.getTarget();
        if (t == null) return;
        unicorn.getLookControl().setLookAt(t, 30, 30);
        unicorn.fireBeam(t.getEyePosition().subtract(unicorn.getEyePosition()));
    }
}
