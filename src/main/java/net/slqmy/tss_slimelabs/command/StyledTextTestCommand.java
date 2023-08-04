package net.slqmy.tss_slimelabs.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slqmy.tss_core.type.Colour;
import net.slqmy.tss_slimelabs.SlimeLabsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StyledTextTestCommand {

	public StyledTextTestCommand(SlimeLabsPlugin plugin) {

		new CommandAPICommand("test-styled-text")
						.executesPlayer((Player player, CommandArguments args) -> {
							String discordInviteURL = "https://www.discord.gg/" + plugin.getCore().getConfig().getString("discord-server-invite-code");

							TextComponent styledText = Component.text(discordInviteURL, Colour.BLURPLE.asTextColour(), TextDecoration.UNDERLINED);

							styledText = styledText.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, discordInviteURL));
							styledText = styledText.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, styledText));
							player.sendMessage(styledText);

							TextComponent finalClickableText = styledText;

							Bukkit.getScheduler().runTaskLater(plugin, () -> {
								player.kick(finalClickableText);
							}, 40);
						})
						.register();
	}
}
