package emanondev.itemedit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class CooldownAPI {
	
	private YMLConfig conf;
	
	CooldownAPI(APlugin plugin){
		long now = System.currentTimeMillis();
		conf = plugin.getConfig("cooldownData.yml");
		for (String id :conf.getKeys("users")) {
			HashMap<String,Long> map = new HashMap<>();
			cooldowns.put(UUID.fromString(id),map);
			for (String cooldownId:conf.getKeys("users."+id))
				try {
					long value = conf.getLong("users."+id+"."+cooldownId,0L);
					if (value>now)
						map.put(cooldownId, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	void save(){
		long now = System.currentTimeMillis();
		conf.set("users",null);
		for (UUID player : cooldowns.keySet()) {
			HashMap<String,Long> values = cooldowns.get(player);
			for (String id:values.keySet())
				if (values.get(id)>now)
					conf.getLong("users."+player.toString()+"."+id,values.get(id));
		}
		conf.save();
	}
	private HashMap<UUID,HashMap<String,Long>> cooldowns = new HashMap<>();

	public void setCooldown(UUID player,String cooldownId,long duration) {
		if (duration<=0 && cooldowns.containsKey(player))
			cooldowns.get(player).remove(cooldownId);
		else {
			if (cooldowns.get(player)==null)
				cooldowns.put(player, new HashMap<>());
			cooldowns.get(player).put(cooldownId, System.currentTimeMillis()+duration);
		}
	}
	public void addCooldown(UUID player,String cooldownId,long duration) {
		if (duration<0)
			throw new IllegalArgumentException();
		setCooldown(player,cooldownId,getCooldownMillis(player,cooldownId)+duration);
	}
	public void reduceCooldown(UUID player,String cooldownId,long duration) {
		if (duration<0)
			throw new IllegalArgumentException();
		setCooldown(player,cooldownId,getCooldownMillis(player,cooldownId)-duration);
	}
	public void setCooldownSeconds(UUID player,String cooldownId,long duration) {
		setCooldown(player,cooldownId,duration*1000);
	}
	public void addCooldownSeconds(UUID player,String cooldownId,long duration) {
		addCooldown(player,cooldownId,duration*1000);
	}
	public void reduceCooldownSeconds(UUID player,String cooldownId,long duration) {
		reduceCooldown(player,cooldownId,duration*1000);
	}
	
	public void removeCooldown(UUID player,String cooldownId) {
		if (cooldowns.get(player)!=null)
			cooldowns.get(player).remove(cooldownId);
	}
	public boolean hasCooldown(UUID player,String cooldownId) {
		return getCooldownMillis(player,cooldownId)>System.currentTimeMillis();
			
	}
	public long getCooldownMillis(UUID player,String cooldownId) {
		return cooldowns.containsKey(player)?(cooldowns.get(player).containsKey(cooldownId)?
				cooldowns.get(player).get(cooldownId):0L):0L;
	}
	public long getCooldownSeconds(UUID player,String cooldownId) {
		return getCooldownMillis(player,cooldownId)/1000;
	}
	public long getCooldownMinutes(UUID player,String cooldownId) {
		return getCooldownMillis(player,cooldownId)/60000;
	}
	public long getCooldownHours(UUID player,String cooldownId) {
		return getCooldownMillis(player,cooldownId)/3600000;
	}
	public void setCooldown(OfflinePlayer player,String cooldownId,long duration) {
		setCooldown(player.getUniqueId(),cooldownId,duration);
	}
	public void addCooldown(OfflinePlayer player,String cooldownId,long duration) {
		addCooldown(player.getUniqueId(), cooldownId, duration);
	}
	public void reduceCooldown(OfflinePlayer player,String cooldownId,long duration) {
		reduceCooldown(player.getUniqueId(), cooldownId, duration);
	}
	public void setCooldownSeconds(OfflinePlayer player,String cooldownId,long duration) {
		setCooldownSeconds(player.getUniqueId(),cooldownId,duration);
	}
	public void addCooldownSeconds(OfflinePlayer player,String cooldownId,long duration) {
		addCooldownSeconds(player.getUniqueId(),cooldownId,duration);
	}
	public void reduceCooldownSeconds(OfflinePlayer player,String cooldownId,long duration) {
		reduceCooldownSeconds(player.getUniqueId(),cooldownId,duration);
	}
	public void removeCooldown(OfflinePlayer player,String cooldownId) {
		removeCooldown(player.getUniqueId(),cooldownId);
	}
	public boolean hasCooldown(OfflinePlayer player,String cooldownId) {
		return hasCooldown(player.getUniqueId(), cooldownId);
	}
	public long getCooldownMillis(OfflinePlayer player,String cooldownId) {
		return getCooldownMillis(player.getUniqueId(), cooldownId);
	}
	public long getCooldownSeconds(OfflinePlayer player,String cooldownId) {
		return getCooldownSeconds(player.getUniqueId(), cooldownId);
	}
	public long getCooldownMinutes(OfflinePlayer player,String cooldownId) {
		return getCooldownMinutes(player.getUniqueId(), cooldownId);
	}
	public long getCooldownHours(OfflinePlayer player,String cooldownId) {
		return getCooldownHours(player.getUniqueId(), cooldownId);
	}
}
