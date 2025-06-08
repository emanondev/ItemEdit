package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Rename extends SubCmd {

    private int lengthLimit;
    private final Map<UUID, String> copies = new HashMap<>();

    public Rename(@NotNull final ItemEditCommand cmd) {
        super("rename", cmd, true, true);
        lengthLimit = getPlugin().getConfig().getInt("blocked.rename-length-limit", 120);
    }

    @Override
    public void reload() {
        super.reload();
        lengthLimit = getPlugin().getConfig().getInt("blocked.rename-length-limit", 120);
    }

    @Override
    public void onCommand(@NotNull final CommandSender sender,
                          @NotNull final String alias,
                          final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!Util.isAllowedRenameItem(sender, item.getType()))
            return;

        ItemMeta itemMeta = ItemUtils.getMeta(item);
        if (args.length == 1) {
            itemMeta.setDisplayName(" ");
            item.setItemMeta(itemMeta);
            updateView(p);
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("-clear")) {
            itemMeta.setDisplayName(null);
            item.setItemMeta(itemMeta);
            //TODO feedback
            updateView(p);
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("-copy")) {
            copies.put(p.getUniqueId(), itemMeta.getDisplayName());
            //TODO feedback
            updateView(p);
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("-paste")) {
            itemMeta.setDisplayName(copies.get(p.getUniqueId()));
            item.setItemMeta(itemMeta);
            //TODO feedback
            updateView(p);
            return;
        }

        StringBuilder bname = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            bname.append(" ").append(args[i]);
        }

        String name = Util.formatText(p, bname.toString(), getPermission());
        if (Util.hasBannedWords(p, name)) {
            //TODO feedback
            return;
        }

        if (!allowedLengthLimit(p, ChatColor.stripColor(name))) {
            Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-rename-length-limit",
                    "", null, true, "%limit%", String.valueOf(lengthLimit)));
            return;
        }

        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        //TODO feedback
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull final CommandSender sender,
                                   final String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length != 2) {
            return Collections.emptyList();
        }
        ItemStack item = this.getItemInHand((Player) sender);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = ItemUtils.getMeta(item);
            if (meta.hasDisplayName()) {
                return CompleteUtility.complete(args[1],
                        meta.getDisplayName().replace('§', '&'), "-clear", "-paste", "-copy");
            }
        }
        return Collections.emptyList();
    }

    private boolean allowedLengthLimit(final Player who,
                                       final String text) {
        if (lengthLimit < 0 || who.hasPermission("itemedit.bypass.rename_length_limit"))
            return true;
        return text.length() <= lengthLimit;
    }
}
