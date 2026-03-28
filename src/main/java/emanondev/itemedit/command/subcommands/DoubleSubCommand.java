package emanondev.itemedit.command.subcommands;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class DoubleSubCommand extends SubCmd {


    private final BiFunction<ItemBuilder, Double, Boolean> apply;
    private final List<String> suggestions;

    public DoubleSubCommand(AbstractCommand command, String id,
                            BiFunction<ItemBuilder, Double, Boolean> apply) {
        this(command, id, apply, List.of());
    }

    public DoubleSubCommand(AbstractCommand command, String id,
                            BiFunction<ItemBuilder, Double, Boolean> apply,
                            List<String> suggestions) {
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
            double value = Double.parseDouble(args[1]);
            ItemBuilder builder = new ItemBuilder(getItemInHand(player));
            if (apply.apply(builder, value)) {
                onSuccess(player, "%value%", String.valueOf(value));
                setItemInHand(player, builder.build());
                updateView(player);
                return;
            }
            onFail(player, alias);
        } catch (NumberFormatException e) {
            onFail(player, alias);
        }
    }

    //command sound <sound>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], suggestions) : null;
    }
}