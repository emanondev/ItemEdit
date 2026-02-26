package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemkinetic.DamageConditions;
import emanondev.itemedit.command.itemkinetic.DismountConditions;
import emanondev.itemedit.command.itemkinetic.KnockbackConditions;
import emanondev.itemedit.utility.ItemBuilder;

import java.util.List;

public class ItemKineticCommand extends AbstractCommand {

    private static final ItemKineticCommand instance = new ItemKineticCommand();

    private ItemKineticCommand() {
        super("itemeditkinetic", ItemEdit.get(), true);
        //version is >= 1.20.5
        this.registerSubCommand(() -> new IntSubCommand(this, "contactcooldownticks",
                ItemBuilder::setKineticContactCooldownTicks, List.of("20", "40", "60")));
        this.registerSubCommand(() -> new DoubleSubCommand(this, "damagemultiplier",
                (b, d) -> b.setKineticDamageMultiplier(d.floatValue()), List.of("1", "1.5", "0.5")));
        this.registerSubCommand(() -> new IntSubCommand(this, "delayticks",
                ItemBuilder::setKineticDelayTicks, List.of("20", "40", "60")));
        this.registerSubCommand(() -> new DoubleSubCommand(this, "forwardmovement",
                (b, d) -> b.setKineticForwardMovement(d.floatValue()), List.of("1", "1.5", "0.5")));
        this.registerSubCommand(() -> new SoundSubCommand(this, "hitsound", ItemBuilder::setKineticHitSound));
        this.registerSubCommand(() -> new SoundSubCommand(this, "sound", ItemBuilder::setKineticSound));
        this.registerSubCommand(() -> new DamageConditions(this));
        this.registerSubCommand(() -> new KnockbackConditions(this));
        this.registerSubCommand(() -> new DismountConditions(this));
    }

    public static ItemKineticCommand get() {
        return instance;
    }

}
