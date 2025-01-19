package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Drop extends SubCmd {

    public Drop(ServerItemCommand cmd) {
        super("drop", cmd, false, false);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        try {
            // <id> <amount> <world> <x> <y> <z>
            if (args.length != 7) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int amount = Integer.parseInt(args[2]);
            if (amount < 1 || amount > 2304)
                throw new IllegalArgumentException("Wrong amount number");

            ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
            World world = Bukkit.getWorld(args[3]);

            int stackSize = item.getMaxStackSize();
            Location loc = new Location(world, Double.parseDouble(args[4]), Double.parseDouble(args[5]),
                    Double.parseDouble(args[6]));

            SchedulerUtils.run(getPlugin(), loc, () -> {
                int toGive = amount;
                while (toGive > 0) {
                    item.setAmount(Math.min(toGive, stackSize));
                    world.dropItem(loc, item.clone());
                    toGive -= Math.min(toGive, stackSize);
                }
            });

            if (ItemEdit.get().getConfig().loadBoolean("log.action.drop", true)) {
                String msg = UtilsString.fix(this.getConfigString("log"), null, true, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%world%", world.getName(), "%x%", args[4], "%y%", args[5], "%z%",
                        args[6]);
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true))
                    Util.sendMessage(Bukkit.getConsoleSender(), msg);
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true))
                    Util.logToFile(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        switch (args.length) {
            case 2:
                return Util.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3:
                return Util.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4: {
                List<String> l = new ArrayList<>();
                for (World w : Bukkit.getWorlds())
                    l.add(w.getName());
                return Util.complete(args[3], l);
            }
            case 5: {
                Location loc = ((Player) sender).getLocation();
                return Util.complete(args[4], Arrays.asList(String.valueOf(loc.getBlockX()), String.valueOf(loc.getX())));
            }
            case 6: {
                Location loc = ((Player) sender).getLocation();
                return Util.complete(args[5], Arrays.asList(String.valueOf(loc.getBlockY()), String.valueOf(loc.getY())));
            }
            case 7: {
                Location loc = ((Player) sender).getLocation();
                return Util.complete(args[6], Arrays.asList(String.valueOf(loc.getBlockZ()), String.valueOf(loc.getZ())));
            }
            default:
                return Collections.emptyList();
        }
    }

}
