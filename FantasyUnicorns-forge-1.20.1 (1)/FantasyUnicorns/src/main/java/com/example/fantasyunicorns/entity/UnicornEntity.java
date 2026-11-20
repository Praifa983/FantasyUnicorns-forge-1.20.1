package com.example.fantasyunicorns.entity;

import com.example.fantasyunicorns.registry.ModEntities;
import com.example.fantasyunicorns.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static com.example.fantasyunicorns.entity.UnicornVariant.*;

public class UnicornEntity extends AbstractHorse implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT =
        SynchedEntityData.defineId(UnicornEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int castCooldown;

    public UnicornEntity(EntityType<? extends UnicornEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseHorseAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void randomizeAttributes(RandomSource r) {
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30 + r.nextDouble() * 0.10);
        getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.95 + r.nextDouble() * 0.25);
    }

    // ---------------- Data / NBT ----------------
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, 0);
    }

    public UnicornVariant getVariant() { return UnicornVariant.byId(entityData.get(VARIANT)); }
    public void setVariant(UnicornVariant v) { entityData.set(VARIANT, v.ordinal()); }

    @Override
    public void addAdditionalSaveData(CompoundTag t) {
        super.addAdditionalSaveData(t);
        t.putInt("Variant", entityData.get(VARIANT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag t) {
        super.readAdditionalSaveData(t);
        entityData.set(VARIANT, t.getInt("Variant"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor lvl, DifficultyInstance diff, MobSpawnType type,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        UnicornVariant[] pool = random.nextFloat() < 0.04f
            ? new UnicornVariant[]{SILVER, EMERALD, CRIMSON}
            : new UnicornVariant[]{WHITE, BLACK, PINK, BLUE, GOLD, PURPLE};
        setVariant(pool[random.nextInt(pool.length)]);
        return super.finalizeSpawn(lvl, diff, type, data, tag);
    }

    // ---------------- AI: เป็นกลาง สวนกลับด้วยเวท + วิ่งหนี ----------------
    @Override
    protected void registerGoals() {
        super.registerGoals(); // PanicGoal, BreedGoal, Wander ฯลฯ ของม้า
        goalSelector.addGoal(1, new UnicornCastGoal(this)); // ใช้แค่ flag LOOK จึงยิงได้ระหว่างวิ่งหนี
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers()); // เรียกฝูงมาช่วย
    }

    // ---------------- เทมด้วยแอปเปิ้ลวิเศษ ----------------
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isTamed()) {
            if (stack.is(ModItems.MAGIC_APPLE.get()) && !isBaby()) {
                if (!level().isClientSide) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    if (random.nextInt(3) == 0) { // 1/3 ต่อลูก
                        setTamed(true);
                        setOwnerUUID(player.getUUID());
                        if (player instanceof ServerPlayer sp) CriteriaTriggers.TAME_ANIMAL.trigger(sp, this);
                        level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            return InteractionResult.PASS; // ยังไม่เชื่อง: ขี่/เทมแบบม้าไม่ได้
        }
        return super.mobInteract(player, hand); // Saddle / ขี่ / Golden Carrot ผสมพันธุ์ ตามระบบม้า
    }

    // ---------------- ผสมพันธุ์ ----------------
    @Override
    public boolean canMate(Animal other) {
        return other != this && other instanceof UnicornEntity u && canParent() && u.canParent();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel lvl, AgeableMob mate) {
        UnicornEntity baby = ModEntities.UNICORN.get().create(lvl);
        if (baby != null && mate instanceof UnicornEntity o) {
            baby.setVariant(UnicornGenetics.mix(getVariant(), o.getVariant(), random));
            setOffspringAttributes(mate, baby);
        }
        return baby;
    }

    // ---------------- ตกจากที่สูง เหลือ 20% ----------------
    @Override
    public boolean causeFallDamage(float dist, float mult, DamageSource src) {
        return super.causeFallDamage(dist * 0.2f, mult, src);
    }

    // ---------------- Tick: พาสซีฟ + อนุภาค ----------------
    @Override
    public void aiStep() {
        super.aiStep();
        if (castCooldown > 0) castCooldown--;

        if (level().isClientSide) {
            if (random.nextInt(3) == 0) {
                level().addParticle(getVariant().particle, getRandomX(0.8), getY() + 0.4 + random.nextDouble() * 1.4,
                    getRandomZ(0.8), 0, 0.02, 0);
            }
            return;
        }
        UnicornVariant v = getVariant();
        if ((v == BLUE || v == PRISMA) && tickCount % 2 == 0)
            FrostWalkerEnchantment.onEntityMoved(this, level(), blockPosition(), 2);

        LivingEntity rider = getControllingPassenger();
        if (rider != null) {
            if (tickCount % 20 == 0) applyPassive(rider, v);
            if (v == BLACK && tickCount % 100 == 0) darkPulse();
        }
    }

    private void giveEffect(LivingEntity rider, MobEffect e) {
        int dur = e == MobEffects.NIGHT_VISION ? 260 : 60; // night vision < 200 ติ๊กจะกะพริบ
        rider.addEffect(new MobEffectInstance(e, dur, 0, true, false));
    }

    private void applyPassive(LivingEntity rider, UnicornVariant v) {
        if (v == PRISMA) { // สุ่มเปลี่ยนทุก 10 วินาที
            MobEffect[] all = {MobEffects.REGENERATION, MobEffects.MOVEMENT_SPEED, MobEffects.JUMP,
                MobEffects.NIGHT_VISION, MobEffects.DAMAGE_RESISTANCE, MobEffects.LUCK};
            giveEffect(rider, all[(tickCount / 200 + getId()) % all.length]);
            return;
        }
        for (MobEffect e : v.riderEffects) giveEffect(rider, e);
    }

    private void darkPulse() { // คลื่นพลังมืด ผลักศัตรูรอบตัว
        for (LivingEntity t : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5),
            e -> e != this && e instanceof Enemy)) {
            Vec3 push = t.position().subtract(position()).normalize().scale(0.8);
            t.push(push.x, 0.3, push.z);
            t.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
        }
        ((ServerLevel) level()).sendParticles(ParticleTypes.SQUID_INK, getX(), getY() + 1, getZ(), 40, 2, 0.5, 2, 0.05);
    }

    // ---------------- ลำแสงเวทมนตร์ (hitscan) ----------------
    public boolean canCast() { return castCooldown <= 0; }

    public void fireBeam(Vec3 dir) {
        if (!(level() instanceof ServerLevel sl) || castCooldown > 0) return;
        castCooldown = 25;
        triggerAnim("cast", "cast");

        Vec3 start = getEyePosition().add(getLookAngle().scale(0.8));
        Vec3 end = start.add(dir.normalize().scale(24));
        end = sl.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(sl, this, start, end,
            new AABB(start, end).inflate(1.0),
            e -> e instanceof LivingEntity && e.isAlive() && e != this && !hasPassenger(e) && !(e instanceof UnicornEntity));
        if (hit != null) {
            end = hit.getLocation();
            LivingEntity t = (LivingEntity) hit.getEntity();
            LivingEntity rider = getControllingPassenger();
            Entity owner = rider != null ? rider : this;
            UnicornVariant v = getVariant();
            float dmg = v == CELESTIA ? 9f : 6f;
            if (v == WHITE && t.getMobType() == MobType.UNDEAD) dmg += 4f;
            t.hurt(damageSources().indirectMagic(this, owner), dmg);
            applyBeamEffect(t, v);
        }
        double len = start.distanceTo(end);
        Vec3 step = end.subtract(start).normalize().scale(0.5);
        Vec3 p = start;
        for (double d = 0; d < len; d += 0.5, p = p.add(step)) {
            sl.sendParticles(getVariant().particle, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        sl.playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 1f, 1.4f);
    }

    private void applyBeamEffect(LivingEntity t, UnicornVariant v) {
        if (v.beamEffect != null) t.addEffect(new MobEffectInstance(v.beamEffect, 100));
        if (v == GOLD) t.setSecondsOnFire(5);
        if (v == CRIMSON) t.setSecondsOnFire(8);
        if (v == BLACK) t.knockback(0.8, getX() - t.getX(), getZ() - t.getZ());
    }

    // ---------------- เสียง ----------------
    @Nullable @Override protected SoundEvent getAmbientSound() { return SoundEvents.HORSE_AMBIENT; }
    @Nullable @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.HORSE_HURT; }
    @Nullable @Override protected SoundEvent getDeathSound() { return SoundEvents.HORSE_DEATH; }

    // ---------------- GeckoLib ----------------
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.unicorn.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.unicorn.walk");
    private static final RawAnimation RUN  = RawAnimation.begin().thenLoop("animation.unicorn.run");
    private static final RawAnimation CAST = RawAnimation.begin().then("animation.unicorn.cast", Animation.LoopType.PLAY_ONCE);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move", 5, state -> {
            double speed = getDeltaMovement().horizontalDistanceSqr();
            if (speed > 0.03) return state.setAndContinue(RUN);
            if (speed > 0.0004) return state.setAndContinue(WALK);
            return state.setAndContinue(IDLE);
        }));
        controllers.add(new AnimationController<>(this, "cast", state -> PlayState.STOP)
            .triggerableAnim("cast", CAST));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
