package RenovatioMod.renovated_difficulty.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.util.StringIdentifiable;
import com.mojang.serialization.Codec;
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

    // We use the Intermediary names in the shadow string to ensure they are found
    // even if the environment's remapper is struggling.
    @Shadow(aliases = {"field_5804"}) @Final @Mutable
    private static Difficulty[] field_5804; // $VALUES

    @Shadow(aliases = {"field_41668"}) @Final @Mutable
    private static Codec<Difficulty> field_41668; // CODEC

    @Shadow(aliases = {"field_5800"}) @Final @Mutable
    private static IntFunction<Difficulty> field_5800; // BY_ID

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomDifficulties(CallbackInfo ci) {
        System.out.println("[Renovatio] Executing Compatibility-First Injection...");

        try {
            Difficulty tranquil = DifficultyInvoker.create("TRANQUIL", -1, 4, "tranquil");
            Difficulty brutal = DifficultyInvoker.create("BRUTAL", -1, 5, "brutal");
            Difficulty nightmare = DifficultyInvoker.create("NIGHTMARE", -1, 6, "nightmare");

            Difficulty[] vanilla = field_5804;
            final Difficulty[] newValues = new Difficulty[] {
                    vanilla[0], tranquil, vanilla[1], vanilla[2], vanilla[3], brutal, nightmare
            };

            // Update the values array
            field_5804 = newValues;

            // Use Anonymous Inner Classes to avoid BootstrapMethodErrors (Lambdas)
            field_5800 = new IntFunction<Difficulty>() {
                @Override
                public Difficulty apply(int id) {
                    for (Difficulty d : newValues) {
                        if (d.getId() == id) return d;
                    }
                    return Difficulty.PEACEFUL;
                }
            };

            // Rebuild the Codec
            field_41668 = StringIdentifiable.createCodec(new Supplier<Difficulty[]>() {
                @Override
                public Difficulty[] get() {
                    return newValues;
                }
            });

            System.out.println("[Renovatio] Difficulty Engine Surgery Successful.");
        } catch (Throwable t) {
            System.err.println("[Renovatio] CRITICAL INJECTION FAILURE:");
            t.printStackTrace();
        }
    }
}