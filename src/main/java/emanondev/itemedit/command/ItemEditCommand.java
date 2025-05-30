package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemedit.*;
import emanondev.itemedit.utility.VersionUtils;

public class ItemEditCommand extends AbstractCommand {
    public static ItemEditCommand instance;

    public ItemEditCommand() {
        super("itemedit", ItemEdit.get(), true);
        instance = this;
        try {
            this.registerSubCommand(new Rename(this));
            this.registerSubCommand(new Lore(this));
            this.registerSubCommand(new Enchant(this));
            this.registerSubCommand(new Hide(this));
            this.registerSubCommand(new HideAll(this));
            this.registerSubCommand(new Netherite(this));
            if (VersionUtils.isVersionAfter(1, 20, 5))
                this.registerSubCommand(new HideToolTip(this));
            if (VersionUtils.isVersionAfter(1, 20, 5))
                this.registerSubCommand(new FireResistent(this));

            this.registerSubCommand(new Unbreakable(this));
            this.registerSubCommand(new RepairCost(this));
            if (VersionUtils.isVersionAfter(1, 20, 5)) {
                this.registerSubCommand(new Food(this));
                this.registerSubCommand(new MaxStackSize(this));
                this.registerSubCommand(new MaxDurability(this));
            }
            if (VersionUtils.isVersionAfter(1, 21, 2)) {
                this.registerSubCommand(new Glider(this));
            }
            if (VersionUtils.isVersionAfter(1, 20, 5))
                this.registerSubCommand(new Glow(this));
            if (VersionUtils.isVersionAfter(1, 20, 5))
                this.registerSubCommand(new Rarity(this));

            this.registerSubCommand(new Amount(this));
            this.registerSubCommand(new Damage(this));
            this.registerSubCommand(new Banner(this));
            if (VersionUtils.isVersionUpTo(1, 10))
                this.registerSubCommand(new ColorOld(this));
            else
                this.registerSubCommand(new Color(this));// 1.11+ add potions and tipped arrows
            this.registerSubCommand(new SkullOwner(this));
            this.registerSubCommand(new FireworkPower(this));
            this.registerSubCommand(new Firework(this));
            if (VersionUtils.isVersionUpTo(1, 14))
                this.registerSubCommand(new PotionEffectEditorOld(this));
            else
                this.registerSubCommand(new PotionEffectEditor(this));//1.15+ adds suspicious stew
            this.registerSubCommand(new BookAuthor(this));
            if (VersionUtils.isVersionUpTo(1, 9))
                return;
            this.registerSubCommand(new BookType(this));// 1.10+
            if (VersionUtils.isVersionUpTo(1, 10))
                return;
            if (VersionUtils.isVersionInRange(1, 11,
                    1, 12)) {
                this.registerSubCommand(new SpawnerEggType(this));// 1.11 & 1.12 only
                return;
            }
            this.registerSubCommand(new Attribute(this));// 1.13+
            this.registerSubCommand(new TropicalFish(this));// 1.13+
            if (VersionUtils.isVersionUpTo(1, 13))
                return;
            this.registerSubCommand(new CustomModelData(this));// 1.14+
            if (VersionUtils.isVersionAfter(1, 21, 2))
                this.registerSubCommand(new ItemModel(this));// 1.21.2+
            if (VersionUtils.isVersionUpTo(1, 15))
                return;
            if (VersionUtils.isVersionAfter(1, 21, 2))
                this.registerSubCommand(new ToolTipStyle(this));
            this.registerSubCommand(new Compass(this));// 1.16+
            if (VersionUtils.isVersionUpTo(1, 16))
                return;
            this.registerSubCommand(new AxolotlVariant(this));// 1.17+
            if (VersionUtils.isVersionUpTo(1, 19, 2))
                return;
            try {
                this.registerSubCommand(new GoatHornSound(this));// 1.19.3+
            } catch (Throwable ignored) {
                //avoid some issues
            }
            if (VersionUtils.isVersionUpTo(1, 19, 5))
                return;
            this.registerSubCommand(new Trim(this));// 1.20+
        } finally {
            this.registerSubCommand(new BookEnchant(this));
            this.registerSubCommand(new Type(this));
            this.registerSubCommand(new ListAliases(this));
        }

    }

    public static ItemEditCommand get() {
        return instance;
    }

}
