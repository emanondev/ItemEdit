package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemfood.*;
import emanondev.itemedit.command.subcommands.BooleanSubCommand;
import emanondev.itemedit.command.subcommands.DoubleSubCommand;
import emanondev.itemedit.command.subcommands.IntSubCommand;
import emanondev.itemedit.command.subcommands.SoundSubCommand;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;

import java.util.List;

public class ItemFoodCommand extends AbstractCommand {

    private static final ItemFoodCommand instance = new ItemFoodCommand();

    public ItemFoodCommand() {
        super("itemeditfood", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new DoubleSubCommand(this, "saturation",
                (b, v) -> b.setSaturation(v.floatValue()), List.of("20", "40", "60")));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "canalwayseat",
                ItemBuilder::canAlwaysEat, ItemBuilder::setCanAlwaysEat));
        this.registerSubCommand(() -> new IntSubCommand(this, "eatticks",
                (b, v) -> b.setConsumeSeconds(v / 20f), List.of("20", "40", "60")));
        this.registerSubCommand(() -> new AddEffect(this),
                VersionUtils.isAfter(1, 21, 4));//TODO
        this.registerSubCommand(() -> new RemoveEffect(this),
                VersionUtils.isAfter(1, 21, 4));//TODO
        this.registerSubCommand(() -> new ResetEffects(this),
                VersionUtils.isAfter(1, 21, 4));
        this.registerSubCommand(() -> new Animation(this),
                VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new ConvertTo(this),
                VersionUtils.isAfter(1, 21));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "consumeparticles",
                        ItemBuilder::hasConsumeParticles, ItemBuilder::setConsumeParticles),
                VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new Nutrition(this));
        this.registerSubCommand(() -> new SoundSubCommand(this, "sound", ItemBuilder::setConsumeSound),
                VersionUtils.isAfter(1, 21, 2));
        ;
        this.registerSubCommand(() -> new Info(this));
        this.registerSubCommand(() -> new Reset(this));
    }

    public static ItemFoodCommand get() {
        return instance;
    }

}
