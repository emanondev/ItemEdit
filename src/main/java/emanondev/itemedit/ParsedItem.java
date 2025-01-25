package emanondev.itemedit;

import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since MC 1.20.5
 */
public class ParsedItem {

    private final String type;
    private final Map<String, Object> components = new LinkedHashMap<>();
    private int amount;

    public ParsedItem(ItemStack itemStack) {
        this(itemStack.getType(), ItemUtils.getMeta(itemStack));
        amount = itemStack.getAmount();
    }

    private ParsedItem(Material mat, ItemMeta meta) {
        this(mat, meta.getAsString());
    }


    private ParsedItem(Material mat, String raw) {
        int index = raw.indexOf("{");
        if (index == -1) {
            type = mat.getKey().toString();
            return;
        }
        type = mat.getKey().toString();
        raw = raw.substring(index);
        components.putAll((Map<String, Object>) eatMap(raw, 0, 0).payload);
    }

    public static Map<String, Object> loadMap(Map<String, Object> data, String path) {
        data.putIfAbsent(path, new LinkedHashMap<>());
        return (Map<String, Object>) data.get(path);
    }

    public static List<Object> loadList(Map<String, Object> data, String path) {
        data.putIfAbsent(path, new ArrayList<>());
        return (List<Object>) data.get(path);
    }

    public static Map<String, Object> getMap(Map<String, Object> data, String path) {
        return (Map<String, Object>) data.getOrDefault(path, null);
    }

    public static List<Object> getListOfRaw(Map<String, Object> data, String path) {
        return (List<Object>) data.getOrDefault(path, null);
    }

    public static List<Map<String, Object>> getListOfMap(Map<String, Object> data, String path) {
        return (List<Map<String, Object>>) data.getOrDefault(path, null);
    }

    public static List<Object> loadListOfRaw(Map<String, Object> data, String path) {
        data.putIfAbsent(path, new ArrayList<>());
        return (List<Object>) data.get(path);
    }

    public static List<Map<String, Object>> loadListOfMap(Map<String, Object> data, String path) {
        data.putIfAbsent(path, new ArrayList<>());
        return (List<Map<String, Object>>) data.get(path);
    }

    public static NamespacedKey readNamespacedKey(Map<String, Object> data, String path, NamespacedKey defValue) {
        String text = readString(data, path, null);
        if (text == null)
            return defValue;
        return new NamespacedKey(text.split(":")[0], text.split(":")[1]);
    }

    public static NamespacedKey readNamespacedKey(Map<String, Object> data, String path) {
        return readNamespacedKey(data, path, null);
    }

    public static String readString(Map<String, Object> data, String path, String defValue) {
        if (!data.containsKey(path))
            return defValue;
        return (String) data.get(path);
    }

    public static String readString(Map<String, Object> data, String path) {
        return readString(data, path, null);
    }

    public static Boolean readBoolean(Map<String, Object> data, String path, Boolean defValue) {
        Integer value = readInt(data, path);
        return value == null ? defValue : value != 0;
    }

    public static Boolean readBoolean(Map<String, Object> data, String path) {
        return readBoolean(data, path, null);
    }

    public static Integer readInt(Map<String, Object> data, String path) {
        return readInt(data, path, null);
    }

    public static Integer readInt(Map<String, Object> data, String path, Integer defValue) {
        if (!data.containsKey(path))
            return defValue;
        String value = (String) data.get(path);
        if (value.endsWith("b"))
            value = value.substring(0, value.length() - 1);
        return Integer.parseInt(value);
    }

    public static Double readDouble(Map<String, Object> data, String path) {
        return readDouble(data, path, null);
    }

    public static Double readDouble(Map<String, Object> data, String path, Double defValue) {
        if (!data.containsKey(path))
            return defValue;
        String value = (String) data.get(path);
        if (value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Double.parseDouble(value);
    }

    public static Float readFloat(Map<String, Object> data, String path) {
        return readFloat(data, path, null);
    }

    public static Float readFloat(Map<String, Object> data, String path, Float defValue) {
        Double value = readDouble(data, path, null);
        return value == null ? defValue : value.floatValue();
    }

    public static void setValue(Map<String, Object> data, String path, String value) {
        if (value == null)
            data.remove(path);
        else if (!(value.startsWith("\"") && value.endsWith("\"")))
            data.put(path, "\"" + value + "\"");
        else
            data.put(path, value);
    }

    public static void setValue(Map<String, Object> data, String path, Boolean value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, String.valueOf(value ? 1 : 0));
    }

    public static void setValue(Map<String, Object> data, String path, Integer value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, value.toString());
    }

    public static void setValue(Map<String, Object> data, String path, Double value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, value.toString());
    }

    public static void setValue(Map<String, Object> data, String path, Float value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, value.toString());
    }

    public static void setValue(Map<String, Object> data, String path, NamespacedKey value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, value.toString());
    }

    public static void setValue(Map<String, Object> data, String path, Keyed value) {
        if (value == null)
            data.remove(path);
        else
            data.put(path, value.getKey().toString());
    }

    public void set(@Nullable String value, String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (!(map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map)) {
                if (value == null)
                    return;
                map.put(paths[i], new LinkedHashMap<String, Object>());
            }
            map = (Map<String, Object>) map.get(paths[i]);
        }
        if (value == null)
            map.remove(paths[paths.length - 1]);
        else
            map.put(paths[paths.length - 1], value);
    }

    public void set(@NotNull Map<String, Object> value, String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (!(map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map))
                map.put(paths[i], new LinkedHashMap<String, Object>());
            map = (Map<String, Object>) map.get(paths[i]);
        }
        fixValue(value);
        map.put(paths[paths.length - 1], value);
    }

    public void remove(String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map)
                map = (Map<String, Object>) map.get(paths[i]);
            else
                return;
        }
        map.remove(paths[paths.length - 1], new LinkedHashMap<>());
    }

    public void loadEmptyMap(String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if ((!map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map))
                map.put(paths[i], new LinkedHashMap<String, Object>());
            map = (Map<String, Object>) map.get(paths[i]);
        }
        if (!map.containsKey(paths[paths.length - 1]))
            map.put(paths[paths.length - 1], new LinkedHashMap<>());
    }

    public void set(@NotNull List<Map<String, Object>> value, String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (!(map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map))
                map.put(paths[i], new LinkedHashMap<String, Object>());
            map = (Map<String, Object>) map.get(paths[i]);
        }
        fixValue(value);
        map.put(paths[paths.length - 1], value);
    }

    private void fixValue(Map<String, Object> value) {
        value.forEach((k, v) -> {
            if (v instanceof Map) {
                fixValue((Map<String, Object>) v);
            } else if (v instanceof Number) {
                value.put(k, v.toString());
            } else if (v instanceof NamespacedKey) {
                value.put(k, v.toString());
            } else if (v instanceof Boolean) {
                value.put(k, ((boolean) v) ? "1b" : "0b");
            } else if (v instanceof List) {
                fixValue((List<Map<String, Object>>) v);
            }
        });
    }

    private void fixValue(List<Map<String, Object>> value) {
        value.forEach(this::fixValue);
    }

    public String load(@Nullable String defValue, String... paths) {
        String raw = read(paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Double load(Double defValue, String... paths) {
        Double raw = readDouble(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Float load(Float defValue, String... paths) {
        Float raw = readFloat(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Long load(Long defValue, String... paths) {
        Long raw = readLong(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Integer load(Integer defValue, String... paths) {
        Integer raw = readInteger(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Byte load(Byte defValue, String... paths) {
        Byte raw = readByte(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public Boolean load(Boolean defValue, String... paths) {
        Boolean raw = readBoolean(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public NamespacedKey load(NamespacedKey defValue, String... paths) {
        NamespacedKey raw = readNamespacedKey(null, paths);
        if (raw == null && defValue != null) {
            set(defValue, paths);
            return defValue;
        }
        return raw;
    }

    public void set(Double value, String... paths) {
        set(value == null ? null : String.valueOf(value), paths);
    }

    public void set(Float value, String... paths) {
        set(value == null ? null : value + "f", paths);
    }

    public void set(Long value, String... paths) {
        set(value == null ? null : String.valueOf(value), paths);
    }

    public void set(Integer value, String... paths) {
        set(value == null ? null : String.valueOf(value), paths);
    }

    public void set(Byte value, String... paths) {
        set(value == null ? null : (value + "b"), paths);
    }

    public void set(Boolean value, String... paths) {
        set(value == null ? null : (value ? "1b" : "0b"), paths);
    }

    public void set(NamespacedKey value, String... paths) {
        set(value == null ? null : value.toString(), paths);
    }

    private String read(String[] paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map)
                map = (Map<String, Object>) map.get(paths[i]);
            else
                return null;
        }
        return map.get(paths[paths.length - 1]) instanceof String ? (String) map.get(paths[paths.length - 1]) : null;
    }

    public Map<String, Object> readMap(String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map)
                map = (Map<String, Object>) map.get(paths[i]);
            else
                return null;
        }
        return map.get(paths[paths.length - 1]) instanceof Map ? (Map<String, Object>) map.get(paths[paths.length - 1]) : null;
    }

    public List<Map<String, Object>> readList(String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if (map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map)
                map = (Map<String, Object>) map.get(paths[i]);
            else
                return null;
        }
        return map.get(paths[paths.length - 1]) instanceof List ? (List<Map<String, Object>>) map.get(paths[paths.length - 1]) : null;
    }

    public Integer readInteger(Integer defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        if (value.endsWith("b") || value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Integer.valueOf(value);
    }

    public String readString(String defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        return value;
    }

    public Double readDouble(Double defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        if (value.endsWith("b") || value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Double.valueOf(value);
    }

    public Float readFloat(Float defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        if (value.endsWith("b") || value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Float.valueOf(value);
    }

    public Long readLong(Long defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        if (value.endsWith("b") || value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Long.valueOf(value);
    }

    public Byte readByte(Byte defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        if (value.endsWith("b") || value.endsWith("f"))
            value = value.substring(0, value.length() - 1);
        return Byte.valueOf(value);
    }

    public NamespacedKey readNamespacedKey(NamespacedKey defValue, String... paths) {
        String value = read(paths);
        if (value == null)
            return defValue;
        String[] split = value.split(":");
        if (split.length != 2)
            return defValue;
        return new NamespacedKey(split[0], split[1]);
    }

    public Boolean readBoolean(Boolean defValue, String... paths) {
        return readInteger((!defValue) ? 0 : 1, paths) != 0;
    }

    public Map<String, Object> getMap() {
        return components;
    }

    public ItemStack toItemStack() {
        ItemStack item = Bukkit.getItemFactory().createItemStack(toString());
        item.setAmount(amount);
        return item;
    }

    public ItemMeta toItemMeta() {
        return Bukkit.getItemFactory().createItemStack(toString()).getItemMeta();
    }

    public String toString() {
        StringBuilder text = new StringBuilder(type);
        if (components.isEmpty())
            return text.toString();
        text.append("[");
        components.forEach((key, value) -> text.append(key)
                .append("=").append(writeComponent(value)).append(","));
        return text.substring(0, text.length() - 1) + "]";
    }

    private boolean needBrackets(String text) {
        Pattern pattern = Pattern.compile("^[-_.0-9a-zA-Z]+$");
        Matcher matcher = pattern.matcher(text);
        return !matcher.matches();
    }

    private String writeComponent(Object value) {
        if (value instanceof String) {
            String text = (String) value;
            if (text.startsWith("'")) {
                return text;
            }
            if (needBrackets(text)) {
                return "\"" + text + "\"";
            }
            return text;
        }
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            if (list.isEmpty())
                return "[]";
            StringBuilder text = new StringBuilder("[");
            list.forEach((el) -> text.append(writeComponent(el)).append(","));
            return text.substring(0, text.length() - 1) + "]";
        }
        if (value instanceof Map) {
            Map<String, Object> map = ((Map<String, Object>) value);
            if (map.isEmpty())
                return "{}";
            StringBuilder text = new StringBuilder("{");
            map.forEach((key, val) -> text.append(needBrackets(key) ? "\"" + key + "\"" : key).append(":").append(writeComponent(val)).append(","));
            return text.substring(0, text.length() - 1) + "}";
        }
        ItemEdit.get().log(value.getClass().getSimpleName() + " " + value);
        throw new RuntimeException();
    }

    private EatResult eatList(String raw, int depth, int index) {
        List<Object> value = new ArrayList<>();
        index++;
        while (raw.charAt(index) != ']') {
            EatResult tmp;
            switch (raw.charAt(index)) {
                case '[':
                    tmp = eatList(raw, depth + 1, index);
                    break;
                case '{':
                    tmp = eatMap(raw, depth + 1, index);
                    break;
                case '"':
                    tmp = eatString(raw, depth + 1, index);
                    break;
                case '\'':
                    tmp = eatTextComponent(raw, depth + 1, index);
                    break;
                default:
                    //String charAsString = Character.toString(raw.charAt(index));
                    //Pattern pattern = Pattern.compile("[-._0-9a-zA-Z]");
                    //Matcher matcher = pattern.matcher(charAsString);
                    //if (matcher.matches()) {
                    tmp = eatRawValue(raw, 1, index);
                    //    break;
                    //}
                    //throw new RuntimeException();
            }
            value.add(tmp.payload);
            index = tmp.newIndex;
            if (raw.charAt(index) == ',')
                index++;
        }

        return new EatResult(index + 1, value);
    }

    private EatResult eatMap(String raw, int depth, int index) {
        Map<String, Object> map = new LinkedHashMap<>();
        index++;
        while (raw.charAt(index) != '}') {
            String key;
            if (raw.charAt(index) == '\"') {
                EatResult res = eatString(raw, depth + 1, index);
                key = (String) res.payload;
                index = res.newIndex;
            } else {
                EatResult res = eatRawValue(raw, depth + 1, index);
                key = (String) res.payload;
                index = res.newIndex;
            }
            index++;
            Object value;
            EatResult tmp;
            switch (raw.charAt(index)) {
                case '[':
                    tmp = eatList(raw, depth + 1, index);
                    break;
                case '{':
                    tmp = eatMap(raw, depth + 1, index);
                    break;
                case '"':
                    tmp = eatString(raw, depth + 1, index);
                    break;
                case '\'':
                    tmp = eatTextComponent(raw, depth + 1, index);
                    break;
                default:
                    //String charAsString = Character.toString(raw.charAt(index));
                    //Pattern pattern = Pattern.compile("[-._0-9a-zA-Z]");
                    //Matcher matcher = pattern.matcher(charAsString);
                    //if (matcher.matches()) {
                    tmp = eatRawValue(raw, 1, index);
                    break;
                //}
                //throw new RuntimeException();
            }
            value = tmp.payload;
            index = tmp.newIndex;
            map.put(key.toString(), value);
            if (raw.charAt(index) == ',') {
                index++;
            }
        }
        return new EatResult(index + 1, map);
    }

    private EatResult eatString(String raw, int depth, int index) {
        StringBuilder value = new StringBuilder();
        index++;
        while (raw.charAt(index) != '\"') {
            if (raw.charAt(index) == '\\') {
                value.append("\\");
                index++;
            }
            value.append(raw.charAt(index));
            index++;
        }
        return new EatResult(index + 1, value.toString());
    }

    private EatResult eatTextComponent(String raw, int depth, int index) {
        StringBuilder value = new StringBuilder("'");
        index++;
        while (raw.charAt(index) != '\'') {
            if (raw.charAt(index) == '\\') {
                value.append("\\");
                index++;
            }
            value.append(raw.charAt(index));
            index++;
        }
        value.append('\'');
        return new EatResult(index + 1, value.toString());
    }

    private EatResult eatRawValue(String raw, int depth, int index) {
        StringBuilder value = new StringBuilder();
        while (raw.charAt(index) != ',' && raw.charAt(index) != ']' && raw.charAt(index) != '}'
                && raw.charAt(index) != ':' && raw.charAt(index) != '=') {
            value.append(raw.charAt(index));
            index++;
        }
        return new EatResult(index, value.toString());
    }

    public List<Map<String, Object>> loadEmptyList(String... paths) {
        Map<String, Object> map = components;
        for (int i = 0; i < paths.length - 1; i++) {
            if ((!map.containsKey(paths[i]) && map.get(paths[i]) instanceof Map))
                map.put(paths[i], new LinkedHashMap<String, Object>());
            map = (Map<String, Object>) map.get(paths[i]);
        }
        if (!map.containsKey(paths[paths.length - 1]))
            map.put(paths[paths.length - 1], new ArrayList<Map<String, Object>>());
        return (List<Map<String, Object>>) map.get(paths[paths.length - 1]);
    }

    private static class EatResult {
        private final int newIndex;
        private final Object payload;

        private EatResult(int newIndex, Object payload) {
            this.newIndex = newIndex;
            this.payload = payload;
        }
    }
}

