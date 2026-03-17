package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.implementations.*;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddEffect extends SubCmd {


    public AddEffect(ItemFoodCommand itemFoodCommand) {
        super("addeffect", itemFoodCommand, true, true);
    }

    //addeffect cleareffects
    //addeffect teleport <range>
    //addeffect applyeffects [chance] <potion> <lv> <duration> [ambient] [particle] [icon]
    //addeffect removeeffects <potion>
    //addeffect playsound <sound>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemBuilder builder = new ItemBuilder(getItemInHand(player));
        try {
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "cleareffects" -> cleareffects(player, builder, alias, args);
                case "teleport" -> teleport(player, builder, alias, args);
                case "applyeffects" -> addaffect(player, builder, alias, args);
                case "removeeffect" -> removeeffect(player, builder, alias, args);
                case "playsound" -> playsound(player, builder, alias, args);
                default -> onFail(player, alias);
            }
        } catch (Exception e) {
            onSubFail(player, alias, args[1].toLowerCase(Locale.ENGLISH));
            Util.logCommandError(this.getCommand(), args, sender);
        }
        updateView(player);
    }

    private void playsound(@NotNull Player player, ItemBuilder item, @NotNull String alias, String[] args) {
        Sound sound = Aliases.SOUND.convertAlias(args[2]);
        if (sound == null) {
            onWrongAlias(player,Aliases.SOUND);
            onSubFail(player, alias, "playsound");
            return;
        }
        PlaySound consumableEffect = new PlaySound(sound);
        item.addConsumeEffect(consumableEffect).build();
        onSubSuccess(player, "playsound");
    }

    private void removeeffect(@NotNull Player player, ItemBuilder item, @NotNull String alias, String[] args) {
        List<PotionEffectType> effects = new ArrayList<>();
        for (String effect : Arrays.copyOfRange(args, 2, args.length)) {
            effects.add(Aliases.POTION_EFFECT.convertAlias(effect));
        }
        RemoveEffects removeEffects = new RemoveEffects(effects);
        item.addConsumeEffect(removeEffects).build();
        onSubSuccess(player, "removeeffect");
    }

    private void addaffect(@NotNull Player player, ItemBuilder item, @NotNull String alias, String[] args) {
        List<PotionEffect> effects = new ArrayList<>();
        double chance = 1;
        int chanceIndexMod = 1;
        try {
            Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            chanceIndexMod = 0;
        }
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;
        boolean ambient = false;
        boolean particles = false;
        boolean icon = false;
        for (int i = 2 + chanceIndexMod; i < args.length; i++) {
            String argument = args[i];
            switch ((i - 2 - chanceIndexMod) % 6) {
                case 0 -> {
                    if (type != null) {
                        PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
                        effects.add(effect);
                        duration = 0;
                        amplifier = 0;
                        ambient = false;
                        particles = false;
                        icon = false;
                    }
                    type = Aliases.POTION_EFFECT.convertAlias(argument);
                }
                case 1 -> duration = Integer.parseInt(argument);
                case 2 -> amplifier = Integer.parseInt(argument);
                case 3 -> ambient = Aliases.BOOLEAN.convertAlias(argument);
                case 4 -> particles = Aliases.BOOLEAN.convertAlias(argument);
                case 5 -> icon = Aliases.BOOLEAN.convertAlias(argument);
            }
        }
        if (type != null) {
            PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
            effects.add(effect);
        }
        ApplyEffects applyEffects = new ApplyEffects(effects, (float) chance);
        item.addConsumeEffect(applyEffects).build();
        onSubSuccess(player, "addaffect");
    }

    private void teleport(@NotNull Player player, ItemBuilder item, @NotNull String alias, String[] args) {
        float val = Float.parseFloat(args[1]);
        item.addConsumeEffect(new TeleportRandomly(val)).build();
        onSubSuccess(player, "teleport");
    }

    private void cleareffects(@NotNull Player player, ItemBuilder item, @NotNull String alias, String[] args) {
        item.addConsumeEffect(new ClearEffects()).build();
        onSubSuccess(player, "cleareffects");
    }

    //addeffect cleareffects
    //addeffect teleport <range>
    //addeffect applyeffects <chance> <potion> <lv> <duration> [ambient] [particle] [icon]
    //addeffect removeeffects <potion>
    //addeffect playsound <sound>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> List.of();
            case 2 -> CompleteUtility.complete(args[1],
                    "cleareffects", "teleport", "applyeffects", "removeeffects", "playsound");
            case 3 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "teleport" -> CompleteUtility.complete(args[2], "1", "10", "20");
                case "applyeffects" -> CompleteUtility.complete(args[2], "1", "0.5", "0.1");
                case "playsound" -> CompleteUtility.complete(args[2], Aliases.SOUND);
                case "removeeffects" -> CompleteUtility.complete(args[2], Aliases.POTION_EFFECT);
                default -> List.of();
            };
            default -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "applyeffects" -> switch ((args.length - 3) % 6) {
                    case 0 -> CompleteUtility.complete(args[args.length - 1], Aliases.POTION_EFFECT);
                    case 1 -> CompleteUtility.complete(args[args.length - 1], "1", "2", "3");
                    case 2 -> CompleteUtility.complete(args[args.length - 1],
                            "infinite", "instant", "∞", "90", "180", "480");
                    default -> CompleteUtility.complete(args[args.length - 1], Aliases.BOOLEAN);
                };
                case "removeeffects" -> CompleteUtility.complete(args[1], Aliases.POTION_EFFECT);
                default -> List.of();
            };
        };
    }
}
