package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemequipment.*;
import emanondev.itemedit.command.subcommands.BooleanSubCommand;
import emanondev.itemedit.command.subcommands.SoundSubCommand;
import emanondev.itemedit.utility.ItemBuilder;

public class ItemEquipmentCommand extends AbstractCommand {

    private static final ItemEquipmentCommand instance = new ItemEquipmentCommand();

    private ItemEquipmentCommand() {
        super("itemeditequipment", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new Clear(this));
        this.registerSubCommand(() -> new Slot(this));
        this.registerSubCommand(() -> new AllowedEntities(this));
        this.registerSubCommand(() -> new SoundSubCommand(this, "equipsound",
                ItemBuilder::setEquippableEquipSound));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "equiponinteract",
                ItemBuilder::isEquipmentEquipOnInteract,
                ItemBuilder::setEquipmentEquipOnInteract));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "dispensable",
                ItemBuilder::isEquipmentDispensable,
                ItemBuilder::setEquipmentDispensable));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "damageonhurt",
                ItemBuilder::isEquipmentDamageOnHurt,
                ItemBuilder::setEquipmentDamageOnHurt));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "swappable",
                ItemBuilder::isEquipmentSwappable,
                ItemBuilder::setEquipmentSwappable));
        this.registerSubCommand(() -> new BooleanSubCommand(this, "canbesheared",
                ItemBuilder::isEquippableCanBeSheared,
                ItemBuilder::setEquippableCanBeSheared));
        this.registerSubCommand(() -> new CameraOverlay(this));
        this.registerSubCommand(() -> new SoundSubCommand(this, "shearingsound",
                ItemBuilder::setEquippableShearingSound));
        this.registerSubCommand(() -> new Model(this));
    }

    public static ItemEquipmentCommand get() {
        return instance;
    }

}
