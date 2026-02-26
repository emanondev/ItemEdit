package emanondev.itemedit.command.itemkinetic;

import emanondev.itemedit.command.ItemKineticCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.implementations.KineticWeaponComponentCondition;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DismountConditions extends SubCmd {
    public DismountConditions(ItemKineticCommand command) {
        super("dismountconditions", command, true, true);
    }

    //if contactcooldownticks <animation>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length >= 2 && args.length <= 4) {
            onFail(player, alias);
            return;
        }
        int maxDurationTicks = Integer.parseInt(args[1]);
        double minSpeed = Double.parseDouble(args[2]);
        double minRelativeSpeed = Double.parseDouble(args[3]);
        KineticWeaponComponent.Condition condition = new KineticWeaponComponentCondition(
                maxDurationTicks, (float) minSpeed, (float) minRelativeSpeed);
        new ItemBuilder(getItemInHand(player)).setKineticDismountingConditions(condition).build();
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], List.of("20", "40", "60"));
            case 3, 4 -> CompleteUtility.complete(args[args.length - 1], List.of("1", "1.5", "0.5"));
            default -> List.of();
        };
    }
}
