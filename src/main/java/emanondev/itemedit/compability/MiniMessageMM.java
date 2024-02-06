package emanondev.itemedit.compability;

import io.lumine.mythic.bukkit.utils.adventure.text.minimessage.MiniMessage;
import io.lumine.mythic.bukkit.utils.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import io.lumine.mythic.bukkit.utils.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageMM implements MiniMessageUtil {

    public static final LegacyComponentSerializer UNGLY_LEGACY = LegacyComponentSerializer.legacySection().toBuilder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final MiniMessageMM instance = new MiniMessageMM();

    public static MiniMessageMM getInstance() {
        return instance;
    }

    @Override
    public String fromMiniToText(String text) {
        if (text == null || text.isEmpty())
            return text;
        try {
            return UNGLY_LEGACY.serialize(MiniMessage.miniMessage().deserialize(text));
        } catch (ParsingExceptionImpl exception) {
            exception.printStackTrace();
            return text;
        }
    }
}
