package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemfood.*;
import emanondev.itemedit.command.subcommands.BooleanSubCommand;
import emanondev.itemedit.command.subcommands.DoubleSubCommand;
import emanondev.itemedit.command.subcommands.IntSubCommand;
import emanondev.itemedit.command.subcommands.SoundSubCommand;
import emanondev.itemedit.utility.ItemBuilder;

import java.util.List;

public class ItemFoodCommand extends AbstractCommand {

    private static final ItemFoodCommand instance = new ItemFoodCommand();

    public ItemFoodCommand() {
        super("itemeditfood", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new DoubleSubCommand(this, "saturation",
                (b, v) -> {
                    if (v != null && v >= 0) {
                        b.setSaturation(v.floatValue());
                        return true;
                    }
                    return false;
                }, List.of("20", "40", "60")));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "canalwayseat",
                ItemBuilder::canAlwaysEat, ItemBuilder::setCanAlwaysEat));
        this.registerSubCommand(() -> new IntSubCommand(this, "eatticks",
                (b, v) -> {
                    if (v == null || v <= 0) {
                        return false;
                    }
                    b.setConsumeSeconds(v / 20f);
                    return true;
                }, List.of("20", "40", "60")));
        this.registerSubCommand(() -> new AddEffect(this));
        this.registerSubCommand(() -> new RemoveEffect(this));
        this.registerSubCommand(() -> new ResetEffects(this));
        this.registerSubCommand(() -> new Animation(this));
        this.registerSubCommand(() -> new ConvertTo(this));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "consumeparticles",
                ItemBuilder::hasConsumeParticles, ItemBuilder::setConsumeParticles));
        this.registerSubCommand(() -> new Nutrition(this));
        this.registerSubCommand(() -> new SoundSubCommand(this, "sound", ItemBuilder::setConsumeSound));
        this.registerSubCommand(() -> new Info(this));
        this.registerSubCommand(() -> new Reset(this));
    }

    public static ItemFoodCommand get() {
        return instance;
    }

}
