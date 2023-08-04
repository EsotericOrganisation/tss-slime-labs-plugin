package net.slqmy.tss_slimelabs.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.slqmy.tss_core.type.Colour;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_core.util.MessageUtil;
import net.slqmy.tss_core.util.NMSUtil;
import net.slqmy.tss_core.util.ReflectUtil;
import net.slqmy.tss_slimelabs.SlimeLabsPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LaunchCommand {

	public LaunchCommand(SlimeLabsPlugin plugin) {
		new CommandAPICommand("launch")
						.withArguments(new FloatArgument("force"))
						.executesPlayer((Player player, CommandArguments args) -> {
							player.sendMessage(
											Component.text("--- ", Colour.SKY_BLUE.asTextColour())
															.append(Component.text("Launch", Colour.SLIME.asTextColour()))
															.append(Component.text(" ---", Colour.SKY_BLUE.asTextColour()))
							);

							ServerPlayer serverPlayer = NMSUtil.getServerPlayer(player);
							assert serverPlayer != null;

							boolean effectiveAI = serverPlayer.isEffectiveAi();
							boolean controlledByLocalInstance = serverPlayer.isControlledByLocalInstance();
							boolean shouldDiscardFriction = serverPlayer.shouldDiscardFriction();

							DebugUtil.log(ReflectUtil.getFieldValue(serverPlayer, "bU"));

							boolean discardFriction = (boolean) ReflectUtil.getFieldValue(serverPlayer, "bU", Boolean.TYPE);
							TriState frictionState = serverPlayer.frictionState;

							float force = (float) args.get(0);

							Vector playerDirection = player.getLocation().getDirection().multiply(force);
							Vec3 initialVelocity = new Vec3(
											playerDirection.getX(),
											playerDirection.getY(),
											playerDirection.getZ()
							);

							Level level = serverPlayer.level();
							boolean levelIsClientSide = level.isClientSide();

							float xxa = serverPlayer.xxa;
							float yya = serverPlayer.yya;
							float zza = serverPlayer.zza;

							Method getBlockPosBelowThatAffectsMyMovement = ReflectUtil.getAccessibleMethod(serverPlayer, "aE");
							assert getBlockPosBelowThatAffectsMyMovement != null;

							BlockPos pos;

							try {
								pos = (BlockPos) getBlockPosBelowThatAffectsMyMovement.invoke(serverPlayer);
							} catch (IllegalAccessException | InvocationTargetException exception) {
								DebugUtil.handleException("Couldn't invoke method 'getBlockPosBelowThatAffectsMyMovement' of serverPlayer object " + serverPlayer + "!", exception);
								return;
							}

							float friction = level.getBlockState(pos).getBlock().getFriction();

							Vec3 handledFriction = serverPlayer.handleRelativeFrictionAndCalculateMovement(initialVelocity, friction);

							Object[] values = new Object[]{effectiveAI, controlledByLocalInstance, pos, friction, shouldDiscardFriction, discardFriction, frictionState, handledFriction, levelIsClientSide, xxa, yya, zza};
							String[] valueNames = new String[]{"Effective AI", "Controlled by local instance", "Block position below that affects my movement", "Friction", "Should discard friction", "Discard friction", "Friction state", "Handled friction", "Level is client side", "xxa", "yya", "zza"};

							for (int i = 0; i < values.length; i++) {
								player.sendMessage(
												Component.text("- " + valueNames[i] + ": ", Colour.ORANGE.asTextColour())
																.append(MessageUtil.format(values[i]))
								);
							}

							serverPlayer.setDeltaMovement(initialVelocity);
							serverPlayer.setOnGroundWithKnownMovement(false, initialVelocity);

							new BukkitRunnable() {

								@Override
								public void run() {
									DebugUtil.log("Player is on ground? ", serverPlayer.onGround());

									if (serverPlayer.onGround()) {
										cancel();
										return;
									}

									new BukkitRunnable() {

										private final Location particleLocation = player.getLocation();

										@Override
										public void run() {
											if (serverPlayer.onGround()) {
												cancel();
												return;
											}

											particleLocation.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, particleLocation, 1, new Particle.DustTransition(
																			Color.RED,
																			Color.RED,
																			0.85F
															)
											);
										}
									}.runTaskTimer(plugin, 0, 1);
								}
							}.runTaskTimer(plugin, 1, 1);
						})
						.register();
	}
}
