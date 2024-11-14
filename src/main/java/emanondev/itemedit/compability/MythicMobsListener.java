package emanondev.itemedit.compability;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsInventory;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.item.ItemComponentBukkitItemStack;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MythicMobsListener implements Listener {

    @EventHandler
    private void event(MythicDropLoadEvent event) {
        if (!event.getDropName().equalsIgnoreCase("serveritem"))
            return;
        try {
            event.register(new ServerItemDrop(event.getConfig()));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @EventHandler
    private void event(MythicMechanicLoadEvent event) {
        switch (event.getMechanicName().toLowerCase(Locale.ENGLISH)) {
            case "dropserveritem":
                try {
                    event.register(new DropServerItemMechanic(event.getConfig()));
                } catch (IllegalArgumentException ignored) {
                }
            case "giveserveritem":
                try {
                    event.register(new GiveServerItemMechanic(event.getConfig()));
                } catch (IllegalArgumentException ignored) {
                }
        }
    }
}

class ServerItemDrop implements IItemDrop, ILocationDrop {
    private final String id;
    private final int amount;

    public ServerItemDrop(MythicLineConfig config) {
        String value = config.getString(new String[]{"name", "id", "serveritem", "type"}, null);
        if (value == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fMissing item id on mechanic, use {&eid&f='<your_id>' ....}");
            throw new IllegalArgumentException();
        }
        if (ItemEdit.get().getServerStorage().getItem(value) == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + value + "' is not a registered serveritem");
            throw new IllegalArgumentException();
        }

        int amount = config.getInteger(new String[]{"a", "amount"}, 1);
        if (amount < 1) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid amount, should be from [1 to +inf[");
            throw new IllegalArgumentException();
        }
        this.id = value;
        this.amount = amount;
    }


    @Override
    public void drop(AbstractLocation abstractLocation, DropMetadata dropMetadata, double v) {
        ItemStack item = getItem(getPlayer(dropMetadata), v);
        if (item == null)
            return;
        Location l = BukkitAdapter.adapt(abstractLocation);
        l.getWorld().dropItem(l, item);
    }

    @Override
    public AbstractItemStack getDrop(DropMetadata dropMetadata, double v) {
        try {
            ItemStack item = getItem(getPlayer(dropMetadata), v);
            if (item == null)
                return null;
            return new ItemComponentBukkitItemStack(item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player getPlayer(DropMetadata data) {
        if (data.getCause().isPresent()) {
            Entity e = BukkitAdapter.adapt(data.getCause().get());
            if (e instanceof Player)
                return (Player) e;
        }
        if (data.getCaster() != null && data.getCaster().getEntity() != null) {
            Entity e = BukkitAdapter.adapt(data.getCaster().getEntity());
            if (e instanceof Player)
                return (Player) e;
        }
        if (data.getDropper().isPresent() && data.getDropper().get().getEntity() != null) {
            Entity e = BukkitAdapter.adapt(data.getDropper().get().getEntity());
            if (e instanceof Player)
                return (Player) e;
        }
        return null;
    }


    public ItemStack getItem(@Nullable Player p, double amountMultiplier) {
        ItemStack item = ItemEdit.get().getServerStorage().getItem(id, p);
        if (item == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + id + "' is not a registered serveritem");
            return null;
        }
        int a = (int) (this.amount * amountMultiplier);
        if (a <= 0)
            return null;
        item.setAmount(a);
        return item;
    }
}

class DropServerItemMechanic implements ISkillMechanic, ITargetedEntitySkill, ITargetedLocationSkill {
    private final String id;
    private final int amount;
    private final int diff;
    private final double chance;

    public DropServerItemMechanic(MythicLineConfig mlc) {
        id = mlc.getString(new String[]{"id", "name", "type", "serveritem",}, null);
        if (id == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fMissing item id on mechanic, use {&eid&f='<your_id>' ....}");
            throw new IllegalArgumentException();
        }
        if (ItemEdit.get().getServerStorage().getItem(id) == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + id + "' is not a registered serveritem");
            throw new IllegalArgumentException();
        }
        this.amount = mlc.getInteger(new String[]{"amount", "a"}, 1);
        this.diff = mlc.getInteger(new String[]{"amountmax", "amount-max", "amax"}, this.amount) - this.amount;
        this.chance = mlc.getDouble(new String[]{"chance", "c"}, 1D);//probabilità 0 = nulla 1 = 100%
        if (chance <= 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid chance, should be from ]0 to 1]");
            throw new IllegalArgumentException();
        }
        if (amount <= 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid amount, should be from [1 to +inf[");
            throw new IllegalArgumentException();
        }
        if (diff < 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid max amount, should be from [amount to +inf[");
            throw new IllegalArgumentException();
        }
    }

    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (Math.random() > chance)
            return SkillResult.CONDITION_FAILED;
        if (Bukkit.isPrimaryThread())
            drop(target.getLocation());
        else
            new BukkitRunnable() {
                public void run() {
                    drop(target.getLocation());
                }
            }.runTask(ItemEdit.get());
        return SkillResult.SUCCESS;
    }

    public void drop(AbstractLocation location) {
        Location loc = BukkitAdapter.adapt(location);
        ItemStack item = ItemEdit.get().getServerStorage().getItem(id);
        if (item == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + id + "' is not a registered serveritem");
            return;
        }
        item.setAmount((int) (amount + Math.random() * diff));
        loc.getWorld().dropItem(loc, item);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation location) {
        if (Math.random() > chance)
            return SkillResult.CONDITION_FAILED;
        if (Bukkit.isPrimaryThread())
            drop(location);
        else
            new BukkitRunnable() {
                public void run() {
                    drop(location);
                }
            }.runTask(ItemEdit.get());
        return SkillResult.SUCCESS;
    }
}


class GiveServerItemMechanic implements ISkillMechanic, ITargetedEntitySkill {
    private final String id;
    private final int amount;
    private final int diff;
    private final double chance;

    public GiveServerItemMechanic(MythicLineConfig mlc) {
        id = mlc.getString(new String[]{"id", "name", "type", "serveritem",}, null);
        if (id == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fMissing item id on mechanic, use {&eid&f='<your_id>' ....}");
            throw new IllegalArgumentException();
        }
        if (ItemEdit.get().getServerStorage().getItem(id) == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + id + "' is not a registered serveritem");
            throw new IllegalArgumentException();
        }
        this.amount = mlc.getInteger(new String[]{"amount", "a"}, 1);
        this.diff = mlc.getInteger(new String[]{"amountmax", "amount-max", "amax"}, this.amount) - this.amount;
        this.chance = mlc.getDouble(new String[]{"chance", "c"}, 1D);//probabilità 0 = nulla 1 = 100%
        if (chance <= 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid chance, should be from ]0 to 1]");
            throw new IllegalArgumentException();
        }
        if (amount <= 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid amount, should be from [1 to +inf[");
            throw new IllegalArgumentException();
        }
        if (diff < 0) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid max amount, should be from [amount to +inf[");
            throw new IllegalArgumentException();
        }
    }

    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (Math.random() > chance || !(target instanceof AbstractPlayer))
            return SkillResult.CONDITION_FAILED;
        if (Bukkit.isPrimaryThread())
            give((AbstractPlayer) target);
        else
            new BukkitRunnable() {
                public void run() {
                    give((AbstractPlayer) target);
                }
            }.runTask(ItemEdit.get());
        return SkillResult.SUCCESS;
    }

    public void give(AbstractPlayer target) {
        Player player = BukkitAdapter.adapt(target);
        ItemStack item = ItemEdit.get().getServerStorage().getItem(id);
        if (item == null) {
            ItemEdit.get().log("&9[&fMythicMobs&9] &fInvalid id, '" + id + "' is not a registered serveritem");
            return;
        }
        item.setAmount((int) (amount + Math.random() * diff));
        UtilsInventory.giveAmount(player, item, amount, UtilsInventory.ExcessManage.DROP_EXCESS);
    }
}
