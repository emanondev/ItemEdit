package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemedit.*;
import emanondev.itemedit.utility.VersionUtils;

public class ItemEditCommand extends AbstractCommand {
    public static ItemEditCommand instance;

    public ItemEditCommand() {
        super("itemedit", ItemEdit.get(), true);
        instance = this;

        this.registerSubCommand(() -> new Rename(this));
        this.registerSubCommand(() -> new Lore(this));
        this.registerSubCommand(() -> new Enchant(this));
        this.registerSubCommand(() -> new Hide(this));
        this.registerSubCommand(() -> new HideAll(this));
        this.registerSubCommand(() -> new HideToolTip(this),
                VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new Unbreakable(this));
        this.registerSubCommand(() -> new SubCmdLink("equipment", this, true, true, ItemEquipmentCommand.get()),
                VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new RepairCost(this));
        this.registerSubCommand(() -> new SubCmdLink("food", this, true, true, ItemFoodCommand.get()),
                VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new SubCmdLink("kinetic", this, true, true, ItemKineticCommand.get()),
                VersionUtils.isAfter(1, 21, 11));
        this.registerSubCommand(() -> new SubCmdLink("pierce", this, true, true, ItemPiercingCommand.get()),
                VersionUtils.isAfter(1, 21, 11));
        this.registerSubCommand(() -> new MaxStackSize(this), VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new MaxDurability(this), VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new FireResistent(this), VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new Glider(this), VersionUtils.isAfter(1, 21, 2));


        this.registerSubCommand(() -> new Glow(this), VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new Rarity(this), VersionUtils.isAfter(1, 20, 5));
        this.registerSubCommand(() -> new Amount(this));

        this.registerSubCommand(() -> new Damage(this));
        this.registerSubCommand(() -> new Banner(this));
        this.registerSubCommand(() -> VersionUtils.isUpTo(1, 10) ?
                new ColorOld(this) : new ColorSubcommand(this));
        this.registerSubCommand(() -> new SkullOwner(this));
        this.registerSubCommand(() -> new FireworkPower(this));
        this.registerSubCommand(() -> new Firework(this));
        this.registerSubCommand(() -> new PotionEffectEditor(this)); // 1.15+ adds suspicious stew
        this.registerSubCommand(() -> new BookAuthor(this));
        this.registerSubCommand(() -> new BookType(this), VersionUtils.isAfter(1, 10));
        this.registerSubCommand(() -> new SpawnerEggType(this), VersionUtils.isInRange(1, 11, 1, 12)); // 1.11 & 1.12 only
        this.registerSubCommand(() -> new Attribute(this), VersionUtils.isAfter(1, 13)); // 1.13+
        this.registerSubCommand(() -> new TropicalFish(this), VersionUtils.isAfter(1, 13)); // 1.13+
        this.registerSubCommand(() -> new CustomModelData(this), VersionUtils.isAfter(1, 14)); // 1.14+
        this.registerSubCommand(() -> new ItemModel(this), VersionUtils.isAfter(1, 21, 2)); // 1.21.2+
        this.registerSubCommand(() -> new ToolTipStyle(this), VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new Compass(this), VersionUtils.isAfter(1, 16)); // 1.16+
        this.registerSubCommand(() -> new AxolotlVariant(this), VersionUtils.isAfter(1, 17)); // 1.17+

        this.registerSubCommand(() -> new GoatHornSound(this), VersionUtils.isAfter(1, 19, 3)); // 1.19.3+
        this.registerSubCommand(() -> new Trim(this), VersionUtils.isAfter(1, 20)); // 1.20+
        this.registerSubCommand(() -> new BookEnchant(this));

        //as last
        this.registerSubCommand(() -> new Type(this));
        this.registerSubCommand(() -> new ListAliases(this));
    }

    public static ItemEditCommand get() {
        return instance;
    }

}
