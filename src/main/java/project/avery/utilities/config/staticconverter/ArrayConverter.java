package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;

public class ArrayConverter implements Converter {

    private final InternalConverter internalConverter;

    public ArrayConverter(final InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType parameterizedType) throws
            Exception {
        final Class<?> singleType = type.getComponentType();
        final Converter conv = this.internalConverter.getConverter(singleType);
        if (conv == null) return obj;

        final Object[] ret = new Object[java.lang.reflect.Array.getLength(obj)];
        for (int i = 0; i < ret.length; i++)
            ret[i] = conv.toConfig(singleType, java.lang.reflect.Array.get(obj, i), parameterizedType);
        return ret;
    }

    @Override
    public Object fromConfig(final Class type, final Object section, final ParameterizedType genericType) throws
            Exception {
        final Class<?> singleType = type.getComponentType();
        final java.util.List values;

        if (section instanceof java.util.List) values = (java.util.List) section;
        else {
            values = new ArrayList();
            Collections.addAll(values, (Object[]) section);
        }

        final Object ret = java.lang.reflect.Array.newInstance(singleType, values.size());
        final Converter conv = this.internalConverter.getConverter(singleType);
        if (conv == null) return values.toArray((Object[]) ret);

        for (int i = 0; i < values.size(); i++)
            java.lang.reflect.Array.set(ret, i, conv.fromConfig(singleType, values.get(i), genericType));
        return ret;
    }

    @Override
    public boolean supports(final Class<?> type) {
        return type.isArray();
    }
}
