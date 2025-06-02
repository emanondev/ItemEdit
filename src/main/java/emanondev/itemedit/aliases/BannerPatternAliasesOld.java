package emanondev.itemedit.aliases;

import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.block.banner.PatternType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class BannerPatternAliasesOld extends AliasSet<PatternType> implements BannerPatternAliases {
    private final HashSet<PatternType> values = new HashSet<>();

    public BannerPatternAliasesOld() {
        super("banner_pattern");
        for (PatternType type : ItemUtils.getPatternTypesFiltered()) {
            registerValue(type);
        }
    }

    @Override
    public String getName(final PatternType value) {
        return "" + value;//do not change this
    }

    public void registerValue(final PatternType pattern) {
        values.add(pattern);
    }

    @Override
    public Collection<PatternType> getValues() {
        return Collections.unmodifiableCollection(values);
    }
}

