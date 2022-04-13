package emanondev.itemedit;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class will automatically register as a placeholder expansion when a jar
 * including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server. <br>
 * <br>
 * If you create such a class inside your own plugin, you have to register it
 * manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
class PlaceHolders extends PlaceholderExpansion {
    public PlaceHolders() {

        ItemEdit.get().log("Hooked into PlaceHolderAPI:");
        ItemEdit.get().log("placeholders:");
        ItemEdit.get().log("  &e%itemedit_amount_&6<{itemid}>&e_&6[{slot}]&e_&6[{player}]&e%");
        ItemEdit.get().log("    shows how many &6itemid player &fhas on &6slot");
        ItemEdit.get().log("    <{itemid}> for item id on serveritem");
        ItemEdit.get().log("    [{slot}] for the slot where the item should be counted, by default &ainventory");
        ItemEdit.get().log("      Values: &einventory&f, &eequip&f, &einventoryandequip&f, &ehand&f, &eoffhand&f, &ehead&f, &echest&f, &elegs&f, &efeet");
        ItemEdit.get().log("    [{player}] for the player, by default &aself");
        ItemEdit.get().log("    example: %itemedit_amount_{&6my_item_id&f}_{&6hand&f}%");
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public @NotNull String getAuthor() {
        return "emanon";
    }

    /**
     * The placeholder identifier should go here. <br>
     * This is what tells PlaceholderAPI to call our onRequest method to obtain a
     * value if a placeholder starts with our identifier. <br>
     * This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "itemedit";
    }

    /**
     * if the expansion requires another plugin as a dependency, the proper name of
     * the dependency should go here. <br>
     * Set this to {@code null} if your placeholders do not require another plugin
     * to be installed on the server for them to work. <br>
     * <br>
     * This is extremely important to set your plugin here, since if you don't do
     * it, your expansion will throw errors.
     *
     * @return The name of our dependency.
     */
    @Override
    public String getRequiredPlugin() {
        return ItemEdit.get().getName();
    }

    /**
     * This is the version of this expansion. <br>
     * You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    /**
     * This is the method called when a placeholder with our identifier is found and
     * needs a value. <br>
     * We specify the value identifier in this method. <br>
     * Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player A {@link org.bukkit.entity.Player Player}.
     * @param value  A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String value) {
        if (player == null) {
            return "";
        }
        try {
            String[] args = value.split("_");
            switch (args[0]) {
                case "amount": {
                    String slot = "inventory";
                    int amount = 0;
                    String id = args[1];
                    int indexS = value.indexOf("{");
                    int indexE = value.indexOf("}", indexS);
                    if (indexS == -1 || indexE == -1)
                        throw new IllegalStateException();

                    id = value.substring(indexS + 1, indexE);

                    indexS = value.indexOf("{", indexE);
                    indexE = value.indexOf("}", indexS);
                    if (indexS > indexE)
                        throw new IllegalStateException();

                    if (indexS != -1) {
                        slot = value.substring(indexS + 1, indexE).toLowerCase();

                        indexS = value.indexOf("{", indexE);
                        indexE = value.indexOf("}", indexS);
                        if (indexS > indexE)
                            throw new IllegalStateException();
                        if (indexS != -1)
                            player = Bukkit.getPlayer(value.substring(indexS + 1, indexE));
                    }
                    if (player == null)
                        throw new IllegalStateException();

                    ItemStack item = ItemEdit.get().getServerStorage().getItem(id, player);
                    if (item == null)
                        throw new IllegalStateException();

                    switch (slot.toLowerCase()) {
                        case "main_hand":
                        case "mainhand":
                        case "hand": {
                            ItemStack copy;
                            try {
                                copy = player.getEquipment().getItemInMainHand();
                            } catch (Throwable t) {
                                copy = player.getEquipment().getItemInMainHand();
                            }
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "off_hand":
                        case "offhand": {
                            ItemStack copy = player.getEquipment().getItemInOffHand();
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "legs": {
                            ItemStack copy = player.getEquipment().getItemInOffHand();
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "chest": {
                            ItemStack copy = player.getEquipment().getChestplate();
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "head": {
                            ItemStack copy = player.getEquipment().getHelmet();
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "feet": {
                            ItemStack copy = player.getEquipment().getBoots();
                            if (item.isSimilar(copy))
                                amount = amount + copy.getAmount();
                            break;
                        }
                        case "inventory": {
                            for (ItemStack copy : player.getInventory().getStorageContents())
                                if (item.isSimilar(copy))
                                    amount += copy.getAmount();
                            break;
                        }
                        case "equip": {
                            for (ItemStack copy : player.getInventory().getArmorContents())
                                if (item.isSimilar(copy))
                                    amount = amount + copy.getAmount();
                            break;
                        }
                        case "inventoryandequip": {
                            for (ItemStack copy : player.getInventory().getStorageContents())
                                if (item.isSimilar(copy))
                                    amount += copy.getAmount();

                            for (ItemStack copy : player.getInventory().getArmorContents())
                                if (item.isSimilar(copy))
                                    amount += copy.getAmount();
                            break;
                        }
                        default: {
                            throw new IllegalStateException();
                        }
                    }
                    return String.valueOf(amount);
                }
                default:
                    throw new IllegalStateException();
            }

        } catch (Exception e) {
            ItemEdit.get().log("&c! &fWrong PlaceHolderValue %" + getIdentifier() + "_" + ChatColor.YELLOW + value
                    + ChatColor.WHITE + "%");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}