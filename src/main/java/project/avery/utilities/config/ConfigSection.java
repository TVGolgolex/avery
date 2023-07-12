package project.avery.utilities.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigSection {
    private final String fullPath;
    protected final Map<Object, Object> map = new LinkedHashMap<>();

    public ConfigSection() {
        this.fullPath = "";
    }

    public ConfigSection(final ConfigSection root, final String key) {
        this.fullPath = (!root.fullPath.equals("")) ? root.fullPath + "." + key : key;
    }

    public ConfigSection create(final String path) {
        if (path == null) throw new IllegalArgumentException("Cannot create section at empty path");

        //Be sure to have all ConfigSections down the Path
        int i1 = -1, i2;
        ConfigSection section = this;
        while ((i1 = path.indexOf('.', i2 = i1 + 1)) != -1) {
            final String node = path.substring(i2, i1);
            final ConfigSection subSection = section.getConfigSection(node);

            //This subsection does not exists create one
            if (subSection == null) section = section.create(node);
            else section = subSection;
        }

        final String key = path.substring(i2);
        if (section == this) {
            final ConfigSection result = new ConfigSection(this, key);
            this.map.put(key, result);
            return result;
        }

        return section.create(key);
    }

    private ConfigSection getConfigSection(final String node) {
        return (this.map.containsKey(node) && this.map.get(node) instanceof ConfigSection) ?
                (ConfigSection) this.map.get(node) : null;
    }

    public void set(final String path, final Object value) {
        this.set(path, value, true);
    }

    public void set(final String path, final Object value, final boolean searchForSubNodes) {
        if (path == null) throw new IllegalArgumentException("Cannot set a value at empty path");

        //Be sure to have all ConfigSections down the Path
        int i1 = -1, i2 = 0;
        ConfigSection section = this;

        if (searchForSubNodes) while ((i1 = path.indexOf('.', i2 = i1 + 1)) != -1) {
            final String node = path.substring(i2, i1);
            final ConfigSection subSection = section.getConfigSection(node);

            if (subSection == null) section = section.create(node);
            else section = subSection;
        }

        final String key = path.substring(i2);
        if (section == this) if (value == null) this.map.remove(key);
        else this.map.put(key, value);
        else section.set(key, value);
    }

    protected void mapChildrenValues(final Map<Object, Object> output, final ConfigSection section, final boolean deep) {
        if (section != null) for (final Map.Entry<Object, Object> entry : section.map.entrySet())
            if (entry.getValue() instanceof ConfigSection) {
                final Map<Object, Object> result = new LinkedHashMap<>();

                output.put(entry.getKey(), result);

                if (deep) this.mapChildrenValues(result, (ConfigSection) entry.getValue(), true);
            } else output.put(entry.getKey(), entry.getValue());
    }

    public Map<Object, Object> getValues(final boolean deep) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        this.mapChildrenValues(result, this, deep);
        return result;
    }

    public void remove(final String path) {
        this.set(path, null);
    }

    public boolean has(final String path) {
        if (path == null) throw new IllegalArgumentException("Cannot remove a Value at empty path");

        //Be sure to have all ConfigSections down the Path
        int i1 = -1, i2;
        ConfigSection section = this;
        while ((i1 = path.indexOf('.', i2 = i1 + 1)) != -1) {
            final String node = path.substring(i2, i1);
            final ConfigSection subSection = section.getConfigSection(node);

            if (subSection == null) return false;
            else section = subSection;
        }

        final String key = path.substring(i2);
        if (section == this) return this.map.containsKey(key);
        else return section.has(key);
    }

    public <T> T get(final String path) {
        if (path == null) throw new IllegalArgumentException("Cannot remove a Value at empty path");

        //Be sure to have all ConfigSections down the Path
        int i1 = -1, i2;
        ConfigSection section = this;
        while ((i1 = path.indexOf('.', i2 = i1 + 1)) != -1) {
            final String node = path.substring(i2, i1);
            final ConfigSection subSection = section.getConfigSection(node);

            if (subSection == null) section = section.create(node);
            else section = subSection;
        }

        final String key = path.substring(i2);
        if (section == this) return (T) this.map.get(key);
        else return section.get(key);
    }

    public Map getRawMap() {
        return this.map;
    }

    public static ConfigSection convertFromMap(final Map config) {
        final ConfigSection configSection = new ConfigSection();
        configSection.map.putAll(config);

        return configSection;
    }
}
