package emanondev.itemedit.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import org.jetbrains.annotations.NotNull;

public class ItemEditReloadCommand implements TabExecutor {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("itemedit.itemeditreload")) {
            ItemEdit.get().reload();
            Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditreload.yml").loadString("success", "", true));
        } else
            Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditreload.yml")
                    .loadString("lack-permission", "", true).replace("%permission%", "itemedit.itemeditreload"));
        return true;
    }

}
