package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.utility.ItemBuilder;

public class ItemPiercingCommand extends AbstractCommand {

    private static final ItemPiercingCommand instance = new ItemPiercingCommand();

    private ItemPiercingCommand() {
        super("itemeditpiercing", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new BooleanSubCommand(this, "dealsknockback",
                ItemBuilder::isPiercingDealsKnockback,
                ItemBuilder::setPiercingDealsKnockback));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "dismounts",
                ItemBuilder::isPiercingDismounts,
                ItemBuilder::setPiercingDismounts));
        this.registerSubCommand(() -> new SoundSubCommand(this, "hitsound",
                ItemBuilder::setPiercingHitSound));
        this.registerSubCommand(() -> new SoundSubCommand(this, "sound",
                ItemBuilder::setPiercingSound));
    }

    public static ItemPiercingCommand get() {
        return instance;
    }

}
