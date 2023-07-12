package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;

public class SetConverter implements Converter {
    private final InternalConverter internalConverter;

    public SetConverter(final InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType genericType) throws
            Exception {
        final java.util.Set<Object> values = (java.util.Set<Object>) obj;
        final java.util.List newList = new ArrayList();

        for (final Object val : values) {
            final Converter converter = this.internalConverter.getConverter(val.getClass());

            if (converter != null) newList.add(converter.toConfig(val.getClass(), val, null));
            else newList.add(val);
        }

        return newList;
    }

    @Override
    public Object fromConfig(final Class type, final Object section, final ParameterizedType genericType) throws
            Exception {
        final java.util.List<Object> values = (java.util.List<Object>) section;
        java.util.Set<Object> newList = new HashSet<>();

        try {
            newList = (java.util.Set<Object>) type.newInstance();
        } catch (final Exception e) {
        }

        if (genericType != null && genericType.getActualTypeArguments()[0] instanceof Class) {
            final Converter converter = this.internalConverter.getConverter((Class) genericType.getActualTypeArguments()[0]);

            if (converter != null) for (final Object value : values)
                newList.add(converter.fromConfig((Class) genericType.getActualTypeArguments()[0], value, null));
            else
                newList.addAll(values);
        } else newList.addAll(values);

        return newList;
    }

    @Override
    public boolean supports(final Class<?> type) {
        return java.util.Set.class.isAssignableFrom(type);
    }

}
