package emanondev.itemedit.command.itemedit;

import com.google.common.base.Preconditions;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.ColorGui;
import emanondev.itemedit.utility.ColoredMeta;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ColorSubcommand extends SubCmd {
    private final String tippedArrowPerm;
    private final String potionPerm;
    private final String leatherPerm;
    private final String starsPerm;

    public ColorSubcommand(@NotNull ItemEditCommand cmd) {
        super("color", cmd, true, true);
        tippedArrowPerm = getPermission() + ".tipped_arrow";
        potionPerm = getPermission() + ".potion";
        leatherPerm = getPermission() + ".leather";
        starsPerm = getPermission() + ".firework_star";
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = getItemInHand(p);

        // Get meta type
        ColoredMeta meta = ColoredMeta.of(item);
        if(meta == null) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        // Check permissions, according to type
        switch (meta.getType()) {
            case POTION:
                if (item.getType() == Material.TIPPED_ARROW && !sender.hasPermission(tippedArrowPerm)) {
                    getCommand().sendPermissionLackMessage(tippedArrowPerm, sender);
                    return;
                }
                if (item.getType().name().contains("POTION") && !sender.hasPermission(potionPerm)) {
                    getCommand().sendPermissionLackMessage(potionPerm, sender);
                    return;
                }
                break;
            case LEATHER_ARMOR:
                if (!sender.hasPermission(leatherPerm)) {
                    getCommand().sendPermissionLackMessage(leatherPerm, sender);
                    return;
                }
                break;
            case FIREWORK:
                if (!sender.hasPermission(starsPerm)) {
                    this.getCommand().sendPermissionLackMessage(starsPerm, sender);
                    return;
                }
                break;
        }

        // Change the color or open a GUI, according to the type and args count.
        try {
            if (args.length == 1) {
                p.openInventory(new ColorGui(p, item).getInventory());
                return;
            }

            // Read color from args
            meta.setColor(parseColor(args));

            // Apply and update
            meta.setToItem(item);
            updateView(p);
        } catch(Exception e) {
            onFail(p, alias);
        }
    }

    /**
     * Parse a bukkit Color from an argument array.
     * @param args arguments array. Size must be > 3.
     * @return a new color instance.
     */
    @Contract("_ -> new")
    private @NotNull Color parseColor(String @NotNull [] args) {
        Preconditions.checkArgument(args.length >= 4, "Missing parameters.");
        return Color.fromRGB(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3])
        );
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
