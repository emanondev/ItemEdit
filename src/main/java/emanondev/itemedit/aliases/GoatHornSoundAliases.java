package emanondev.itemedit.aliases;

import org.bukkit.MusicInstrument;

import java.util.Collection;
import java.util.Locale;

public class GoatHornSoundAliases extends AliasSet<MusicInstrument> {

    public GoatHornSoundAliases() {
        super("goat_horn_sound");
        MusicInstrument.values(); //force load the class or throw an exception if absent
    }

    @Override
    public String getName(MusicInstrument type) {
        return type.getKey().getKey().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Collection<MusicInstrument> getValues() {
        return MusicInstrument.values();
    }
}
