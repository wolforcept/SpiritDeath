package wolforce.spiritdeath;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Config {
	public static ForgeConfigSpec CONFIG_SPEC;
	public static Config CONFIG;

	public final IntValue graveyardDistance;

	public static void init() {
		Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Config(ForgeConfigSpec.Builder builder) {
		graveyardDistance = builder.comment("The average distance between graveyard positions where players respawn. " + //
				"Effectively, its the maximum distance to the death point that the player can respawn in.") //
				.defineInRange("graveyardDistance", 500, 1, Integer.MAX_VALUE);
	}

	public static int getGraveyardDistance() {
		return Math.max(1, CONFIG.graveyardDistance.get());
	}

}