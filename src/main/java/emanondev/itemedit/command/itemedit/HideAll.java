package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class HideAll extends SubCmd {

    public HideAll(final ItemEditCommand cmd) {
        super("hideall", cmd, true, true);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 1) {
                throw new IllegalArgumentException("Wrong param number");
            }
            ItemMeta itemMeta = ItemUtils.getMeta(item);
            handleFlagChange(item, itemMeta);
            itemMeta.addItemFlags(ItemFlag.values());
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    private void handleFlagChange(final ItemStack item, final ItemMeta meta) {
        if (!VersionUtils.hasPaperAPI() ||
                !VersionUtils.isVersionAfter(1, 20, 5) ||
                !ItemEdit.get().getConfig().loadBoolean("itemedit.paper_hide_fix", true)) {
            return;
        }
        if (meta.getAttributeModifiers() != null) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            item.getType().getDefaultAttributeModifiers(slot).forEach(meta::addAttributeModifier);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        return Collections.emptyList();
    }

}
