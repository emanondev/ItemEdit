package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.MusicInstrument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoatHornSound extends SubCmd {
    public GoatHornSound(ItemEditCommand cmd) {
        super("goathornsound", cmd, true, true);
        MusicInstrument.values(); //force load the class or throw an exception if absent
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(player));
        if (!item.isMetaClass(MusicInstrumentMeta.class)) {
            getPlugin().getTranslator().send(player, "generic.error.wrong-material_music_instrument");
            return;
        }

        if (args.length != 2) {
            onFail(player, alias);
            return;
        }
        MusicInstrument type = Aliases.GOAT_HORN_SOUND.convertAlias(args[1]);
        if (type == null) {
            onWrongAlias(player, Aliases.GOAT_HORN_SOUND);
            onFail(player, alias);
            return;
        }
        item.setMusicInstrument(type).build();
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.GOAT_HORN_SOUND);
        }
        return List.of();
    }
}
