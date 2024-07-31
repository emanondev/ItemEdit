package emanondev.itemedit.aliases;

import emanondev.itemedit.UtilLegacy;
import org.bukkit.block.banner.PatternType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class BannerPatternAliasesOld extends AliasSet<PatternType> implements BannerPatternAliases {
    public BannerPatternAliasesOld() {
        super("banner_pattern");
        for (PatternType type: UtilLegacy.getPatternTypesFilthered())
            registerValue(type);
    }

    @Override
    public String getName(PatternType value) {
        return ""+value;//do not change this
    }

    public void registerValue(PatternType pattern) {
        values.add(pattern);
    }

    private final HashSet<PatternType> values = new HashSet<>();

    @Override
    public Collection<PatternType> getValues() {
        return Collections.unmodifiableCollection(values);
    }
}

