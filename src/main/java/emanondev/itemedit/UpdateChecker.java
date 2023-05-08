package emanondev.itemedit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class UpdateChecker {
    private final int project;
    private URL checkURL;
    private String newVersion;
    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning("§4Could not connect to Spigot, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public String getResourceUrl() {
        return "https://spigotmc.org/resources/" + project;
    }

    /**
     *
     */
    private boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);

    }

    public void logUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (checkForUpdates()) {
                    Bukkit.getConsoleSender()
                            .sendMessage("[" + plugin.getName() + "] New Update at " + getResourceUrl());

                }
            } catch (FileNotFoundException | UnknownHostException e) {
                Bukkit.getConsoleSender().sendMessage(
                        "[" + plugin.getName() + "] Unable to check for new updates at " + getResourceUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}