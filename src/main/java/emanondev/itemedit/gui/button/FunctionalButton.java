package emanondev.itemedit.gui.button;

import emanondev.itemedit.gui.Gui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class FunctionalButton extends SimpleButton {
    private final BiPredicate<InventoryClickEvent, FunctionalButton> onClick;
    private final Function<FunctionalButton, ItemStack> getItem;

    public FunctionalButton(@NotNull Gui gui,
                            @NotNull BiPredicate<InventoryClickEvent, FunctionalButton> onClick,
                            @NotNull Function<FunctionalButton, ItemStack> getItem) {
        super(gui);
        this.onClick = onClick;
        this.getItem = getItem;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick.test(event, this);
    }

    @Override
    public ItemStack getItem() {
        return getItem.apply(this);
    }
}
