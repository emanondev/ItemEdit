package emanondev.itemedit.gui.button;

import emanondev.itemedit.gui.Gui;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleButton implements Button {

    private final Gui gui;

    public SimpleButton(@NotNull Gui gui) {
        this.gui = gui;
    }

    @Override
    public @NotNull Gui getGui() {
        return gui;
    }
}
