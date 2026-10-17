package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemedit.*;
import emanondev.itemedit.command.subcommands.BooleanSubCommand;
import emanondev.itemedit.command.subcommands.SubCmdLink;
import emanondev.itemedit.utility.ItemBuilder;
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
        this.registerSubCommand(() -> new BooleanSubCommand(this, "hidetooltip",
                ItemBuilder::isHideToolTip, ItemBuilder::setHideToolTip));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "unbreakable",
                ItemBuilder::isUnbreakable, ItemBuilder::setUnbreakable));
        this.registerSubCommand(() -> new SubCmdLink("equipment", this, true, true, ItemEquipmentCommand.get()),
                VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new RepairCost(this));
        this.registerSubCommand(() -> new SubCmdLink("food", this, true, true, ItemFoodCommand.get()));
        this.registerSubCommand(() -> new SubCmdLink("kinetic", this, true, true, ItemKineticCommand.get()),
                VersionUtils.isAfter(1, 21, 11));
        this.registerSubCommand(() -> new SubCmdLink("pierce", this, true, true, ItemPiercingCommand.get()),
                VersionUtils.isAfter(1, 21, 11));
        this.registerSubCommand(() -> new MaxStackSize(this));
        this.registerSubCommand(() -> new MaxDurability(this));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "fireresistent",
                ItemBuilder::isFireResistent, ItemBuilder::setFireResistent));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "glider",
                        ItemBuilder::isGlider, ItemBuilder::setGlider),
                VersionUtils.isAfter(1, 21, 2));

        this.registerSubCommand(() -> new Glow(this));
        this.registerSubCommand(() -> new Rarity(this));
        this.registerSubCommand(() -> new Amount(this));

        this.registerSubCommand(() -> new Damage(this));
        this.registerSubCommand(() -> new Banner(this));
        this.registerSubCommand(() -> new Color(this));
        this.registerSubCommand(() -> new SkullOwner(this));
        this.registerSubCommand(() -> new FireworkPower(this));
        this.registerSubCommand(() -> new Firework(this));
        this.registerSubCommand(() -> new PotionEffectEditor(this)); // 1.15+ adds suspicious stew
        this.registerSubCommand(() -> new BookAuthor(this));
        this.registerSubCommand(() -> new BookType(this));
        this.registerSubCommand(() -> new Attribute(this)); // 1.13+
        this.registerSubCommand(() -> new TropicalFish(this)); // 1.13+
        this.registerSubCommand(() -> new CustomModelData(this)); // 1.14+
        this.registerSubCommand(() -> new ItemModel(this), VersionUtils.isAfter(1, 21, 2)); // 1.21.2+
        this.registerSubCommand(() -> new ToolTipStyle(this), VersionUtils.isAfter(1, 21, 2));
        this.registerSubCommand(() -> new Compass(this)); // 1.16+
        this.registerSubCommand(() -> new AxolotlVariant(this)); // 1.17+

        this.registerSubCommand(() -> new GoatHornSound(this)); // 1.19.3+
        this.registerSubCommand(() -> new ArmorTrim(this)); // 1.20+
        this.registerSubCommand(() -> new BookEnchant(this));

        //as last
        this.registerSubCommand(() -> new Type(this));
        this.registerSubCommand(() -> new ListAliases(this));
    }

    public static ItemEditCommand get() {
        return instance;
    }

}
