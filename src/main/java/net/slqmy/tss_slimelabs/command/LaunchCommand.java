package net.slqmy.tss_slimelabs.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.slqmy.tss_core.type.Colour;
import net.slqmy.tss_core.util.MessageUtil;
import net.slqmy.tss_core.util.NMSUtil;
import net.slqmy.tss_slimelabs.SlimeLabsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LaunchCommand {

	private final ArrayList<BukkitTask> spawnParticlesTasks = new ArrayList<>();
	private BukkitTask analysisTask;

	public LaunchCommand(SlimeLabsPlugin plugin) {
		ScoreboardManager boardManager = Bukkit.getScoreboardManager();

		new CommandAPICommand("launch")
						.withOptionalArguments(new EntitySelectorArgument.OneEntity("entity"), new DoubleArgument("velocity"), new BooleanArgument("finish on landing"))
						.executesPlayer((Player player, CommandArguments args) -> {
							if (analysisTask != null) {
								player.sendMessage(Component.text("There is already a launch analysis task active!", Colour.RED.asTextColour()));
								return;
							}

							TextComponent launchInfoTitle = Component.text("----- ", Colour.SKY_BLUE.asTextColour())
											.append(Component.text("Launch", Colour.SLIME.asTextColour(), TextDecoration.UNDERLINED))
											.append(Component.text(" -----", Colour.SKY_BLUE.asTextColour()));

							player.sendMessage(launchInfoTitle);

							Boolean finishOnLanding = (Boolean) args.get("finish on landing");

							if (finishOnLanding == null || !finishOnLanding) {
								TextComponent finishLaunchAnalysis = Component.text("[Click here to finish launch and view statistics]", Colour.SLIME.asTextColour());
								finishLaunchAnalysis = finishLaunchAnalysis.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/finish-launch"));
								finishLaunchAnalysis = finishLaunchAnalysis.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to stop launch analysis!", Colour.YELLOW.asTextColour())));

								player.sendMessage(finishLaunchAnalysis);
							}

							Scoreboard board = boardManager.getNewScoreboard();
							Objective objective = board.registerNewObjective("launch_info", Criteria.DUMMY, launchInfoTitle);
							objective.setDisplaySlot(DisplaySlot.SIDEBAR);

							TextComponent pipe = MessageUtil.getPipeTextComponent();
							TextComponent colon = MessageUtil.getColonTextComponent();

							Team time = board.registerNewTeam("Time");
							time.addEntry(ChatFormatting.YELLOW.toString());
							time.prefix(Component.text("Time", Colour.LIGHT_GREY.asTextColour()).append(colon));

							Team vx = board.registerNewTeam("vx");
							vx.addEntry(ChatFormatting.RED.toString() + ChatFormatting.RED);
							vx.prefix(pipe.append(Component.text("vx", Colour.RED)).append(colon));

							Team vz = board.registerNewTeam("vz");
							vz.addEntry(ChatFormatting.AQUA.toString() + ChatFormatting.AQUA);
							vz.prefix(pipe.append(Component.text("vz", Colour.SKY_BLUE)).append(colon));

							Team vy = board.registerNewTeam("vy");
							vy.addEntry(ChatFormatting.GREEN.toString() + ChatFormatting.GREEN);
							vy.prefix(pipe.append(Component.text("vy", Colour.SLIME)).append(colon));

							objective.getScore(ChatFormatting.YELLOW.toString()).setScore(6);
							objective.getScore("").setScore(5);
							objective.getScore(ChatFormatting.RED.toString() + ChatFormatting.RED).setScore(4);
							objective.getScore(ChatFormatting.AQUA.toString() + ChatFormatting.AQUA).setScore(3);
							objective.getScore(" ").setScore(2);
							objective.getScore(ChatFormatting.GREEN.toString() + ChatFormatting.GREEN).setScore(1);

							Entity entity = (Entity) args.get("entity");
							if (entity == null) {
								entity = player;
							}

							Double velocity = (Double) args.get("velocity");
							if (velocity == null) {
								velocity = 1D;
							}

							entity.setVelocity(entity.getLocation().getDirection().multiply(velocity));

							player.setScoreboard(board);

							ServerPlayer serverPlayer = NMSUtil.getServerPlayer(player);
							assert serverPlayer != null;

							final int[] ticks = {0};

							Entity finalEntity = entity;
							net.minecraft.world.entity.Entity nmsEntity = (net.minecraft.world.entity.Entity) NMSUtil.invokeHandle(entity);
							assert nmsEntity != null;

							analysisTask = new BukkitRunnable() {

								private Location previousLocation = finalEntity.getLocation();

								@Override
								public void run() {
									ticks[0] += 1;

									Vector velocity = finalEntity.getVelocity();
									Location currentLocation = finalEntity.getLocation();

									TextComponent comma = Component.text(", ", Colour.GREY.asTextColour());

									vx.suffix(
													Component.text(MessageUtil.formatNumber(velocity.getX()), Colour.LIGHT_GREY)
																	.append(comma)
																	.append(Component.text(MessageUtil.formatNumber(currentLocation.getX() - previousLocation.getX())))
									);

									vz.suffix(
													Component.text(MessageUtil.formatNumber(velocity.getZ()), Colour.LIGHT_GREY)
																	.append(comma)
																	.append(Component.text(MessageUtil.formatNumber(currentLocation.getZ() - previousLocation.getZ())))
									);

									vy.suffix(
													Component.text(MessageUtil.formatNumber(velocity.getY()), Colour.LIGHT_GREY)
																	.append(comma)
																	.append(Component.text(MessageUtil.formatNumber(currentLocation.getY() - previousLocation.getY())))
									);

									previousLocation = currentLocation;

									time.suffix(
													Component.text(
																					MessageUtil.formatNumber(ticks[0] / 20D) + "s", Colour.YELLOW.asTextColour()
																	)
																	.append(Component.text(" (", Colour.LIGHT_GREY.asTextColour()))
																	.append(Component.text(ticks[0] + "t", Colour.SKY_BLUE.asTextColour()))
																	.append(Component.text(")", Colour.LIGHT_GREY.asTextColour()))
									);

									spawnParticlesTasks.add(new BukkitRunnable() {

										private final Location particleLocation = finalEntity.getLocation();

										@Override
										public void run() {
											particleLocation.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, particleLocation, 1, new Particle.DustTransition(
																			Color.RED,
																			Color.RED,
																			0.85F
															)
											);

											if (finishOnLanding != null && finishOnLanding && serverPlayer.onGround()) {
												cancel();
											}
										}
									}.runTaskTimer(plugin, 0, 1));

									if (finishOnLanding != null && finishOnLanding && serverPlayer.onGround()) {
										player.setScoreboard(boardManager.getNewScoreboard());
										analysisTask = null;
										cancel();
									}
								}
							}.runTaskTimer(plugin, 1, 1);
						})
						.register();

		new CommandAPICommand("finish-launch")
						.executesPlayer((Player player, CommandArguments args) -> {
							if (analysisTask == null) {
								player.sendMessage(Component.text("There is no analysis task active!", Colour.RED.asTextColour()));
								return;
							}

							analysisTask.cancel();
							analysisTask = null;
							player.setScoreboard(boardManager.getNewScoreboard());

							for (BukkitTask spawnParticlesTask : spawnParticlesTasks) {
								spawnParticlesTask.cancel();
							}
						})
						.register();
	}
}
