package emanondev.itemedit.compability;

import emanondev.itemedit.ItemEdit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

@Slf4j
public class MiniMessagePaper implements MiniMessageUtil {

    private static final LegacyComponentSerializer UNGLY_LEGACY = LegacyComponentSerializer.legacySection().toBuilder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    @Getter
    private static final MiniMessagePaper instance = new MiniMessagePaper();

    @Override
    public String fromMiniToText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        try {
            return UNGLY_LEGACY.serialize(MiniMessage.miniMessage().deserialize(text.replace("§", "&")));
        } catch (NoSuchMethodError e) {
            if (Bukkit.getPluginManager().isPluginEnabled("SCore")) {
                ItemEdit.get().log("SCore is disabling MiniMessage compability?");
            } else {
                log.warn(e.getMessage(), e);
            }
            return text;
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
            return text;
        }
    }


}
