package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.attribute.AttributeModifier.Operation;

public class OperationAliases extends EnumAliasSet<Operation> {

    public OperationAliases() {
        super(ItemEdit.get(), Operation.class);
    }
}