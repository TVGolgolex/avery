package project.avery.utilities.config;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class YamlConfig extends ConfigMapper implements IConfig {
    public YamlConfig() {

    }

    public YamlConfig(final String filename) {
        this.CONFIG_FILE = new File(filename + (filename.endsWith(".yml") ? "" : ".yml"));
    }

    @Override
    public void save() throws InvalidConfigurationException {
        if (this.CONFIG_FILE == null) throw new IllegalArgumentException("Saving a config without given File");

        if (this.root == null) this.root = new ConfigSection();

        this.clearComments();

        this.internalSave(this.getClass());
        this.saveToYaml();
    }

    private void internalSave(final Class clazz) throws InvalidConfigurationException {
        if (!clazz.getSuperclass().equals(YamlConfig.class)) this.internalSave(clazz.getSuperclass());

        for (final Field field : clazz.getDeclaredFields()) {
            if (this.doSkip(field)) continue;

            String path = "";

            switch (this.CONFIG_MODE) {
                case PATH_BY_UNDERSCORE -> path = field.getName().replace("_", ".");
                case FIELD_IS_KEY -> path = field.getName();
                default -> {
                    final String fieldName = field.getName();
                    if (fieldName.contains("_")) path = field.getName().replace("_", ".");
                    else path = field.getName();
                }
            }

            final ArrayList<String> comments = new ArrayList<>();
            for (final Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof Comment) {
                    final Comment comment = (Comment) annotation;
                    comments.add(comment.value());

                }

                if (annotation instanceof Comments) {
                    final Comments comment = (Comments) annotation;
                    comments.addAll(Arrays.asList(comment.value()));
                }
            }

            if (field.isAnnotationPresent(Path.class)) {
                final Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (comments.size() > 0) for (final String comment : comments) this.addComment(path, comment);

            if (Modifier.isPrivate(field.getModifiers())) field.setAccessible(true);

            try {
                this.converter.toConfig(this, field, this.root, path);
                this.converter.fromConfig(this, field, this.root, path);
            } catch (final Exception e) {
                if (!this.skipFailedObjects) throw new InvalidConfigurationException(
                        "Could not save the Field for " + field.getName() + "#" + path,
                        e
                );
            }
        }
    }

    @Override
    public void save(final File file) throws InvalidConfigurationException {
        if (file == null) throw new IllegalArgumentException("File argument can not be null");

        this.CONFIG_FILE = file;
        this.save();
    }

    @Override
    public void init() throws InvalidConfigurationException {
        if (!this.CONFIG_FILE.exists()) {
            if (this.CONFIG_FILE.getParentFile() != null) this.CONFIG_FILE.getParentFile().mkdirs();

            try {
                this.CONFIG_FILE.createNewFile();
                this.save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException("Could not create new empty Config", e);
            }
        } else this.load();
    }

    @Override
    public void init(final File file) throws InvalidConfigurationException {
        if (file == null) throw new IllegalArgumentException("File argument can not be null");

        this.CONFIG_FILE = file;
        this.init();
    }

    @Override
    public void reload() throws InvalidConfigurationException {
        this.loadFromYaml();
        this.internalLoad(this.getClass());
    }

    @Override
    public void load() throws InvalidConfigurationException {
        if (this.CONFIG_FILE == null) throw new IllegalArgumentException("Loading a config without given File");

        this.loadFromYaml();
        this.update(this.root);
        this.internalLoad(this.getClass());
    }

    private void internalLoad(final Class clazz) throws InvalidConfigurationException {
        if (!clazz.getSuperclass().equals(YamlConfig.class)) this.internalLoad(clazz.getSuperclass());

        boolean save = false;
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

            if (this.root.has(path)) try {
                this.converter.fromConfig(this, field, this.root, path);
            } catch (final Exception e) {
                throw new InvalidConfigurationException("Could not set field", e);
            }
            else try {
                this.converter.toConfig(this, field, this.root, path);
                this.converter.fromConfig(this, field, this.root, path);

                save = true;
            } catch (final Exception e) {
                if (!this.skipFailedObjects) throw new InvalidConfigurationException("Could not get field", e);
            }
        }

        if (save) this.saveToYaml();
    }

    @Override
    public void load(final File file) throws InvalidConfigurationException {
        if (file == null) throw new IllegalArgumentException("File argument can not be null");

        this.CONFIG_FILE = file;
        this.load();
    }
}
