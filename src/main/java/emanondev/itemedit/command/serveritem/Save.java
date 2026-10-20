package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class Save extends SubCmd {

    public Save(ServerItemCommand cmd) {
        super("save", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            String id = args[1].toLowerCase(Locale.ENGLISH);
            if (ItemEdit.get().getServerStorage().getItem(id) == null) {
                ItemEdit.get().getServerStorage().setItem(id, this.getItemInMainHand(p).clone());
            } else {
                sendLanguageString("already_used_id", p, "%id%", id);
                return;
            }
            sendLanguageString("success", p, "%id%", id);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }

}
