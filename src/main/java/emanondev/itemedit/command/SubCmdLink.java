package emanondev.itemedit.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SubCmdLink extends SubCmd {

    @Getter
    private final @NotNull AbstractCommand linkedCommand;

    public SubCmdLink(@NotNull String id, @NotNull AbstractCommand command, boolean playerOnly, boolean checkNonNullItem, @NotNull AbstractCommand linkedCommand) {
        super(id, command, playerOnly, checkNonNullItem);
        this.linkedCommand = linkedCommand;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        linkedCommand.onCommand(sender, null, alias, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return linkedCommand.onTabComplete(sender, null, linkedCommand.getName(), Arrays.copyOfRange(args, 1, args.length));
    }
}
