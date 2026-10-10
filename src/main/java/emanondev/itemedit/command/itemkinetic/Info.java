package emanondev.itemedit.command.itemkinetic;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemKineticCommand;
import emanondev.itemedit.command.SubCmd;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Info extends SubCmd {
    public Info(ItemKineticCommand command) {
        super("info", command, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = getItemInMainHand(player);
        KineticWeapon weapon = item.getData(DataComponentTypes.KINETIC_WEAPON);
        if (weapon == null) {
            Util.sendMessage2(player, translate("not_set", player));
            return;
        }

        Util.sendMessage2(player, translateList("message", player,
                "%contactcooldownticks%", String.valueOf(weapon.contactCooldownTicks()),
                "%damagemultiplier%", String.valueOf(weapon.damageMultiplier()),
                "%delayticks%", String.valueOf(weapon.delayTicks()),
                "%forwardmovement%", String.valueOf(weapon.forwardMovement()),
                "%hitsound%", Objects.toString(weapon.hitSound(), "-"),
                "%sound%", Objects.toString(weapon.sound(), "-"),
                "%damageconditions%", formatCondition(weapon.damageConditions()),
                "%dismountconditions%", formatCondition(weapon.dismountConditions()),
                "%knockbackconditions%", formatCondition(weapon.knockbackConditions())));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }

    private String formatCondition(KineticWeapon.Condition condition) {
        if (condition == null) {
            return "-";
        }
        return "maxDurationTicks=" + condition.maxDurationTicks()
                + ", minSpeed=" + condition.minSpeed()
                + ", minRelativeSpeed=" + condition.minRelativeSpeed();
    }
}
