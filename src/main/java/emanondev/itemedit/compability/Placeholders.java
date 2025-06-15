package emanondev.itemedit.compability;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * This class will automatically register as a placeholder expansion when a jar
 * including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server. <br>
 * <br>
 * If you create such a class inside your own plugin, you have to register it
 * manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
public class Placeholders extends PlaceholderExpansion {
    public Placeholders() {

        //ItemEdit.get().log("Hooked into PlaceHolderAPI:");
        ItemEdit.get().log("placeholders:");
        ItemEdit.get().log("  &e%itemedit_amount_&6<{itemid}>&e_&6[{slot}]&e_&6[{player}]&e%");
        ItemEdit.get().log("    shows how many &6itemid player &fhas on &6slot");
        ItemEdit.get().log("    <{itemid}> for item id on serveritem");
        ItemEdit.get().log("    [{slot}] for the slot where the item should be counted, by default &ainventory");
        ItemEdit.get().log("      Values: &einventory&f (include offhand), &eequip&f (include offhand), &einventoryandequip&f (include offhand), &ehand&f, &eoffhand&f, &ehead&f, &echest&f, &elegs&f, &efeet");
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
            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "amount": {
                    return amount(player, value.substring("amount_".length()));
                }
                default:
                    throw new IllegalStateException();
            }

        } catch (Exception e) {
            ItemEdit.get().log("&c! &fWrong PlaceHolderValue %" + getIdentifier() + "_" + ChatColor.YELLOW + value
                    + ChatColor.WHITE + "% " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    //{itemid}
    //{itemid}_{slot}
    //{itemid}_{slot}_{player}
    private String amount(Player player, String value) {
        String slot = "inventory";
        int amount = 0;
        String id;
        int indexStart = value.indexOf("{");
        int indexEnd = value.indexOf("}", indexStart);
        if (indexStart != 0 || indexEnd == -1)
            throw new IllegalStateException("item id not closed inside { }");
        id = value.substring(indexStart + 1, indexEnd);
        ItemStack item = ItemEdit.get().getServerStorage().getItem(id, player);
        if (item == null)
            throw new IllegalStateException("item id '" + id + "' is invalid or doesn't exist");

        value = value.substring(indexEnd + 1);
        if (!value.isEmpty()) {  //has slot
            indexStart = value.indexOf("{");
            indexEnd = value.indexOf("}");
            if (indexStart != 1)
                throw new IllegalStateException("bad formatting");
            if (indexEnd == -1) //_{..}?..
                throw new IllegalStateException("slot value not closed inside { }");
            slot = value.substring(indexStart + 1, indexEnd).toLowerCase(Locale.ENGLISH);
            ItemEdit.get().log(id + " " + slot + " " + player.getName());
            value = value.substring(indexEnd + 1);

            indexStart = value.indexOf("{", indexEnd);
            indexEnd = value.indexOf("}", indexStart);
            if (indexStart > indexEnd)
                throw new IllegalStateException();
            if (indexStart != -1)
                player = Bukkit.getPlayer(value.substring(indexStart + 1, indexEnd));

            if (player == null)
                throw new IllegalStateException();
        }

        switch (slot.toLowerCase(Locale.ENGLISH)) {
            case "main_hand":
            case "mainhand":
            case "hand": {
                ItemStack copy = ItemUtils.getHandItem(player);
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
                ItemStack copy = player.getEquipment().getLeggings();
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
                if (VersionUtils.isVersionAfter(1, 9)) {
                    if (item.isSimilar(player.getInventory().getItemInOffHand()))
                        amount += player.getInventory().getItemInOffHand().getAmount();
                }
                break;
            }
            case "equip": {
                for (ItemStack copy : player.getInventory().getArmorContents())
                    if (item.isSimilar(copy))
                        amount = amount + copy.getAmount();
                if (VersionUtils.isVersionAfter(1, 9)) {
                    if (item.isSimilar(player.getInventory().getItemInOffHand()))
                        amount += player.getInventory().getItemInOffHand().getAmount();
                }
                break;
            }
            case "inventoryandequip": {
                for (ItemStack copy : player.getInventory().getStorageContents())
                    if (item.isSimilar(copy))
                        amount += copy.getAmount();

                for (ItemStack copy : player.getInventory().getArmorContents())
                    if (item.isSimilar(copy))
                        amount += copy.getAmount();
                if (VersionUtils.isVersionAfter(1, 9)) {
                    if (item.isSimilar(player.getInventory().getItemInOffHand()))
                        amount += player.getInventory().getItemInOffHand().getAmount();
                }

                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return String.valueOf(amount);
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