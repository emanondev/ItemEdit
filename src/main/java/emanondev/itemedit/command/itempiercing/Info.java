package emanondev.itemedit.command.itempiercing;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemPiercingCommand;
import emanondev.itemedit.command.SubCmd;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Info extends SubCmd {
    public Info(ItemPiercingCommand command) {
        super("info", command, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = getItemInMainHand(player);
        PiercingWeapon weapon = item.getData(DataComponentTypes.PIERCING_WEAPON);
        if (weapon == null) {
            Util.sendMessage2(player, translate("not_set", player));
            return;
        }

        Util.sendMessage2(player, translateList("message", player,
                "%dealsknockback%", String.valueOf(weapon.dealsKnockback()),
                "%dismounts%", String.valueOf(weapon.dismounts()),
                "%hitsound%", Objects.toString(weapon.hitSound(), "-"),
                "%sound%", Objects.toString(weapon.sound(), "-")));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
