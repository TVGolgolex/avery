package project.avery.utilities.config.staticconverter;

import project.avery.utilities.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;

public class PrimitiveConverter implements Converter {
    private final HashSet<String> types = new HashSet<>() {{
        this.add("boolean");
        this.add("char");
        this.add("byte");
        this.add("short");
        this.add("int");
        this.add("long");
        this.add("float");
        this.add("double");
    }};

    public PrimitiveConverter(final InternalConverter paperInternalConverter) {
    }

    @Override
    public Object toConfig(final Class<?> type, final Object obj, final ParameterizedType parameterizedType) throws
            Exception {
        return obj;
    }

    @Override
    public Object fromConfig(final Class type, final Object section, final ParameterizedType genericType) throws
            Exception {
        switch (type.getSimpleName()) {
            case "short" -> {
                return (section instanceof Short) ? section : (short) section;
            }
            case "byte" -> {
                return (section instanceof Byte) ? section : (byte) section;
            }
            case "float" -> {
                if (section instanceof Integer) return section;
                return (section instanceof Float) ? section : (float) section;
            }
            case "char" -> {
                return (section instanceof Character) ? section : ((String) section).charAt(0);
            }
            default -> {
                return section;
            }
        }
    }

    @Override
    public boolean supports(final Class<?> type) {
        return this.types.contains(type.getName());
    }
}
