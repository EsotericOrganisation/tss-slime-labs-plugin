package net.slqmy.tss_slimelabs.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class LogContainerContents {

  public LogContainerContents() {
	new CommandAPICommand("log-container-contents")
			.executesPlayer((Player player, CommandArguments args) -> {
			  ItemStack container = player.getInventory().getItemInMainHand();

			  if (container.getType() == Material.CHEST || container.getType() == Material.BARREL) {
				StringBuilder contents = new StringBuilder();

				for (ItemStack itemStack : (container.getType() == Material.CHEST ? ((Chest) ((BlockStateMeta) container.getItemMeta()).getBlockState()).getBlockInventory().getContents() : ((Barrel) ((BlockStateMeta) container.getItemMeta()).getBlockState()).getInventory().getContents())) {
				  if (itemStack == null) {
					continue;
				  }

				  contents.append("Material.").append(itemStack.getType().name()).append(", ");
				}

				LogUtil.log(contents.toString());
			  }

			})
			.register();
  }
}
