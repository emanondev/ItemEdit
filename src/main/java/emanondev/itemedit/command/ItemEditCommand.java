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
        this.registerSubCommand(() -> new HideToolTip(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new Unbreakable(this));
        this.registerSubCommand(() -> new Equipment(this), VersionUtils.isVersionAfter(1, 21,2)); // 1.20+
        this.registerSubCommand(() -> new RepairCost(this));

        this.registerSubCommand(() -> new Food(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new MaxStackSize(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new MaxDurability(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new FireResistent(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new Glider(this), VersionUtils.isVersionAfter(1, 21, 2));
        this.registerSubCommand(() -> new Glow(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new Rarity(this), VersionUtils.isVersionAfter(1, 20, 5));
        this.registerSubCommand(() -> new Amount(this));

        this.registerSubCommand(() -> new Damage(this));
        this.registerSubCommand(() -> new Banner(this));
        this.registerSubCommand(() -> VersionUtils.isVersionUpTo(1, 10) ?
                new ColorOld(this) : new ColorSubcommand(this));
        this.registerSubCommand(() -> new SkullOwner(this));
        this.registerSubCommand(() -> new FireworkPower(this));
        this.registerSubCommand(() -> new Firework(this));
        this.registerSubCommand(() -> new PotionEffectEditor(this)); // 1.15+ adds suspicious stew
        this.registerSubCommand(() -> new BookAuthor(this));

        this.registerSubCommand(() -> new BookType(this), VersionUtils.isVersionAfter(1, 10));
        this.registerSubCommand(() -> new SpawnerEggType(this), VersionUtils.isVersionInRange(1, 11, 1, 12)); // 1.11 & 1.12 only
        this.registerSubCommand(() -> new Attribute(this), VersionUtils.isVersionAfter(1, 13)); // 1.13+
        this.registerSubCommand(() -> new TropicalFish(this), VersionUtils.isVersionAfter(1, 13)); // 1.13+
        this.registerSubCommand(() -> new CustomModelData(this), VersionUtils.isVersionAfter(1, 14)); // 1.14+
        this.registerSubCommand(() -> new ItemModel(this), VersionUtils.isVersionAfter(1, 21, 2)); // 1.21.2+
        this.registerSubCommand(() -> new ToolTipStyle(this), VersionUtils.isVersionAfter(1, 21, 2));
        this.registerSubCommand(() -> new Compass(this), VersionUtils.isVersionAfter(1, 16)); // 1.16+
        this.registerSubCommand(() -> new AxolotlVariant(this), VersionUtils.isVersionAfter(1, 17)); // 1.17+

        this.registerSubCommand(() -> new GoatHornSound(this), VersionUtils.isVersionAfter(1, 19, 3)); // 1.19.3+
        this.registerSubCommand(() -> new Trim(this), VersionUtils.isVersionAfter(1, 20)); // 1.20+
        this.registerSubCommand(() -> new BookEnchant(this));

        //as last
        this.registerSubCommand(() -> new Type(this));
        this.registerSubCommand(() -> new ListAliases(this));
    }

    public static ItemEditCommand get() {
        return instance;
    }

}
