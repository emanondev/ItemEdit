package emanondev.itemedit;

import emanondev.itemedit.utility.SchedulerUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class UpdateChecker {
    private final int project;
    private final APlugin plugin;
    private URL checkURL;
    private String newVersion;

    public UpdateChecker(@NotNull APlugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            plugin.log("&cCould not connect to Spigot!");
        }
    }

    public @NotNull String getResourceUrl() {
        return "https://spigotmc.org/resources/" + project;
    }

    private boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }

    public void logUpdates() {
        SchedulerUtils.runAsync(plugin, () -> {
            try {
                if (checkForUpdates()) {
                    plugin.log("&eNew Update at " + getResourceUrl());
                }
            } catch (FileNotFoundException e) {
                plugin.log("&cUnable to check updates at " + getResourceUrl());
            } catch (UnknownHostException e) {
                plugin.log("&cUnable to reach Spigot server. Check your network connection.");
            } catch (IOException e) {
                plugin.log("&cError while checking for updates: " + e.getMessage());
            } catch (Exception e) {
                plugin.log("&cAn unexpected error occurred while checking for updates.");
                e.printStackTrace();
            }
        });
    }
}