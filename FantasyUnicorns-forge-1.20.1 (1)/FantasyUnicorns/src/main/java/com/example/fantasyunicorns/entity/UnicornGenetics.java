package com.example.fantasyunicorns.entity;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.fantasyunicorns.entity.UnicornVariant.*;

public final class UnicornGenetics {
    private record Combo(UnicornVariant result, float chance) {}

    private static final Map<Set<UnicornVariant>, Combo> COMBOS = Map.of(
        Set.of(WHITE, BLACK), new Combo(SILVER, 0.60f),
        Set.of(PINK, BLACK),  new Combo(CRIMSON, 0.50f),
        Set.of(BLUE, GOLD),   new Combo(EMERALD, 0.50f),
        Set.of(PINK, BLUE),   new Combo(PURPLE, 0.70f),
        Set.of(SILVER, GOLD), new Combo(CELESTIA, 0.20f)
    );

    private static final List<UnicornVariant> NON_LEGENDARY = new ArrayList<>();
    static { for (UnicornVariant v : values()) if (v.tier != Tier.LEGENDARY) NON_LEGENDARY.add(v); }

    public static UnicornVariant mix(UnicornVariant a, UnicornVariant b, RandomSource r) {
        if (a != b) {
            Combo c = COMBOS.get(Set.of(a, b));
            if (c != null && r.nextFloat() < c.chance()) return c.result();
            if (a.tier == Tier.RARE && b.tier == Tier.RARE && r.nextFloat() < 0.05f) return PRISMA;
        }
        // กลายพันธุ์สุ่ม 3% (Legendary หลุดได้ 0.3% แยกต่างหาก)
        if (r.nextFloat() < 0.03f) return NON_LEGENDARY.get(r.nextInt(NON_LEGENDARY.size()));
        if (r.nextFloat() < 0.003f) return r.nextBoolean() ? PRISMA : CELESTIA;
        return r.nextBoolean() ? a : b;
    }

    private UnicornGenetics() {}
}
