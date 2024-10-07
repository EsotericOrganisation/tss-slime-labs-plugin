package org.esoteric_organisation.tss_slime_labs_plugin;

import org.esoteric_organisation.tss_core_plugin.TSSCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.esoteric_organisation.tss_slime_labs_plugin.command.*;

public final class TSSSlimeLabsPlugin extends JavaPlugin {

  private final TSSCorePlugin corePlugin = (TSSCorePlugin) Bukkit.getPluginManager().getPlugin("tss-core-plugin");

  public TSSCorePlugin getCore() {
	return corePlugin;
  }

  @Override
  public void onEnable() {
	new StyledTextTestCommand(this);
	new LaunchCommand(this);
	new LocaleCommand();
	new ClearPDCCommand();
	new GetPDCValuesCommand(this);
	new LogContainerContents();
  }
}
