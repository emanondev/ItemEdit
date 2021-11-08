package emanondev.itemedit.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public abstract class SubCmd {

	private final String permission;
	public final String ID;
	private final String PATH;
	private BaseComponent[] fail;
	private String description;
	private String help;
	private String name;
	private final String commandName;
	private final boolean playerOnly;
	private final boolean checkNonNullItem;
	private final AbstractCommand cmd;

	public SubCmd(String id, AbstractCommand cmd, boolean playerOnly, boolean checkNonNullItem) {
		if (id == null)
			throw new NullPointerException();
		if (id.equals("") || id.contains(" "))
			throw new IllegalArgumentException();
		this.ID = id.toLowerCase();
		this.cmd = cmd;
		this.commandName = cmd.getName();
		this.playerOnly = playerOnly;
		this.checkNonNullItem = checkNonNullItem;
		this.PATH = "sub-commands." + this.ID;
		load();
		this.permission = this.getPlugin().getName().toLowerCase()+"." + this.commandName + "." + this.ID;
	}
	
	public AbstractCommand getCommand() {
		return cmd;
	}
	
	public APlugin getPlugin() {
		return cmd.getPlugin();
	}

	public boolean isPlayerOnly() {
		return this.playerOnly;
	}

	public boolean checkNonNullItem() {
		return this.checkNonNullItem;
	}

	@SuppressWarnings("deprecation")
	protected ItemStack getItemInHand(Player p) {
		return p.getInventory().getItemInHand();
	}

	@SuppressWarnings("deprecation")
	private void load() {
		name = this.getConfString("name").toLowerCase();
		if (name.equals("") || name.contains(" "))
			name = ID;

		description = String.join("\n", getConfStringList("description"));
		String params = getConfString("params");
		if (params == null)
			params = "";
		params = ChatColor.translateAlternateColorCodes('&', params);
		this.fail = new ComponentBuilder(
				ChatColor.RED + "/" + commandName + " " + this.name + " " + ChatColor.stripColor(params))
						.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
								"/" + commandName + " " + this.name + " " + ChatColor.stripColor(params)))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder(this.description).create()))
						.create();
		this.help = ChatColor.DARK_GREEN + "/" + commandName + " " + ChatColor.GREEN + this.name + " "
				+ params.replace(ChatColor.RESET.toString(), ChatColor.GREEN.toString());
	}

	public void reload() {
		load();
	}

	@SuppressWarnings("deprecation")
	protected BaseComponent[] craftFailFeedback(String params, String desc) {
		if (params == null)
			params = "";
		ComponentBuilder fail = new ComponentBuilder(ChatColor.RED + "/" + commandName + " " + this.name + " " + params)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
						"/" + commandName + " " + this.name + " " + params));
		if (desc != null && !desc.equals("")) {
			desc = ChatColor.translateAlternateColorCodes('&', desc);
			fail.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(desc).create()));
		}
		return fail.create();
	}

	protected String getConfString(String path) {
		return getPlugin().getConfig(commandName + ".yml").loadString(this.PATH + "." + path, "", true);
	}
	
	protected int getConfInt(String path) {
		return getPlugin().getConfig(commandName + ".yml").loadInteger(this.PATH + "." + path, 0);
	}
	protected long getConfLong(String path) {
		return getPlugin().getConfig(commandName + ".yml").loadLong(this.PATH + "." + path, 0L);
	}

	protected boolean getConfBoolean(String path) {
		return getPlugin().getConfig(commandName + ".yml").loadBoolean(this.PATH + "." + path, true);
	}

	protected List<String> getConfStringList(String path) {
		return getPlugin().getConfig(commandName + ".yml").loadStringList(this.PATH + "." + path, new ArrayList<String>(), true);
	}

	public String getName() {
		return this.name;
	}

	public String getPermission() {
		return this.permission;
	}

	@SuppressWarnings("deprecation")
	public ComponentBuilder getHelp(ComponentBuilder base) {
		base.append(help).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(help)))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.description).create()));
		return base;
	}

	public void onFail(CommandSender target) {
		Util.sendMessage(target, this.fail);
	}

	abstract public void onCmd(CommandSender sender, String[] args);

	abstract public List<String> complete(CommandSender sender, String[] args);

}
