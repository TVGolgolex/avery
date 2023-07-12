package project.avery.utilities.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BaseConfig {
    protected transient File CONFIG_FILE = null;
    protected transient String[] CONFIG_HEADER = null;
    protected transient ConfigMode CONFIG_MODE = ConfigMode.DEFAULT;
    protected transient boolean skipFailedObjects = false;

    protected transient InternalConverter converter = new InternalConverter();

    /**
     * This function gets called after the File has been loaded and before the Converter gets it.
     * This is used to manually edit the configSection when you updated the config or something
     *
     * @param configSection The root ConfigSection with all Subnodes loaded into
     */
    public void update(final ConfigSection configSection) {

    }

    /**
     * Add a Custom Converter. A Converter can take Objects and return a pretty Object which gets saved/loaded from
     * the Converter. How a Converter must be build can be looked up in the Converter Interface.
     *
     * @param addConverter Converter to be added
     * @throws InvalidConverterException If the Converter has any errors this Exception tells you what
     */
    public void addConverter(final Class addConverter) throws InvalidConverterException {
        this.converter.addCustomConverter(addConverter);
    }

    protected boolean doSkip(final Field field) {
        if (Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) return true;

        if (Modifier.isStatic(field.getModifiers())) {
            if (!field.isAnnotationPresent(PreserveStatic.class)) return true;

            final PreserveStatic presStatic = field.getAnnotation(PreserveStatic.class);
            return !presStatic.value();
        }

        return false;
    }

    protected void configureFromSerializeOptionsAnnotation() {
        if (!this.getClass().isAnnotationPresent(SerializeOptions.class)) return;

        final SerializeOptions options = this.getClass().getAnnotation(SerializeOptions.class);
        this.CONFIG_HEADER = options.configHeader();
        this.CONFIG_MODE = options.configMode();
        this.skipFailedObjects = options.skipFailedObjects();
    }
}
