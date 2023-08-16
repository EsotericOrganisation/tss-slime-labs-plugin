package net.slqmy.tss_slimelabs;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_slimelabs.command.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TSSSlimeLabsPlugin extends JavaPlugin {

  private final TSSCorePlugin corePlugin = (TSSCorePlugin) Bukkit.getPluginManager().getPlugin("TSS-Core");

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
