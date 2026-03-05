package RenovatioMod.renovated_difficulty.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntFunction;
import java.util.function.Supplier;

@Mixin(Difficulty.class)
public class DifficultyMixin {

    // Using Yarn names + Aliases ensures the Mixin finds the field
    // regardless of whether the RefMap or Remapper is working.
    @Shadow(aliases = {"field_5804", "VALUES"}) @Final @Mutable
    private static Difficulty[] $VALUES;

    @Shadow(aliases = {"field_41668"}) @Final @Mutable
    private static StringIdentifiable.EnumCodec<Difficulty> CODEC;

    @Shadow(aliases = {"field_5800"}) @Final @Mutable
    private static IntFunction<Difficulty> BY_ID;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomDifficulties(CallbackInfo ci) {
        System.out.println("[Renovatio] Executing Difficulty Injection...");

        try {
            Difficulty[] v = $VALUES;

            // Avoid duplicate injection if this class has already been patched.
            if (v.length > 4) {
                System.out.println("[Renovatio] Difficulty values already extended, skipping.");
                return;
            }

            final int tranquilOrdinal = v.length;
            final int brutalOrdinal = v.length + 1;
            final int nightmareOrdinal = v.length + 2;

            Difficulty tranquil = DifficultyInvoker.create("TRANQUIL", tranquilOrdinal, 4, "tranquil");
            Difficulty brutal = DifficultyInvoker.create("BRUTAL", brutalOrdinal, 5, "brutal");
            Difficulty nightmare = DifficultyInvoker.create("NIGHTMARE", nightmareOrdinal, 6, "nightmare");

            final Difficulty[] newValues = new Difficulty[] {
                    v[0], tranquil, v[1], v[2], v[3], brutal, nightmare
            };

            $VALUES = newValues;

            // Using an Anonymous Inner Class instead of a Lambda (->) to prevent Bootstrap Errors
            BY_ID = new IntFunction<Difficulty>() {
                @Override
                public Difficulty apply(int id) {
                    for (Difficulty d : newValues) {
                        if (d.getId() == id) return d;
                    }
                    return id < 0 ? Difficulty.PEACEFUL : Difficulty.HARD;
                }
            };

            // Rebuilding the Codec using a Supplier class to avoid Method References (::)
            CODEC = StringIdentifiable.createCodec(new Supplier<Difficulty[]>() {
                @Override
                public Difficulty[] get() {
                    return newValues;
                }
            });

            System.out.println("[Renovatio] Difficulty Engine Surgery Successful.");
        } catch (Throwable t) {
            System.err.println("[Renovatio] CRITICAL INJECTION FAILURE:");
            t.printStackTrace();
            throw new RuntimeException("Failed to inject custom difficulties", t);
        }
    }
}
