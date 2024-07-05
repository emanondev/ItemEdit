package emanondev.itemedit.compability;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessagePaper implements MiniMessageUtil {

    private static final LegacyComponentSerializer UNGLY_LEGACY = LegacyComponentSerializer.legacySection().toBuilder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final MiniMessagePaper instance = new MiniMessagePaper();

    public static MiniMessagePaper getInstance() {
        return instance;
    }

    @Override
    public String fromMiniToText(String text) {
        if (text == null || text.isEmpty())
            return text;
        try {
            return UNGLY_LEGACY.serialize(MiniMessage.miniMessage().deserialize(text.replace("§", "&")));
        } catch (ParsingExceptionImpl | NoSuchMethodError e) {
            e.printStackTrace();
            return text;
        }
    }


}
