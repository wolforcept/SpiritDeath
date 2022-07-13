package wolforce.spiritdeath;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SpiritDeath.MODID)
public class SpiritDeath {

	public static final String MODID = "spiritdeath";

	public SpiritDeath() {
		Config.init();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.CONFIG_SPEC, MODID + ".toml");
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {

		private static final String LAST_DEATH_TAG_STRING = "spiritdeath";

		@SubscribeEvent
		public static void onDeath(LivingDeathEvent event) {
			if (event.getEntityLiving() instanceof Player player) {
				player.addTag(LAST_DEATH_TAG_STRING //
						+ ":" + player.position().x //
						+ ":" + player.position().y //
						+ ":" + player.position().z //
				);
			}
		}

		@SubscribeEvent
		public static void onRespawn(PlayerRespawnEvent event) {

			if (event.getPlayer() instanceof ServerPlayer player) {
				BlockPos respawnPos = getNearestGraveyard(player);
				if (respawnPos != null)
					event.getPlayer().teleportTo(respawnPos.getX() + .5, respawnPos.getY(), respawnPos.getZ() + .5);
			}

		}

		private static BlockPos getNearestGraveyard(ServerPlayer player) {
			Random random = new Random(player.server.getWorldData().worldGenSettings().seed());
			Vec3 deathPos = getLastDeath(player);
			if (deathPos == null)
				return null;
			int max = Config.getGraveyardDistance();
			int x = max * (int) Math.round(deathPos.x / (double) max) - max / 2 + random.nextInt(max);
			int z = max * (int) Math.round(deathPos.z / (double) max) - max / 2 + random.nextInt(max);
			player.level.getBlockState(new BlockPos(x, 64, z)); // just to chunkload
			int y = player.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
			return new BlockPos(x, y, z);
		}

		private static Vec3 getLastDeath(Player player) {
			for (String tag : player.getTags()) {
				System.out.println(tag);
				if (tag.startsWith(LAST_DEATH_TAG_STRING)) {
					String[] parts = tag.split(":");
					player.removeTag(tag);
					return new Vec3(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
				}
			}
			return null;
		}
	}
}
