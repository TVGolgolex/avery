package project.avery.utilities.config;

import project.avery.utilities.config.staticconverter.Converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ConfigMapper extends BaseConfigMapper {
    public Map<String, Object> saveToMap(final Class clazz) throws Exception {
        final Map<String, Object> returnMap = new HashMap<>();

        if (!clazz.getSuperclass().equals(YamlConfig.class) && !clazz.getSuperclass().equals(Object.class)) {
            final Map<String, Object> map = this.saveToMap(clazz.getSuperclass());
            returnMap.putAll(map);
        }

        for (final Field field : clazz.getDeclaredFields()) {
            if (this.doSkip(field)) continue;

            String path;

            switch (this.CONFIG_MODE) {
                case PATH_BY_UNDERSCORE -> path = field.getName().replace("_", ".");
                case FIELD_IS_KEY -> path = field.getName();
                default -> {
                    final String fieldName = field.getName();
                    if (fieldName.contains("_")) path = field.getName().replace("_", ".");
                    else path = field.getName();
                }
            }

            if (field.isAnnotationPresent(Path.class)) {
                final Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (Modifier.isPrivate(field.getModifiers())) field.setAccessible(true);

            try {
                returnMap.put(path, field.get(this));
            } catch (final IllegalAccessException e) {
            }
        }

        final Converter mapConverter = (Converter) this.converter.getConverter(Map.class);
        return (Map<String, Object>) mapConverter.toConfig(HashMap.class, returnMap, null);
    }

    public void loadFromMap(final Map section, final Class clazz) throws Exception {
        if (!clazz.getSuperclass().equals(YamlConfig.class) && !clazz.getSuperclass().equals(YamlConfig.class))
            this.loadFromMap(section, clazz.getSuperclass());

        for (final Field field : clazz.getDeclaredFields()) {
            if (this.doSkip(field)) continue;

            String path =
                    (this.CONFIG_MODE.equals(ConfigMode.PATH_BY_UNDERSCORE)) ? field.getName().replaceAll("_", ".") :
                            field.getName();

            if (field.isAnnotationPresent(Path.class)) {
                final Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (Modifier.isPrivate(field.getModifiers())) field.setAccessible(true);

            this.converter.fromConfig((YamlConfig) this, field, ConfigSection.convertFromMap(section), path);
        }
    }
}
