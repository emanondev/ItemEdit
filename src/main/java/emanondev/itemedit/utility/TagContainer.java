package emanondev.itemedit.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Keyed;
import org.bukkit.Tag;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class TagContainer<T extends Keyed> {

    private final Tag<T> tag;

    public Collection<T> getValues() {
        return tag.getValues();
    }
}
