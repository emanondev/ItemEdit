package emanondev.itemedit.command;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooleanSubCommand extends SubCmd {


    private final BiConsumer<ItemBuilder, Boolean> applier;
    private final Function<ItemBuilder, Boolean> getter;

    public BooleanSubCommand(AbstractCommand command, String id,
                             Function<ItemBuilder, Boolean> getter,
                             BiConsumer<ItemBuilder, Boolean> applier) {
        super(id, command, true, true);
        this.applier = applier;
        this.getter = getter;
    }

    //command sound <sound>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder builder = new ItemBuilder(getItemInHand(p));
        if (args.length != 1 && args.length != 2) {
            onFail(p, alias);
            return;
        }
        Boolean value = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : (Boolean) !getter.apply(builder);
        if (value == null) {
            onWrongAlias(p, Aliases.BOOLEAN);
            onFail(p, alias);
            return;
        }
        applier.accept(builder, value);
        builder.build();
        onSuccess(p, "%value%", String.valueOf(value));
    }

    //command sound <sound>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], Aliases.BOOLEAN) : List.of();
    }
}