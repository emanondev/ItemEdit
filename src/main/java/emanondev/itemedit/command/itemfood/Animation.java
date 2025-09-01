package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Animation extends SubCmd {
    public Animation(ItemFoodCommand itemFoodCommand) {
        super("animation", itemFoodCommand, true,true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {

    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
