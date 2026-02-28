package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HideAll extends SubCmd {

    public HideAll(ItemEditCommand cmd) {
        super("hideall", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length != 1) {
            onFail(p, alias);
            return;
        }
        handleFlagChange(item);
        item.setItemFlags(ItemFlag.values(), true).build();
        onSuccess(p);
        updateView(p);
    }

    private void handleFlagChange(ItemBuilder item) {
        if (!VersionUtils.hasPaperAPI() ||
                !VersionUtils.isAfter(1, 20, 5) ||
                !ItemEdit.get().getConfig().loadBoolean("itemedit.paper_hide_fix", true)) {
            return;
        }
        if (item.getAttributeModifiers() != null) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            item.getType().getDefaultAttributeModifiers(slot).forEach(item::addAttributeModifier);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }

}
