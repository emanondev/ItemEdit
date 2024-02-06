package emanondev.itemedit.compability;

import emanondev.itemedit.Util;

public interface MiniMessageUtil {

    String fromMiniToText(String text);
    static boolean hasMiniMessage(){
        return getInstance() != null;
    }

    static MiniMessageUtil getInstance() {
        try {
            if (Util.hasPaperAPI() && Util.isVersionAfter(1, 16, 5))
                return MiniMessagePaper.getInstance();
            if (Hooks.isMythicMobsEnabled())
                return MiniMessageMM.getInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
