package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class Glow extends SubCmd {

    public Glow(ItemEditCommand cmd) {
        super("glow", cmd, true, true);
    }

    //ie glow <true/false/default>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;

        ItemBuilder item = new ItemBuilder(getItemInHand(p));

        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean value = args.length == 1
                    ? ((Boolean) (item.getEnchantmentGlintOverride() != null ?
                    !item.getEnchantmentGlintOverride() : Boolean.TRUE))
                    : Aliases.BOOLEAN.convertAlias(args[1]);
            item.setEnchantmentGlintOverride(value).build();
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return Stream.concat(
                CompleteUtility.complete(args[1], Aliases.BOOLEAN).stream(),
                "default".startsWith(args[1].toLowerCase(Locale.ENGLISH)) ? Stream.of("default") : Stream.empty()
        ).toList();
    }
}