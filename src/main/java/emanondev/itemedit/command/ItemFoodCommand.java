package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemfood.*;
import emanondev.itemedit.utility.VersionUtils;

public class ItemFoodCommand extends AbstractCommand {

    private static final ItemFoodCommand instance = new ItemFoodCommand();

    private ItemFoodCommand() {
        super("itemfood", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new Saturation(this));
        this.registerSubCommand(() -> new CanAlwaysEat(this));
        this.registerSubCommand(() -> new EatTicks(this));
        this.registerSubCommand(() -> new AddEffect(this), VersionUtils.isVersionAfter(1,21,4));//TODO
        this.registerSubCommand(() -> new RemoveEffect(this), VersionUtils.isVersionAfter(1,21,4));//TODO
        this.registerSubCommand(() -> new ClearEffects(this), VersionUtils.isVersionAfter(1,21,4));
        this.registerSubCommand(() -> new Animation(this), VersionUtils.isVersionAfter(1, 21, 2));
        this.registerSubCommand(() -> new ConvertTo(this), VersionUtils.isVersionAfter(1, 21));
        this.registerSubCommand(() -> new ConsumeParticles(this), VersionUtils.isVersionAfter(1, 21, 2));
        this.registerSubCommand(() -> new Nutrition(this));
        this.registerSubCommand(() -> new Sound(this), VersionUtils.isVersionAfter(1, 21, 2));
        this.registerSubCommand(() -> new Info(this));
        this.registerSubCommand(() -> new Clear(this));
    }

    public static ItemFoodCommand get() {
        return instance;
    }

}
