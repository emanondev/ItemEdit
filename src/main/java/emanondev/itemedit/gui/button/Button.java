package emanondev.itemedit.gui.button;

import emanondev.itemedit.gui.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Button {

    default Player getTargetPlayer() {
        return getGui().getTargetPlayer();
    }

    @NotNull
    Gui getGui();

    boolean onClick(@NotNull InventoryClickEvent event);

    @Nullable
    ItemStack getItem();
}
