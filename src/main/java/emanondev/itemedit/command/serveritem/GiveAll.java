package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GiveAll extends SubCmd {

    public GiveAll(ServerItemCommand cmd) {
        super("giveall", cmd, false, false);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        try {
            if (Bukkit.getOnlinePlayers().size() == 0)
                return;
            // <id> [amount] [silent]
            if (args.length < 2 || args.length > 4) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 4 ? (Aliases.BOOLEAN.convertAlias(args[3])) : ((Boolean) false);
            if (silent == null)
                silent = Boolean.valueOf(args[3]);
            int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
            if (amount < 1)
                throw new IllegalArgumentException("Wrong amount number");
            ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
            ItemMeta meta = null;
            List<String> lore = null;
            String title = null;
            if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
                meta = item.getItemMeta();
                lore = meta.hasLore() ? meta.getLore() : null;
                title = meta.hasDisplayName() ? meta.getDisplayName() : null;
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
                    meta.setDisplayName(UtilsString.fix(title, target, true, "%player_name%",
                            target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                    meta.setLore(UtilsString.fix(lore, target, true, "%player_name%", target.getName(),
                            "%player_uuid%", target.getUniqueId().toString()));
                    item.setItemMeta(meta);
                }
                UtilsInventory.giveAmount(target, item, amount, ExcessManage.DROP_EXCESS);
                if (!silent)
                    sendLanguageString("feedback", null, target, "%id%", args[1].toLowerCase(),
                            "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                            String.valueOf(amount));
            }

            if (ItemEdit.get().getConfig().loadBoolean("log.action.giveall", true)) {
                StringBuilder sb = new StringBuilder("[");
                for (Player target : Bukkit.getOnlinePlayers())
                    sb.append(target.getName()).append(", ");

                String msg = UtilsString.fix(this.getConfigString("log"), null, true, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%targets%", sb.delete(sb.length() - 2, sb.length()).append("]").toString());
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true))
                    Util.sendMessage(Bukkit.getConsoleSender(), msg);
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true))
                    Util.logToFile(msg);
            }
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        switch (args.length) {
            // <id> [amount] [silent]
            case 2:
                return Util.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3:
                return Util.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4:
                return Util.complete(args[3], Aliases.BOOLEAN);
        }
        return Collections.emptyList();
    }

}
