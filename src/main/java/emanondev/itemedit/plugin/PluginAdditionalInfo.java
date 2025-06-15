package emanondev.itemedit.plugin;

import emanondev.itemedit.APlugin;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Getter
public class PluginAdditionalInfo {

    private final String modrinthProjectId;
    private final String modrinthProjectName;
    private final Integer spigotResourceId;
    private final boolean foliaSupported;
    private final Integer bstatsPluginId;
    private final APlugin plugin;

    public PluginAdditionalInfo(@NotNull APlugin plugin) {
        this.plugin = plugin;
        Yaml yaml = new Yaml();
        Integer spigotResourceId = null;
        String modrinthResourceId = null;
        String modrinthResourceName = null;
        boolean foliaSupported = false;
        Integer bstatsPluginId = null;

        // Load from resources inside the JAR
        try (InputStream inputStream = plugin.getResource("plugin.yml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("YAML file not found inside JAR");
            }
            Map<String, Object> data = yaml.load(inputStream);

            spigotResourceId = (Integer) data.get("spigot-resource-id");
            modrinthResourceId = (String) data.get("modrinth-project-id");
            modrinthResourceName = (String) data.get("modrinth-project-name");
            foliaSupported = (Boolean) data.getOrDefault("folia-supported", false);
            bstatsPluginId = (Integer) data.get("bstats-plugin-id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.spigotResourceId = spigotResourceId;
        this.modrinthProjectId = modrinthResourceId;
        this.modrinthProjectName = modrinthResourceName;
        this.foliaSupported = foliaSupported;
        this.bstatsPluginId = bstatsPluginId;
    }

    public <T> T get(@NotNull String path) {
        return get(path, null);
    }

    public <T> T get(@NotNull String path, @Nullable T def) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = plugin.getResource("plugin.yml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("YAML file not found inside JAR");
            }
            Map<String, Object> data = yaml.load(inputStream);
            return (T) data.getOrDefault(path, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }
}
