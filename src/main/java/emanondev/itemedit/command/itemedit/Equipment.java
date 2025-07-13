package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.TagContainer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Equipment extends SubCmd {

    private final String[] subCommands = new String[]{"slot", "swappable", "allowedentities", "equipsound",
            "equiponinteract", "dispensable", "damageonhurt", "cameraoverlay", "canshear", "shearsound", "clear"};

    public Equipment(@NotNull ItemEditCommand command) {
        super("equipment", command, true, true);
    }

    /*
     * /ie equipment slot <slot>
     * /ie equipment swappable [true/false]
     * /ie equipment AllowedEntities <EntityType1>
     * /ie equipment AllowedEntities <Tag<EntityType> tag>
     * /ie equipment EquipSound (Sound sound)
     * /ie equipment EquipOnInteract [true/false]
     * /ie equipment Dispensable [true/false]
     * /ie equipment DamageOnHurt [true/false]
     * /ie equipment CameraOverlay (NamespacedKey key)
     * /ie equipment shearsound (Sound sound)
     * /ie equipment canshear [true/false]
     */
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "clear":
                equipmentClear(p, item, alias, args);
                return;
            case "slot":
                equipmentSlot(p, item, alias, args);
                return;
            case "swappable":
                equipmentSwappable(p, item, alias, args);
                return;
            case "allowedentities":
                equipmentAllowedEntities(p, item, alias, args);
                return;
            case "equipsound":
                equipmentEquipSound(p, item, alias, args);
                return;
            case "equiponinteract":
                equipmentEquipOnInteract(p, item, alias, args);
                return;
            case "dispensable":
                equipmentDispensable(p, item, alias, args);
                return;
            case "damageonhurt":
                equipmentDamageOnHurt(p, item, alias, args);
                return;
            case "cameraoverlay":
                equipmentCameraOverlay(p, item, alias, args);
                return;
            case "canshear":
                equipmentCanShear(p, item, alias, args);
                return;
            case "shearsound":
                equipmentShearSound(p, item, alias, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    private void equipmentShearSound(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "shearsound");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            Sound value = args.length == 2 ? null : Aliases.SOUND.convertAlias(args[2]);
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-sound", p, Aliases.SOUND);
                sendFailFeedbackForSub(p, alias, "shearsound");
                return;
            }
            comp.setShearingSound(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            item.setItemMeta(meta);
            if (value != null) {
                sendFeedbackForSub(p, "shearsound", "%value%", args[2]);
            } else {
                sendCustomFeedbackForSub(p, "shearsound", "feedback-reset");
            }
        } catch (NoSuchMethodError e) {
            sendCustomFeedbackForSub(p, "canshear", "unsupported-version",
                    "%value%", Bukkit.getVersion());
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "shearsound");
        }
    }

    private void equipmentCanShear(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "canshear");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            Boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !comp.isCanBeSheared();
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "canshear");
                return;
            }
            comp.setCanBeSheared(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);

            sendFeedbackForSub(p, "canshear", "%value%", String.valueOf(value));
        } catch (NoSuchMethodError e) {
            sendCustomFeedbackForSub(p, "canshear", "unsupported-version",
                    "%value%", Bukkit.getVersion());
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "canshear");
        }
    }

    //it equip cameraoverlay value
    private void equipmentCameraOverlay(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "cameraoverlay");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();

            String rawkey = args.length == 2 ? null : args[2];
            NamespacedKey key = rawkey == null ? null : NamespacedKey.fromString(rawkey);
            if (args.length == 3 && key == null) {
                sendCustomFeedbackForSub(p, "cameraoverlay", "invalid-namespacedkey",
                        "%value%", args[2]);
                return;
            }
            comp.setCameraOverlay(key);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            if (key != null) {
                sendFeedbackForSub(p, "cameraoverlay", "%value%", key.toString());
            } else {
                sendCustomFeedbackForSub(p, "cameraoverlay", "feedback-reset");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "cameraoverlay");
        }
    }

    private void equipmentDamageOnHurt(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "damageonhurt");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            Boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !comp.isDamageOnHurt();
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "damageonhurt");
                return;
            }
            comp.setDamageOnHurt(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "damageonhurt", "%value%", String.valueOf(value));
            if (value && (meta.hasMaxStackSize() ? meta.getMaxStackSize() > 1 : item.getType().getMaxStackSize() > 1)) {
                String msg = getLanguageString("damageonhurt.warning-maxstacksize", null, p);
                if (msg != null && !msg.isEmpty()) {
                    ComponentBuilder compMsg = new ComponentBuilder(msg)
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "/" + alias + " maxstacksize 1"));
                    Util.sendMessage(p, compMsg.create());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "damageonhurt");
        }
    }

    private void equipmentDispensable(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "dispensable");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();

            Boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !comp.isDamageOnHurt();
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "dispensable");
                return;
            }

            comp.setDamageOnHurt(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "dispensable", "%value%", String.valueOf(value));
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "dispensable");
        }
    }

    private void equipmentEquipOnInteract(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "equiponinteract");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();

            Boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !comp.isEquipOnInteract();
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "equiponinteract");
                return;
            }

            comp.setEquipOnInteract(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "equiponinteract", "%value%", String.valueOf(value));
        } catch (NoSuchMethodError e) {
            sendCustomFeedbackForSub(p, "canshear", "unsupported-version",
                    "%value%", Bukkit.getVersion());
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "equiponinteract");
        }
    }

    private void equipmentSwappable(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "swappable");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            Boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !comp.isSwappable();
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "swappable");
                return;
            }

            comp.setSwappable(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "swappable", "%value%", String.valueOf(value));
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "swappable");
        }
    }

    private void equipmentAllowedEntities(Player p, ItemStack item, String alias, String[] args) {
        try {
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            if (args.length == 2) {
                comp.setAllowedEntities((EntityType) null);
                meta.setEquippable(comp);
                item.setItemMeta(meta);
                sendCustomFeedbackForSub(p, "allowedentities", "feedback-reset");
                return;
            }

            Set<EntityType> types = new HashSet<>();
            for (String arg : Arrays.copyOfRange(args, 2, args.length)) {
                EntityType entity = Aliases.ENTITY_TYPE.convertAlias(arg);
                if (entity != null && entity.isAlive()) {
                    types.add(entity);
                    continue;
                }
                TagContainer<EntityType> tag = Aliases.ENTITY_GROUPS.convertAlias(arg);
                if (tag != null && tag.getValues().stream().anyMatch(EntityType::isAlive)) {
                    types.addAll(tag.getValues().stream().filter(EntityType::isAlive).collect(Collectors.toList()));
                    continue;
                }
                onWrongAlias("wrong-entitytype", p, Aliases.ENTITY_TYPE);
                onWrongAlias("wrong-entitygroup", p, Aliases.ENTITY_GROUPS);
                sendCustomFeedbackForSub(p, "allowedentities", "invalid-type",
                        "%value%", arg);
                return;
            }
            comp.setAllowedEntities(types);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "allowedentities",
                    "%value%", types.stream().map(Enum::name).collect(Collectors.joining(", ")));
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "allowedentities");
        }
    }

    private void equipmentEquipSound(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "equipsound");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();

            Sound value = args.length == 2 ? null : Aliases.SOUND.convertAlias(args[2]);
            if (args.length == 3 && value == null) {
                onWrongAlias("wrong-sound", p, Aliases.SOUND);
                sendFailFeedbackForSub(p, alias, "equipsound");
                return;
            }

            comp.setEquipSound(value);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            if (value != null) {
                sendFeedbackForSub(p, "equipsound", "%value%", args[2]);
            } else {
                sendCustomFeedbackForSub(p, "equipsound", "feedback-reset");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "equipsound");
        }
    }

    //ie equipment slot <slot>
    private void equipmentSlot(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "slot");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            EquippableComponent comp = meta.getEquippable();
            EquipmentSlot slot = Aliases.EQUIPMENT_SLOTS.convertAlias(args[2]);
            if (slot == null) {
                onWrongAlias("wrong-slot", p, Aliases.SOUND);
                sendFailFeedbackForSub(p, alias, "slot");
                return;
            }
            comp.setSlot(slot);
            meta.setEquippable(comp);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "slot", "%value%", args[2]);
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "slot");
        }
    }

    private void equipmentClear(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2) {
                sendFailFeedbackForSub(p, alias, "clear");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            meta.setEquippable(null);
            item.setItemMeta(meta);
            sendFeedbackForSub(p, "clear");
        } catch (Throwable t) {
            t.printStackTrace();
            sendFailFeedbackForSub(p, alias, "clear");
        }
    }


    public void onFail(@NotNull CommandSender target, @NotNull String alias) {
        Util.sendMessage(target, new ComponentBuilder(getLanguageString("help-header", "", target)).create());
        for (String sub : subCommands) {
            Util.sendMessage(target, new ComponentBuilder(
                    ChatColor.DARK_GREEN + "/" + alias + " " + this.getName() + ChatColor.GREEN + " " + sub + " "
                            + getLanguageString(sub + ".params", "", target))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/" + alias + " " + this.getName() + " " + sub + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(String.join("\n",
                                    getLanguageStringList(sub + ".description", null, target)))))
                    .create());
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return CompleteUtility.complete(args[1], subCommands);
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "slot":
                        return CompleteUtility.complete(args[2], Aliases.EQUIPMENT_SLOTS);
                    case "swappable":
                    case "dispensable":
                    case "damageonhurt":
                    case "equiponinteract":
                    case "canshear":
                        return CompleteUtility.complete(args[2], Aliases.BOOLEAN);
                    case "equipsound":
                    case "shearsound":
                        return CompleteUtility.complete(args[2], Aliases.SOUND);
                    case "allowedentities":
                        List<String> res = CompleteUtility.complete(args[2], Aliases.ENTITY_TYPE, EntityType::isAlive);
                        res.addAll(CompleteUtility.complete(args[2], Aliases.ENTITY_GROUPS,
                                tag -> tag.getValues().stream().anyMatch(EntityType::isAlive)));
                        return res;
                    case "cameraoverlay":
                        return CompleteUtility.complete(args[2], "minecraft:misc/pumpkinblur");
                }
            default:
                if (args[1].equalsIgnoreCase("allowedentities")) {
                    List<String> res = CompleteUtility.complete(args[args.length - 1], Aliases.ENTITY_TYPE, EntityType::isAlive);
                    res.addAll(CompleteUtility.complete(args[args.length - 1], Aliases.ENTITY_GROUPS,
                            tag -> tag.getValues().stream().anyMatch(EntityType::isAlive)));
                    return res;
                }
        }
        return Collections.emptyList();
    }
}
