package emanondev.itemedit.command.subcommands;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public class SoundSubCommand extends SubCmd {


    private final BiConsumer<ItemBuilder, Sound> apply;

    public SoundSubCommand(AbstractCommand command, String id, BiConsumer<ItemBuilder, Sound> apply) {
        super(id, command, true, true);
        this.apply = apply;
    }

    //command sound <sound>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 2) {
            onFail(player, alias);
            return;
        }
        org.bukkit.Sound value = Aliases.SOUND.convertAlias(args[1]);
        if (value == null) {
            onWrongAlias(sender, Aliases.SOUND);
            onFail(player, alias);
            return;
        }
        ItemBuilder builder = new ItemBuilder(getItemInHand(player));
        apply.accept(builder, value);
        setItemInHand(player, builder.build());
        onSuccess(player);
        updateView(player);
    }

    //command sound <sound>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], Aliases.SOUND) : null;
    }
}