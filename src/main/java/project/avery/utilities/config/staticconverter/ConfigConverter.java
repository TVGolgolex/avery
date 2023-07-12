package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.ConfigSection;
import project.avery.utilities.config.InternalConverter;
import project.avery.utilities.config.YamlConfig;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class ConfigConverter implements Converter {
    private final InternalConverter internalConverter;

    public ConfigConverter(final InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType parameterizedType) throws
            Exception {
        return (obj instanceof Map) ? obj : ((YamlConfig) obj).saveToMap(obj.getClass());
    }

    @Override
    public Object fromConfig(final Class type, final Object section, final ParameterizedType genericType) throws
            Exception {
        final YamlConfig obj = (YamlConfig) this.newInstance(type);

        // Inject Converter stack into subconfig
        for (final Class aClass : this.internalConverter.getCustomConverters()) obj.addConverter(aClass);

        obj.loadFromMap((section instanceof Map) ? (Map) section : ((ConfigSection) section).getRawMap(), type);
        return obj;
    }

    // recursively handles enclosed classes
    public Object newInstance(final Class type) throws Exception {
        final Class enclosingClass = type.getEnclosingClass();
        if (enclosingClass != null) {
            final Object instanceOfEnclosingClass = this.newInstance(enclosingClass);
            return type.getConstructor(enclosingClass).newInstance(instanceOfEnclosingClass);
        } else return type.newInstance();
    }

    @Override
    public boolean supports(final Class<?> type) {
        return YamlConfig.class.isAssignableFrom(type);
    }
}
