package emanondev.itemedit.gui.button;

import emanondev.itemedit.gui.Gui;

public abstract class SimpleButton implements Button {

    private final Gui gui;

    public SimpleButton(Gui gui) {
        this.gui = gui;
    }

    @Override
    public Gui getGui() {
        return gui;
    }
}
