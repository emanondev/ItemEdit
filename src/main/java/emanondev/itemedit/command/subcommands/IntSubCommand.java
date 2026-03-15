package emanondev.itemedit.command.subcommands;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class IntSubCommand extends SubCmd {


    private final BiConsumer<ItemBuilder, Integer> apply;
    private final BiFunction<String, Player, List<String>> suggestions;

    public IntSubCommand(AbstractCommand command, String id, BiConsumer<ItemBuilder, Integer> apply) {
        this(command, id, apply, List.of());
    }

    public IntSubCommand(AbstractCommand command,
                         String id,
                         BiConsumer<ItemBuilder, Integer> apply,
                         List<String> suggestions) {

        this(command, id, apply, (arg, supplier) -> CompleteUtility.complete(arg, suggestions));
    }

    public IntSubCommand(AbstractCommand command,
                         String id,
                         BiConsumer<ItemBuilder, Integer> apply,
                         BiFunction<String, Player, List<String>> suggestions) {
        super(id, command, true, true);
        this.apply = apply;
        this.suggestions = suggestions;
    }

    //command sound <sound>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        try {
            if (args.length != 2) {
                onFail(player, alias);
                return;
            }
            int value = Integer.parseInt(args[1]);
            ItemBuilder builder = new ItemBuilder(getItemInHand(player));
            apply.accept(builder, value);
            setItemInHand(player, builder.build());
            updateView(player);
        } catch (NumberFormatException e) {
            onFail(player, alias);
        }
    }

    //command sound <sound>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? suggestions.apply(args[1], (Player) sender) : null;
    }
}