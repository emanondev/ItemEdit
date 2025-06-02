package emanondev.itemedit.aliases;

import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TrimPatternAliasesOld extends AliasSet<TrimPattern> implements TrimPatternAliases {
    private final HashSet<TrimPattern> values = new HashSet<>();

    public TrimPatternAliasesOld() {
        super("trim_pattern");
        registerValue(TrimPattern.COAST);
        registerValue(TrimPattern.DUNE);
        registerValue(TrimPattern.EYE);
        registerValue(TrimPattern.RIB);
        registerValue(TrimPattern.HOST);
        registerValue(TrimPattern.RAISER);
        registerValue(TrimPattern.SENTRY);
        registerValue(TrimPattern.SHAPER);
        registerValue(TrimPattern.SILENCE);
        registerValue(TrimPattern.SPIRE);
        registerValue(TrimPattern.SNOUT);
        registerValue(TrimPattern.TIDE);
        registerValue(TrimPattern.VEX);
        registerValue(TrimPattern.WARD);
        registerValue(TrimPattern.WILD);
        registerValue(TrimPattern.WAYFINDER);
    }

    @Override
    public String getName(final TrimPattern value) {
        return value.getKey().toString();
    }

    public void registerValue(final TrimPattern pattern) {
        values.add(pattern);
    }

    @Override
    public Collection<TrimPattern> getValues() {
        return Collections.unmodifiableCollection(values);
    }
}

