package emanondev.itemedit.command;

import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public class DoubleSubCommand extends SubCmd {


    private final BiConsumer<ItemBuilder, Double> apply;
    private final List<String> suggestions;

    public DoubleSubCommand(AbstractCommand command, String id, BiConsumer<ItemBuilder, Double> apply) {
        this(command, id, apply, List.of());
    }

    public DoubleSubCommand(AbstractCommand command, String id, BiConsumer<ItemBuilder, Double> apply, List<String> suggestions) {
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
        return args.length == 2 ? CompleteUtility.complete(args[1], suggestions) : null;
    }
}