package RenovatioMod.renovated_difficulty;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.Difficulty;

public class Renovated_difficulty implements ModInitializer {
    @Override
    public void onInitialize() {
        // Accessing any field forces the static initializer (and our Mixin)
        // to run immediately on the main thread during mod startup.
        Difficulty initialLoad = Difficulty.PEACEFUL;
        System.out.println("Renovatio Difficulty: Static Engine Locked (" + initialLoad.name() + ")");
    }
}