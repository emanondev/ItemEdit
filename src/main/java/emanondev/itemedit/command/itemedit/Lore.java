package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Lore extends SubCmd {

    private final Map<UUID, List<String>> copies = new HashMap<>();
    private final YMLConfig loreCopy = ItemEdit.get().getConfig("loreCopy");
    private int lineLimit;
    private int lengthLimit;

    private static final String[] loreSub = new String[]{"add", "set", "remove", "reset", "insert", "copy",
            "copybook", "copyfile", "paste", "replace"};

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

    private boolean allowedLineLimit(Player who, int lines) {
        if (lineLimit < 0 || who.hasPermission("itemedit.bypass.lore_line_limit"))
            return true;
        return lines <= lineLimit;
    }

    private boolean allowedLengthLimit(Player who, String text) {
        if (lengthLimit < 0 || who.hasPermission("itemedit.bypass.lore_length_limit"))
            return true;
        return text.length() <= lengthLimit;
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "set":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreSet(p, item, args);
                return;
            case "add":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreAdd(p, item, args);
                return;
            case "insert":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreInsert(p, item, args);
                return;
            case "reset":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreReset(p, item, args);
                return;
            case "remove":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreRemove(p, item, args);
                return;
            case "copy":
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                loreCopy(p, item, args);
                return;
            case "copybook":
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                loreCopyBook(p, item, args);
                return;
            case "copyfile":
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                loreCopyFile(p, item, args);
                return;
            case "paste":
                if (!sender.hasPermission(getPermission() + ".copy")) {
                    getCommand().sendPermissionLackMessage(getPermission() + ".copy", sender);
                    return;
                }
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                lorePaste(p, item, args);
                return;
            case "replace":
                if (!Util.isAllowedChangeLore(sender, item.getType()))
                    return;
                loreReplace(p, item, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    private void loreReplace(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 4) {
                p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("replace.params", null, p),
                        getLanguageStringList("replace.description", null, p)));
                return;
            }
            if (!item.hasItemMeta())
                return;
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasLore())
                return;
            List<String> lore = meta.getLore();
            String from;
            String to;
            if (args.length == 4) {
                from = args[2];
                to = args[3];
            } else {
                StringBuilder raw = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    raw.append(" ").append(args[i]);
                String rawText = raw.substring(1);
                int i1 = rawText.indexOf("{");
                if (i1 != 0) {
                    replaceBadFormat(p, args);
                    return;
                }
                int i2 = rawText.indexOf("}", i1);
                if (i2 == -1) {
                    replaceBadFormat(p, args);
                    return;
                }
                int i3 = rawText.indexOf("{", i2);
                if (i3 == -1 || i2 + 2 != i3) {
                    replaceBadFormat(p, args);
                    return;
                }
                int i4 = rawText.indexOf("}", i3);
                if (i4 != rawText.length() - 1) {
                    replaceBadFormat(p, args);
                    return;
                }
                from = rawText.substring(1, i2);
                to = rawText.substring(i3 + 1, i4);
            }
            from = UtilsString.fix(from, null, true);
            to = UtilsString.fix(to, null, true);

            for (String s : lore) {
                String text = s.replace(from, to);
                if (!allowedLengthLimit(p, ChatColor.stripColor(text))) {
                    Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-length-limit",
                            "", null, true, "%limit%", String.valueOf(lengthLimit)));
                    return;
                }
            }

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, lore.get(i).replace(from, to));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("replace.params", null, p),
                    getLanguageStringList("replace.description", null, p)));
        }
    }

    private void replaceBadFormat(Player p, String[] args) {
        Util.sendMessage(p, this.craftFailFeedback(getLanguageString("replace.params", null, p),
                getLanguageStringList("replace.description", null, p)));
    }

    private void lorePaste(Player p, ItemStack item, String[] args) {
        if (!copies.containsKey(p.getUniqueId())) {
            Util.sendMessage(p, this.getLanguageString("paste.no-copy", null, p));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(copies.get(p.getUniqueId()));
        item.setItemMeta(meta);
        Util.sendMessage(p, this.getLanguageString("paste.feedback", null, p));
        updateView(p);
    }

    private void loreCopy(Player p, ItemStack item, String[] args) {

        List<String> lore;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasLore())
                lore = new ArrayList<>(itemMeta.getLore());
            else
                lore = new ArrayList<>();
        } else
            lore = new ArrayList<>();

        copies.put(p.getUniqueId(), lore);
        Util.sendMessage(p, this.getLanguageString("copy.feedback", null, p));
    }

    private void loreCopyBook(Player p, ItemStack item, String[] args) {

        List<String> lore;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (!(itemMeta instanceof BookMeta)) {
                Util.sendMessage(p, this.getLanguageString("copyBook.wrong-type", null, p));
                return;
            }
            BookMeta meta = (BookMeta) itemMeta;
            List<String> pages = meta.getPages();
            lore = new ArrayList<>();
            if (pages != null)
                for (String page : pages) {
                    if (page == null)
                        continue;
                    lore.addAll(Arrays.asList(page.split("\n")));
                }
        } else
            lore = new ArrayList<>();
        for (int i = 0; i < lore.size(); i++)
            lore.set(i, Util.formatText(p, lore.get(i), getPermission()));
        copies.put(p.getUniqueId(), lore);
        Util.sendMessage(p, this.getLanguageString("copyBook.feedback", null, p));
    }

    private void loreCopyFile(Player p, ItemStack item, String[] args) {
        if (args.length < 2) {
            Util.sendMessage(p, this.getLanguageString("copyFile.no-path", null, p));
            return;
        }
        if (!loreCopy.contains(args[2])) {
            Util.sendMessage(p, this.getLanguageString("copyFile.wrong-path", null, p));
            return;
        }
        List<String> lore = new ArrayList<>(loreCopy.getStringList(args[2]));
        for (int i = 0; i < lore.size(); i++)
            lore.set(i, Util.formatText(p, lore.get(i), getPermission()));
        copies.put(p.getUniqueId(), lore);
        Util.sendMessage(p, this.getLanguageString("copyFile.feedback", null, p));
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Util.complete(args[1], loreSub);
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "remove":
                    case "set":
                        if (!(sender instanceof Player))
                            return Collections.emptyList();
                        Player player = (Player) sender;
                        ItemStack item = this.getItemInHand(player);
                        if (Util.isAirOrNull(item))
                            return Collections.emptyList();
                        if (!item.hasItemMeta())
                            return Util.complete(args[2], Arrays.asList("1", "last"));
                        ItemMeta meta = item.getItemMeta();
                        if (!meta.hasLore())
                            return Util.complete(args[2], Arrays.asList("1", "last"));
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < meta.getLore().size(); i++)
                            list.add(String.valueOf(i + 1));
                        list.add("last");
                        return Util.complete(args[2], list);
                    case "copyfile":
                        return Util.complete(args[2], loreCopy.getKeys(false));
                }
                return Collections.emptyList();
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "set":
                        if (sender instanceof Player) {
                            ItemStack item = this.getItemInHand((Player) sender);
                            if (item != null && item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta.hasLore()) {

                                    List<String> lore = meta.getLore();

                                    try {
                                        int line = args[2].equalsIgnoreCase("last") ?
                                                lore.size() - 1 : Integer.parseInt(args[2]) - 1;
                                        if (line < 0 || line >= lore.size())
                                            return Collections.emptyList();
                                        return Util.complete(args[3], lore.get(line).replace('§', '&'));
                                    } catch (NumberFormatException e) {
                                        return Collections.emptyList();
                                    }
                                }
                            }
                        }
                }
        }
        return Collections.emptyList();
    }

    // /itemedit lore add
    private void loreAdd(Player p, ItemStack item, String[] args) {
        StringBuilder text = new StringBuilder();
        if (args.length > 2) {
            text = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++)
                text.append(" ").append(args[i]);
            // text = ChatColor.translateAlternateColorCodes('&', text);
        }

        ItemMeta itemMeta = item.getItemMeta();

        List<String> lore;
        if (itemMeta.hasLore())
            lore = new ArrayList<>(itemMeta.getLore());
        else
            lore = new ArrayList<>();
        if (!allowedLineLimit(p, lore.size() + 1)) {
            Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-line-limit",
                    "", null, true, "%limit%", String.valueOf(lineLimit)));
            return;
        }

        String lineText = Util.formatText(p, text.toString(), getPermission());
        if (!allowedLengthLimit(p, ChatColor.stripColor(lineText))) {
            Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-length-limit",
                    "", null, true, "%limit%", String.valueOf(lengthLimit)));
            return;
        }
        if (Util.hasBannedWords(p, lineText))
            return;

        lore.add(lineText);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        updateView(p);
    }

    // /itemedit lore insert [line] [text]
    private void loreInsert(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 3)
                throw new IllegalArgumentException("Wrong param number");

            StringBuilder text = new StringBuilder();
            if (args.length > 3) {
                text = new StringBuilder(args[3]);
                for (int i = 4; i < args.length; i++)
                    text.append(" ").append(args[i]);
                // text = ChatColor.translateAlternateColorCodes('&', text);
            }

            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException("Wrong line number");
            ItemMeta itemMeta = item.getItemMeta();

            List<String> lore;
            if (itemMeta.hasLore())
                lore = new ArrayList<>(itemMeta.getLore());
            else
                lore = new ArrayList<>();
            if (!allowedLineLimit(p, Math.max(lore.size() + 1, line + 1))) {
                Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-line-limit",
                        "", null, true, "%limit%", String.valueOf(lineLimit)));
                return;
            }
            String lineText = Util.formatText(p, text.toString(), getPermission());
            if (!allowedLengthLimit(p, ChatColor.stripColor(lineText))) {
                Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-length-limit",
                        "", null, true, "%limit%", String.valueOf(lengthLimit)));
                return;
            }


            for (int i = lore.size(); i < line; i++)
                lore.add("");

            if (Util.hasBannedWords(p, lineText))
                return;

            lore.add(line, lineText);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("insert.params", null, p),
                    getLanguageStringList("insert.description", null, p)));
        }
    }

    // lore set line text
    private void loreSet(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 3)
                throw new IllegalArgumentException("Wrong param number");

            StringBuilder text = new StringBuilder();
            if (args.length > 3) {
                text = new StringBuilder(args[3]);
                for (int i = 4; i < args.length; i++)
                    text.append(" ").append(args[i]);
                // text = ChatColor.translateAlternateColorCodes('&', text);
            }
            String lineText = Util.formatText(p, text.toString(), getPermission());

            ItemMeta itemMeta = item.getItemMeta();

            List<String> lore;
            if (itemMeta.hasLore())
                lore = new ArrayList<>(itemMeta.getLore());
            else
                lore = new ArrayList<>();
            int line = args[2].equalsIgnoreCase("last") ?
                    lore.size() - 1 : Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException("Wrong line number");

            if (lore.size() <= line && !allowedLineLimit(p, line + 1)) {
                Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-line-limit",
                        "", null, true, "%limit%", String.valueOf(lineLimit)));
                return;
            }
            if (!allowedLengthLimit(p, ChatColor.stripColor(lineText))) {
                Util.sendMessage(p, ItemEdit.get().getLanguageConfig(p).loadMessage("blocked-by-lore-length-limit",
                        "", null, true, "%limit%", String.valueOf(lengthLimit)));
                return;
            }
            for (int i = lore.size(); i <= line; i++)
                lore.add("");

            if (Util.hasBannedWords(p, lineText))
                return;

            lore.set(line, lineText);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("set.params", null, p),
                    getLanguageStringList("set.description", null, p)));
        }
    }

    private void loreRemove(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 3)
                throw new IllegalArgumentException("Wrong param number");
            if (!item.hasItemMeta())
                return;
            ItemMeta itemMeta = item.getItemMeta();
            if (!itemMeta.hasLore() || itemMeta.getLore().size() == 0)
                return;
            List<String> lore = new ArrayList<>(itemMeta.getLore());
            int line;
            if (args[2].equalsIgnoreCase("last"))
                line = lore.size() - 1;
            else
                line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException("Wrong line number");

            if (lore.size() < line)
                return;

            lore.remove(line);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    private void loreReset(Player p, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(null);
        item.setItemMeta(meta);
        updateView(p);
    }
}
