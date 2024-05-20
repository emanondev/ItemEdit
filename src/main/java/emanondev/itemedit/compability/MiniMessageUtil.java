package emanondev.itemedit.compability;

public interface MiniMessageUtil {

    String fromMiniToText(String text);

    /*
    static boolean hasMiniMessage() {
        return getInstance() != null;
    }*/

    /*
    static MiniMessageUtil getInstance() {
        try {
            if (Util.hasPaperAPI() && Util.isVersionAfter(1, 16, 5))
                return MiniMessagePaper.getInstance();
        } catch (Throwable t) {

        }
        try {
            if (Hooks.isMythicMobsEnabled())
                return MiniMessageMM.getInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
