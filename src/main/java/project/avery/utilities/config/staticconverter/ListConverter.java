package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class ListConverter implements Converter {
    private final InternalConverter internalConverter;

    public ListConverter(final InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType genericType) throws
            Exception {
        final java.util.List values = (java.util.List) obj;
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
        java.util.List newList = new ArrayList();
        try {
            newList = ((java.util.List) type.newInstance());
        } catch (final Exception e) {
        }

        final java.util.List values = (java.util.List) section;

        if (genericType != null && genericType.getActualTypeArguments()[0] instanceof Class) {
            final Converter converter = this.internalConverter.getConverter((Class) genericType.getActualTypeArguments()[0]);

            if (converter != null) for (int i = 0; i < values.size(); i++)
                newList.add(converter.fromConfig(
                        (Class) genericType.getActualTypeArguments()[0],
                        values.get(i),
                        null
                ));
            else newList = values;
        } else newList = values;

        return newList;
    }

    @Override
    public boolean supports(final Class<?> type) {
        return java.util.List.class.isAssignableFrom(type);
    }
}
