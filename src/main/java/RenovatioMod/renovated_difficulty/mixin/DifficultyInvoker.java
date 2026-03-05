package RenovatioMod.renovated_difficulty.mixin;

import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Difficulty.class)
public interface DifficultyInvoker {
    @Invoker("<init>")
    static Difficulty create(String internalName, int internalId, int id, String name) {
        throw new AssertionError();
    }
}