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

    // These shadows now point to the "Named" Yarn mappings
    @Shadow @Final @Mutable
    private static Difficulty[] field_5804; // $VALUES

    @Shadow @Final @Mutable
    public static Codec<Difficulty> CODEC;

    @Shadow @Final @Mutable
    private static IntFunction<Difficulty> BY_ID;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomDifficulties(CallbackInfo ci) {
        System.out.println("[Renovatio] Injecting via Access Widener...");

        try {
            Difficulty tranquil = DifficultyInvoker.create("TRANQUIL", -1, 4, "tranquil");
            Difficulty brutal = DifficultyInvoker.create("BRUTAL", -1, 5, "brutal");
            Difficulty nightmare = DifficultyInvoker.create("NIGHTMARE", -1, 6, "nightmare");

            Difficulty[] vanilla = field_5804;
            final Difficulty[] newValues = new Difficulty[] {
                    vanilla[0], tranquil, vanilla[1], vanilla[2], vanilla[3], brutal, nightmare
            };

            // Directly overwrite the fields unlocked by the Access Widener
            field_5804 = newValues;

            // Use a classic Anonymous Class instead of a Lambda to prevent BootstrapMethodError
            BY_ID = new IntFunction<Difficulty>() {
                @Override
                public Difficulty apply(int id) {
                    for (Difficulty d : newValues) {
                        if (d.getId() == id) return d;
                    }
                    return Difficulty.PEACEFUL;
                }
            };

            // Refresh the Codec
            CODEC = StringIdentifiable.createCodec(new Supplier<Difficulty[]>() {
                @Override
                public Difficulty[] get() {
                    return newValues;
                }
            });

            System.out.println("[Renovatio] Surgery Complete!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}