package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.ConfigSection;
import project.avery.utilities.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

public class MapConverter implements Converter {
    private final InternalConverter internalConverter;

    public MapConverter(final InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType genericType) throws
            Exception {
        final java.util.Map<Object, Object> map1 = (java.util.Map) obj;

        for (final java.util.Map.Entry<Object, Object> entry : map1.entrySet()) {
            if (entry.getValue() == null) continue;

            final Class clazz = entry.getValue().getClass();

            final Converter converter = this.internalConverter.getConverter(clazz);
            map1.put(
                    entry.getKey(),
                    (converter != null) ? converter.toConfig(clazz, entry.getValue(), null) : entry.getValue()
            );
        }

        return map1;
    }

    @Override
    public Object fromConfig(final Class type, Object section, final ParameterizedType genericType) throws Exception {
        if (genericType != null) {

            java.util.Map map;
            try {
                map = ((java.util.Map) ((Class) genericType.getRawType()).newInstance());
            } catch (final InstantiationException e) {
                map = new HashMap();
            }

            if (genericType.getActualTypeArguments().length == 2) {
                final Class keyClass = ((Class) genericType.getActualTypeArguments()[0]);

                if (section == null) section = new HashMap<>();

                final java.util.Map<?, ?> map1 = (section instanceof java.util.Map) ? (java.util.Map) section :
                        ((ConfigSection) section).getRawMap();
                for (final java.util.Map.Entry<?, ?> entry : map1.entrySet()) {
                    final Object key;

                    if (keyClass.equals(Integer.class) && !(entry.getKey() instanceof Integer))
                        key = Integer.valueOf((String) entry.getKey());
                    else
                        if (keyClass.equals(Short.class) && !(entry.getKey() instanceof Short))
                            key = Short.valueOf((String) entry.getKey());
                        else
                            if (keyClass.equals(Byte.class) && !(entry.getKey() instanceof Byte))
                                key = Byte.valueOf((String) entry.getKey());
                            else
                                if (keyClass.equals(Float.class) && !(entry.getKey() instanceof Float))
                                    key = Float.valueOf((String) entry.getKey());
                                else
                                    if (keyClass.equals(Double.class) && !(entry.getKey() instanceof Double))
                                        key = Double.valueOf((String) entry.getKey());
                                    else key = entry.getKey();

                    final Class clazz;
                    if (genericType.getActualTypeArguments()[1] instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) genericType.getActualTypeArguments()[1];
                        clazz = (Class) parameterizedType.getRawType();
                    } else clazz = (Class) genericType.getActualTypeArguments()[1];

                    final Converter converter = this.internalConverter.getConverter(clazz);
                    map.put(
                            key,
                            (converter != null) ? converter.fromConfig(
                                    clazz,
                                    entry.getValue(),
                                    (genericType.getActualTypeArguments()[1] instanceof ParameterizedType) ?
                                            (ParameterizedType) genericType.getActualTypeArguments()[1] : null
                            ) : entry.getValue()
                    );
                }
            } else {
                final Converter converter = this.internalConverter.getConverter((Class) genericType.getRawType());

                if (converter != null) return converter.fromConfig((Class) genericType.getRawType(), section, null);

                return (section instanceof java.util.Map) ? (java.util.Map) section :
                        ((ConfigSection) section).getRawMap();
            }

            return map;
        } else return section;
    }

    @Override
    public boolean supports(final Class<?> type) {
        return java.util.Map.class.isAssignableFrom(type);
    }
}
