package emanondev.itemedit.command.itemstorage;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Save extends SubCmd {

    public Save(ItemStorageCommand cmd) {
        super("save", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int limit = ItemEdit.get().getConfig().loadInteger("storage.player-item-limit", 45);
            if (limit >= 0 && ItemEdit.get().getPlayerStorage().getIds(p).size() >= limit) {
                sendLanguageString("limit-reached", null, p, "%limit%",
                        String.valueOf(limit));
                return;
            }
            if (ItemEdit.get().getPlayerStorage().getItem(p, args[1]) == null) {
                ItemEdit.get().getPlayerStorage().setItem(p, args[1], this.getItemInHand(p).clone());
            } else {
                throw new IllegalArgumentException();
            }
            sendLanguageString("success", null, p, "%id%",
                    args[1].toLowerCase(Locale.ENGLISH));
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
