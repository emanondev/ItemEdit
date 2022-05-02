package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemedit.*;

public class ItemEditCommand extends AbstractCommand {
    public static ItemEditCommand instance;

    public static ItemEditCommand get() {
        return instance;
    }

    public ItemEditCommand() {
        super("itemedit", ItemEdit.get());
        instance = this;
        try {
            this.registerSubCommand(new Rename(this));
            this.registerSubCommand(new Lore(this));
            this.registerSubCommand(new Enchant(this));
            this.registerSubCommand(new Hide(this));
            this.registerSubCommand(new HideAll(this));

            this.registerSubCommand(new Unbreakable(this));
            this.registerSubCommand(new RepairCost(this));
            this.registerSubCommand(new Amount(this));
            this.registerSubCommand(new Damage(this));
            this.registerSubCommand(new Banner(this));
            if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_10_R"))
                this.registerSubCommand(new ColorOld(this));
            else
                this.registerSubCommand(new Color(this));// 1.11+ add potions and tipped arrows
            this.registerSubCommand(new SkullOwner(this));
            this.registerSubCommand(new FireworkPower(this));
            this.registerSubCommand(new Firework(this));
            if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_12_R") || ItemEdit.NMS_VERSION.startsWith("v1_13_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_14_R"))
                this.registerSubCommand(new PotionEffectEditorOld(this));
            else
                this.registerSubCommand(new PotionEffectEditor(this));//1.15+ adds suspicious stew
            this.registerSubCommand(new BookAuthor(this));
            if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R"))
                return;
            this.registerSubCommand(new BookType(this));// 1.10+
            if (ItemEdit.NMS_VERSION.startsWith("v1_10_R"))
                return;
            if (ItemEdit.NMS_VERSION.startsWith("v1_11_R") || ItemEdit.NMS_VERSION.startsWith("v1_12_R"))
                this.registerSubCommand(new SpawnerEggType(this));// 1.11 & 1.12 only
            if (ItemEdit.NMS_VERSION.startsWith("v1_11_R") || ItemEdit.NMS_VERSION.startsWith("v1_12_R"))
                return;
            this.registerSubCommand(new Attribute(this));// 1.13+
            this.registerSubCommand(new TropicalFish(this));// 1.13+
            if (ItemEdit.NMS_VERSION.startsWith("v1_13_R"))
                return;
            this.registerSubCommand(new CustomModelData(this));// 1.14+
            if (ItemEdit.NMS_VERSION.startsWith("v1_14_R"))
                return;
            if (ItemEdit.NMS_VERSION.startsWith("v1_15_R"))
                return;
            this.registerSubCommand(new Compass(this));// 1.16+
            if (ItemEdit.NMS_VERSION.startsWith("v1_16_R"))
                return;
            this.registerSubCommand(new AxolotlVariant(this));// 1.17+

        } finally {
            this.registerSubCommand(new Type(this));
            this.registerSubCommand(new ListAliases(this));
        }

    }

}
