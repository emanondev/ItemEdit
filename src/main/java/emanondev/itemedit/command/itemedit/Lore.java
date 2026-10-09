package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.IntParser;
import emanondev.itemedit.utility.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lore extends SubCmd {

    private static final String[] loreSub = new String[]{"add", "set", "remove", "reset", "insert", "copy",
            "copybook", "copyfile", "paste", "replace"};
    private final Map<UUID, List<Component>> copies = new HashMap<>();
    private final YMLConfig loreCopy = ItemEdit.get().getConfig("loreCopy");
    private int lineLimit;
    private int lengthLimit;

    public Lore(ItemEditCommand cmd) {
        super("lore", cmd, true, true);
        lineLimit = getPlugin().getConfig().getInt("blocked.lore-line-limit", 16);
        lengthLimit = getPlugin().getConfig().getInt("blocked.lore-length-limit", 120);
    }

    public void reload() {
        super.reload();
        lineLimit = getPlugin().getConfig().getInt("blocked.lore-line-limit", 16);
        lengthLimit = getPlugin().getConfig().getInt("blocked.lore-length-limit", 120);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = getItemInMainHand(p);

        if (args.length < 2) {
            onFail(p, alias);
            return;
        }

        String subCommand = args[1].toLowerCase(Locale.ENGLISH);

        // Commands that require lore modification permission
        switch (subCommand) {
            case "set", "add", "insert", "reset", "remove", "replace" -> {
                if (!Util.isAllowedChangeLore(sender, item.getType())) return;
                switch (subCommand) {
                    case "set" -> loreSet(p, item, alias, args);
                    case "add" -> loreAdd(p, item, alias, args);
                    case "insert" -> loreInsert(p, item, alias, args);
                    case "reset" -> loreReset(p, item, alias, args);
                    case "remove" -> loreRemove(p, item, alias, args);
                    case "replace" -> loreReplace(p, item, alias, args);
                }
            }
            // Commands that require copy permission
            case "copy", "copybook", "copyfile" -> {
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                switch (subCommand) {
                    case "copy" -> loreCopy(p, item, alias, args);
                    case "copybook" -> loreCopyBook(p, item, alias, args);
                    case "copyfile" -> loreCopyFile(p, item, alias, args);
                }
            }
            // Paste requires both copy permission AND lore modification
            case "paste" -> {
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                if (!Util.isAllowedChangeLore(sender, item.getType())) return;

                lorePaste(p, item, alias, args);
            }
            default -> onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], loreSub);
            case 3 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "remove", "set" -> {
                    if (!(sender instanceof Player player)) yield List.of();

                    ItemStack item = getItemInMainHand(player);
                    if (ItemUtils.isAirOrNull(item)) yield List.of();

                    ItemMeta meta = ItemUtils.getMeta(item);
                    if (!item.hasItemMeta() || !meta.hasLore()) {
                        yield CompleteUtility.complete(args[2], Arrays.asList("1", "last"));
                    }
                    List<String> loreIndices = IntStream.range(0, meta.lore().size())
                            .mapToObj(i -> String.valueOf(i + 1))
                            .collect(Collectors.toList());
                    loreIndices.add("last");
                    yield CompleteUtility.complete(args[2], loreIndices);
                }
                case "copyfile" -> CompleteUtility.complete(args[2], loreCopy.getKeys(false));
                default -> List.of();
            };
            case 4 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "set" -> {
                    if (!(sender instanceof Player player)) yield List.of();

                    ItemStack item = getItemInMainHand(player);
                    if (item == null || !item.hasItemMeta()) yield List.of();

                    ItemMeta meta = ItemUtils.getMeta(item);
                    if (!meta.hasLore()) yield List.of();

                    List<Component> lore = meta.lore();
                    int line;
                    try {
                        line = args[2].equalsIgnoreCase("last") ? lore.size() - 1 : Integer.parseInt(args[2]) - 1;
                    } catch (NumberFormatException e) {
                        yield List.of();
                    }

                    if (line < 0 || line >= lore.size()) yield List.of();

                    yield CompleteUtility.complete(args[3], Util.serializeItemTemplate(lore.get(line)));
                }
                default -> List.of();
            };
            default -> List.of();
        };
    }

    private boolean allowedLineLimit(Player who, int lines) {
        if (lineLimit < 0 || who.hasPermission("itemedit.bypass.lore_line_limit")) {
            return true;
        }
        return lines <= lineLimit;
    }

    private boolean allowedLengthLimit(Player who, String text) {
        if (lengthLimit < 0 || who.hasPermission("itemedit.bypass.lore_length_limit")) {
            return true;
        }
        return text.length() <= lengthLimit;
    }

    private void loreReplace(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 4) {
                onSubFail(p, alias, "replace");
                return;
            }
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            if (!meta.hasLore()) {
                return;
            }
            List<Component> lore = meta.lore();
            String from;
            String to;
            if (args.length == 4) {
                from = args[2];
                to = args[3];
            } else {
                StringBuilder raw = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    raw.append(" ").append(args[i]);
                }
                String rawText = raw.substring(1);
                int i1 = rawText.indexOf("{");
                if (i1 != 0) {
                    onSubFail(p, alias, "replace");
                    return;
                }
                int i2 = rawText.indexOf("}", i1);
                if (i2 == -1) {
                    onSubFail(p, alias, "replace");
                    return;
                }
                int i3 = rawText.indexOf("{", i2);
                if (i3 == -1 || i2 + 2 != i3) {
                    onSubFail(p, alias, "replace");
                    return;
                }
                int i4 = rawText.indexOf("}", i3);
                if (i4 != rawText.length() - 1) {
                    onSubFail(p, alias, "replace");
                    return;
                }
                from = rawText.substring(1, i2);
                to = rawText.substring(i3 + 1, i4);
            }
            Component fromComponent = Util.formatComponent(p, from, getPermission());
            String fromText = PlainTextComponentSerializer.plainText().serialize(fromComponent);
            Component replacement = Util.formatItemTemplate(p, to, getPermission());
            if (fromText.isEmpty()) {
                onSubFail(p, alias, "replace");
                return;
            }

            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                    .matchLiteral(fromText)
                    .replacement(replacement)
                    .build();
            String replacementPlainText = Util.getPlainText(Util.formatComponent(p, to, getPermission()));
            List<Component> replacedLore = new ArrayList<>(lore.size());
            for (Component line : lore) {
                Component replaced = line.replaceText(replacementConfig);
                String plainText = PlainTextComponentSerializer.plainText().serialize(line)
                        .replace(fromText, replacementPlainText);
                if (!allowedLengthLimit(p, plainText)) {
                    getPlugin().getTranslator().send(p, "blocked-by-lore-length-limit",
                            "%limit%", String.valueOf(lengthLimit));
                    return;
                }
                replacedLore.add(replaced);
            }

            meta.lore(replacedLore);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "replace");
        }
    }

    private void lorePaste(Player p, ItemStack item, String alias, String[] args) {
        if (!copies.containsKey(p.getUniqueId())) {
            Util.sendMessage2(p, this.translate("paste.no-copy", p));
            return;
        }
        ItemMeta meta = ItemUtils.getMeta(item);
        meta.lore(copies.get(p.getUniqueId()).stream()
                .map(line -> Util.formatText(p, line, getPermission()))
                .toList());
        item.setItemMeta(meta);
        Util.sendMessage2(p, this.translate("paste.feedback", p));
        updateView(p);
    }

    private void loreCopy(Player p, ItemStack item, String alias, String[] args) {
        List<Component> lore;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = ItemUtils.getMeta(item);
            if (itemMeta.hasLore()) {
                lore = new ArrayList<>(itemMeta.lore());
            } else {
                lore = new ArrayList<>();
            }
        } else
            lore = new ArrayList<>();

        copies.put(p.getUniqueId(), lore);
        Util.sendMessage2(p, this.translate("copy.feedback", p));
    }

    private void loreCopyBook(Player p, ItemStack item, String alias, String[] args) {

        List<Component> lore;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = ItemUtils.getMeta(item);
            if (!(itemMeta instanceof BookMeta meta)) {
                getPlugin().getTranslator().send(p, "generic.wrong-material.writable_book");
                return;
            }
            List<String> pages = meta.getPages();
            lore = new ArrayList<>();
            if (pages != null)
                for (String page : pages) {
                    if (page == null) {
                        continue;
                    }
                    for (String line : page.split("\n")) {
                        lore.add(Util.formatItemTemplate(p, line, getPermission()));
                    }
                }
        } else {
            lore = new ArrayList<>();
        }
        copies.put(p.getUniqueId(), lore);
        Util.sendMessage2(p, this.translate("copyBook.feedback", p));
    }

    private void loreCopyFile(Player p, ItemStack item, String alias, String[] args) {
        if (args.length < 2) {
            Util.sendMessage2(p, this.translate("copyFile.no-path", p));
            return;
        }
        if (!loreCopy.contains(args[2])) {
            Util.sendMessage2(p, this.translate("copyFile.wrong-path", p));
            return;
        }
        List<Component> lore = loreCopy.getStringList(args[2]).stream()
                .map(text -> Util.formatItemTemplate(p, text, getPermission()))
                .toList();
        copies.put(p.getUniqueId(), lore);
        Util.sendMessage2(p, this.translate("copyFile.feedback", p));
    }

    // /itemedit lore add
    private void loreAdd(Player p, ItemStack item, String alias, String[] args) {
        StringBuilder text = new StringBuilder();
        if (args.length > 2) {
            text = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++)
                text.append(" ").append(args[i]);
            // text = ChatColor.translateAlternateColorCodes('&', text);
        }

        ItemMeta itemMeta = ItemUtils.getMeta(item);

        List<Component> lore;
        if (itemMeta.hasLore()) {
            lore = new ArrayList<>(itemMeta.lore());
        } else {
            lore = new ArrayList<>();
        }
        if (!allowedLineLimit(p, lore.size() + 1)) {
            getPlugin().getTranslator().send(p, "blocked-by-lore-line-limit",
                    "%limit%", String.valueOf(lineLimit));
            return;
        }

        Component lineText = Util.formatItemTemplate(p, text.toString(), getPermission());
        String plainText = Util.getPlainTextItemTemplate(p, lineText, getPermission());
        if (!allowedLengthLimit(p, plainText)) {
            getPlugin().getTranslator().send(p, "blocked-by-lore-length-limit",
                    "%limit%", String.valueOf(lengthLimit));
            return;
        }
        if (Util.checkBannedWords(p, plainText)) {
            return;
        }

        lore.add(lineText);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        updateView(p);
    }

    // /itemedit lore insert [line] [text]
    private void loreInsert(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 3) {
                onSubFail(p, alias, "insert");
                return;
            }

            StringBuilder text = new StringBuilder();
            if (args.length > 3) {
                text = new StringBuilder(args[3]);
                for (int i = 4; i < args.length; i++) {
                    text.append(" ").append(args[i]);
                }
                // text = ChatColor.translateAlternateColorCodes('&', text);
            }

            IntParser line = new IntParser(args[2], -1);
            if (!line.isNumberMin(0)) {
                onSubFail(p, alias, "insert");
                return;
            }
            ItemMeta itemMeta = ItemUtils.getMeta(item);

            List<Component> lore;
            if (itemMeta.hasLore()) {
                lore = new ArrayList<>(itemMeta.lore());
            } else {
                lore = new ArrayList<>();
            }
            if (!allowedLineLimit(p, Math.max(lore.size() + 1, line.getValue() + 1))) {
                getPlugin().getTranslator().send(p, "blocked-by-lore-line-limit",
                        "%limit%", String.valueOf(lineLimit));
                return;
            }
            Component lineText = Util.formatItemTemplate(p, text.toString(), getPermission());
            String plainText = Util.getPlainTextItemTemplate(p, lineText, getPermission());
            if (!allowedLengthLimit(p, plainText)) {
                getPlugin().getTranslator().send(p, "blocked-by-lore-length-limit",
                        "%limit%", String.valueOf(lengthLimit));
                return;
            }


            for (int i = lore.size(); i < line.getValue(); i++) {
                lore.add(Component.empty());
            }

            if (Util.checkBannedWords(p, plainText)) {
                return;
            }

            lore.add(line.getValue(), lineText);
            itemMeta.lore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "insert");
        }
    }

    // lore set line text
    private void loreSet(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 3) {
                onSubFail(p, alias, "set");
                return;
            }

            StringBuilder text = new StringBuilder();
            if (args.length > 3) {
                text = new StringBuilder(args[3]);
                for (int i = 4; i < args.length; i++) {
                    text.append(" ").append(args[i]);
                }
                // text = ChatColor.translateAlternateColorCodes('&', text);
            }
            Component lineText = Util.formatItemTemplate(p, text.toString(), getPermission());
            String plainText = Util.getPlainTextItemTemplate(p, lineText, getPermission());

            ItemMeta itemMeta = ItemUtils.getMeta(item);

            List<Component> lore;
            if (itemMeta.hasLore()) {
                lore = new ArrayList<>(itemMeta.lore());
            } else {
                lore = new ArrayList<>();
            }
            IntParser line = args[2].equalsIgnoreCase("last") ?
                    new IntParser(lore.size() - 1) :
                    new IntParser(args[2], -1);
            if (!line.isNumberMin(0)) {
                onSubFail(p, alias, "set");
                return;
            }

            if (lore.size() <= line.getValue() && !allowedLineLimit(p, line.getValue() + 1)) {
                getPlugin().getTranslator().send(p, "blocked-by-lore-line-limit",
                        "%limit%", String.valueOf(lineLimit));
                return;
            }
            if (!allowedLengthLimit(p, plainText)) {
                getPlugin().getTranslator().send(p, "blocked-by-lore-length-limit",
                        "%limit%", String.valueOf(lengthLimit));
                return;
            }
            for (int i = lore.size(); i <= line.getValue(); i++) {
                lore.add(Component.empty());
            }

            if (Util.checkBannedWords(p, plainText)) {
                return;
            }

            lore.set(line.getValue(), lineText);
            itemMeta.lore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "set");
        }
    }

    private void loreRemove(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta itemMeta = ItemUtils.getMeta(item);
            if (!itemMeta.hasLore() || itemMeta.lore().isEmpty()) {
                return;
            }
            List<Component> lore = new ArrayList<>(itemMeta.lore());
            IntParser line = args[2].equalsIgnoreCase("last") ?
                    new IntParser(lore.size() - 1) :
                    new IntParser(args[2], -1);
            if (!line.isNumberMin(0)) {
                onSubFail(p, alias, "remove");
                return;
            }
            if (lore.size() <= line.getValue()) {
                return;
            }

            lore.remove(line.getValue());
            itemMeta.lore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "remove");
        }
    }

    private void loreReset(Player p, ItemStack item, String alias, String[] args) {
        ItemMeta meta = ItemUtils.getMeta(item);
        meta.lore(null);
        item.setItemMeta(meta);
        updateView(p);
    }
}
