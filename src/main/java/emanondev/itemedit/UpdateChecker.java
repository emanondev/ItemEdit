package emanondev.itemedit;

import com.google.gson.JsonParser;
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
import java.util.concurrent.Callable;

public class UpdateChecker {
    private final APlugin plugin;
    private String newVersion;
    private Boolean isUpdated = null;

    public UpdateChecker(@NotNull APlugin plugin) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
    }

    public @NotNull String getResourceDownloadUrl() {
        if (plugin.getPluginAdditionalInfo().getModrinthProjectName() != null) {
            return "https://modrinth.com/plugin/" + plugin.getPluginAdditionalInfo().getModrinthProjectName() + "/version/latest";
        }
        return "https://spigotmc.org/resources/" + plugin.getPluginAdditionalInfo().getSpigotResourceId();
    }

    public void logUpdates() {
        SchedulerUtils.runAsync(plugin, () -> {
            if (loadLatestVersion()) {
                isUpdated = newVersion.equals(plugin.getDescription().getVersion());
            }
            if (isUpdated != null && !isUpdated) {
                plugin.log("&bNEW UPDATE&f (&6" + plugin.getDescription().getVersion() + "&f -> &a" + newVersion + "&f) available at &b" + getResourceDownloadUrl());
            }
        });
    }

    private boolean loadLatestVersion() {
        return attemptUpdateCheck("Modrinth", this::loadLatestVersionModrinth)
                || attemptUpdateCheck("Spigot", this::loadLatestVersionSpigot);
    }

    private boolean attemptUpdateCheck(String sourceName, Callable<Boolean> checkMethod) {
        try {
            return checkMethod.call();
        } catch (MalformedURLException e) {
            plugin.log("&cInvalid URL while checking for updates on " + sourceName + ".");
        } catch (FileNotFoundException e) {
            plugin.log("&cUpdate file not found on " + sourceName + ".");
        } catch (UnknownHostException e) {
            plugin.log("&cCannot reach " + sourceName + " server. Check your network.");
        } catch (IOException e) {
            plugin.log("&cI/O error while checking " + sourceName + ": " + e.getMessage());
        } catch (Exception e) {
            plugin.log("&cUnexpected error on " + sourceName + ".");
            e.printStackTrace();
        }
        return false;
    }

    private Boolean loadLatestVersionSpigot() throws Exception {
        if (plugin.getPluginAdditionalInfo().getSpigotResourceId() == null) {
            return false;
        }
        URL checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + plugin.getPluginAdditionalInfo().getSpigotResourceId());
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return true;
    }

    private Boolean loadLatestVersionModrinth() throws Exception {
        if (plugin.getPluginAdditionalInfo().getModrinthProjectId() == null) {
            return false;
        }
        URL checkURL = new URL("https://api.modrinth.com/v2/project/" + plugin.getPluginAdditionalInfo().getModrinthProjectId() + "/version");
        URLConnection con = checkURL.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            newVersion = JsonParser.parseString(reader.readLine())
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("version_number")
                    .getAsString();
        }
        return true;
    }
}