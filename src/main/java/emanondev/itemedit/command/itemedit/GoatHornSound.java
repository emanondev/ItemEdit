package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.MusicInstrument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GoatHornSound extends SubCmd {
    public GoatHornSound(ItemEditCommand cmd) {
        super("goathornsound", cmd, true, true);
        MusicInstrument.values(); //force load the class or throw an exception if absent
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = this.getItemInHand(player);
        if (!(item.getItemMeta() instanceof MusicInstrumentMeta)) {
            Util.sendMessage(player, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            MusicInstrumentMeta meta = (MusicInstrumentMeta) ItemUtils.getMeta(item);
            MusicInstrument type = Aliases.GOAT_HORN_SOUND.convertAlias(args[1]);
            if (type == null) {
                onWrongAlias("wrong-sound", player, Aliases.GOAT_HORN_SOUND);
                onFail(player, alias);
                return;
            }
            meta.setInstrument(type);
            item.setItemMeta(meta);
            updateView(player);
        } catch (Exception e) {
            onFail(player, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.GOAT_HORN_SOUND);
        }
        return Collections.emptyList();
    }
}
