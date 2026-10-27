package aetherales.antiphantom.mixin;

import aetherales.antiphantom.AntiPhantom;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;"), method = "tick")
	private List<ServerPlayer> preventPhantomSpawning(ServerLevel instance, Operation<List<ServerPlayer>> original) {
		final int spawnDistance = instance.getGameRules().get(AntiPhantom.PHANTOM_SPAWN_DISTANCE);

        //		AntiPhantom.LOGGER.info("List contains {} players", list.size());
//		AntiPhantom.LOGGER.info("List: " + Arrays.toString(list.toArray()));

		return original.call(instance).stream().filter(
			player -> {
				if (Objects.isNull(player.getRespawnConfig())) {
//					AntiPhantom.LOGGER.info("Spawnpoint no existy!");
					return true;
				}

				LevelData.RespawnData data = player.getRespawnConfig().respawnData();

				if (!data.dimension().identifier().equals(instance.dimension().identifier())) {
//					AntiPhantom.LOGGER.info("Spawnpoint in different dimension!");
					return true;
				}

                //					AntiPhantom.LOGGER.info("Far enough away to spawn phantoms!");
                return !data.pos().closerThan(player.blockPosition(), spawnDistance);

//				AntiPhantom.LOGGER.info("Too close to spawn phantoms!");
            }
		).toList();
	}
}