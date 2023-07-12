package project.avery.utilities.config;

import project.avery.utilities.config.staticconverter.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class InternalConverter {
    private final LinkedHashSet<Converter> converters = new LinkedHashSet<>();
    private final List<Class> customConverters = new ArrayList<>();

    public InternalConverter() {
        try {
            this.addConverter(PrimitiveConverter.class);
            this.addConverter(ConfigConverter.class);
            this.addConverter(ListConverter.class);
            this.addConverter(MapConverter.class);
            this.addConverter(ArrayConverter.class);
            this.addConverter(SetConverter.class);
        } catch (final InvalidConverterException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addConverter(final Class converter) throws InvalidConverterException {
        if (!Converter.class.isAssignableFrom(converter))
            throw new InvalidConverterException("Converter does not implement the Interface Converter - " + converter.getSimpleName());

        try {
            final Converter converter1 = (Converter) converter.getConstructor(InternalConverter.class).newInstance(this);
            this.converters.add(converter1);
        } catch (final NoSuchMethodException e) {
            throw new InvalidConverterException(
                    "Converter does not implement a Constructor which takes the InternalConverter instance",
                    e
            );
        } catch (final InvocationTargetException e) {
            throw new InvalidConverterException("Converter could not be invoked", e);
        } catch (final InstantiationException e) {
            throw new InvalidConverterException("Converter could not be instantiated", e);
        } catch (final IllegalAccessException e) {
            throw new InvalidConverterException(
                    "Converter does not implement a public Constructor which takes the InternalConverter instance",
                    e
            );
        }
    }

    public Converter getConverter(final Class type) {
        for (final Converter converter : this.converters) if (converter.supports(type)) return converter;
        return null;
    }

    public void fromConfig(final YamlConfig config, final Field field, final ConfigSection root, final String path) throws
            Exception {
        final Object obj = field.get(config);

        Converter converter;

        if (obj != null) {
            converter = this.getConverter(obj.getClass());

            if (converter != null) {
				/*
					If we're trying to assign a value to a static variable
                    then assure there's the "PreserveStatic" annotation on there!
                     */
                if (Modifier.isStatic(field.getModifiers())) {
                    if (!field.isAnnotationPresent(PreserveStatic.class)) return;

                    final PreserveStatic staticConfig = field.getAnnotation(PreserveStatic.class);
                    if (!staticConfig.value()) return;

                    field.set(
                            null,
                            converter.fromConfig(
                                    field.getType(),
                                    root.get(path),
                                    (field.getGenericType() instanceof ParameterizedType) ?
                                            (ParameterizedType) field.getGenericType() : null
                            )
                    );
                    return;
                }

                field.set(
                        config,
                        converter.fromConfig(
                                obj.getClass(),
                                root.get(path),
                                (field.getGenericType() instanceof ParameterizedType) ?
                                        (ParameterizedType) field.getGenericType() : null
                        )
                );
                return;
            } else {
                converter = this.getConverter(field.getType());
                if (converter != null) {
					/*
					If we're trying to assign a value to a static variable
                    then assure there's the "PreserveStatic" annotation on there!
                     */
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (!field.isAnnotationPresent(PreserveStatic.class)) return;

                        final PreserveStatic staticConfig = field.getAnnotation(PreserveStatic.class);
                        if (!staticConfig.value()) return;

                        field.set(
                                null,
                                converter.fromConfig(
                                        field.getType(),
                                        root.get(path),
                                        (field.getGenericType() instanceof ParameterizedType) ?
                                                (ParameterizedType) field.getGenericType() : null
                                )
                        );
                        return;
                    }
                    field.set(
                            config,
                            converter.fromConfig(
                                    field.getType(),
                                    root.get(path),
                                    (field.getGenericType() instanceof ParameterizedType) ?
                                            (ParameterizedType) field.getGenericType() : null
                            )
                    );
                    return;
                }
            }
        } else {
            converter = this.getConverter(field.getType());

            if (converter != null) {
				/*
					If we're trying to assign a value to a static variable
                    then assure there's the "PreserveStatic" annotation on there!
                     */
                if (Modifier.isStatic(field.getModifiers())) {
                    if (!field.isAnnotationPresent(PreserveStatic.class)) return;

                    final PreserveStatic staticConfig = field.getAnnotation(PreserveStatic.class);
                    if (!staticConfig.value()) return;

                    field.set(
                            null,
                            converter.fromConfig(
                                    field.getType(),
                                    root.get(path),
                                    (field.getGenericType() instanceof ParameterizedType) ?
                                            (ParameterizedType) field.getGenericType() : null
                            )
                    );
                    return;
                }

                field.set(
                        config,
                        converter.fromConfig(
                                field.getType(),
                                root.get(path),
                                (field.getGenericType() instanceof ParameterizedType) ?
                                        (ParameterizedType) field.getGenericType() : null
                        )
                );
                return;
            }
        }

		/*
		If we're trying to assign a value to a static variable
		then assure there's the "PreserveStatic" annotation on there!
		 */
        if (Modifier.isStatic(field.getModifiers())) {
            if (!field.isAnnotationPresent(PreserveStatic.class)) return;

            final PreserveStatic staticConfig = field.getAnnotation(PreserveStatic.class);
            if (!staticConfig.value()) return;

            field.set(null, root.get(path));
            return;
        }

        field.set(config, root.get(path));
    }

    public void toConfig(final YamlConfig config, final Field field, final ConfigSection root, final String path) throws
            Exception {
        final Object obj = field.get(config);

        Converter converter;

        if (obj != null) {
            converter = this.getConverter(obj.getClass());

            if (converter != null) {
                root.set(
                        path,
                        converter.toConfig(
                                obj.getClass(),
                                obj,
                                (field.getGenericType() instanceof ParameterizedType) ?
                                        (ParameterizedType) field.getGenericType() : null
                        )
                );
                return;
            } else {
                converter = this.getConverter(field.getType());
                if (converter != null) {
                    root.set(
                            path,
                            converter.toConfig(
                                    field.getType(),
                                    obj,
                                    (field.getGenericType() instanceof ParameterizedType) ?
                                            (ParameterizedType) field.getGenericType() : null
                            )
                    );
                    return;
                }
            }
        }

        root.set(path, obj);
    }

    public List<Class> getCustomConverters() {
        return new ArrayList<>(this.customConverters);
    }

    public void addCustomConverter(final Class addConverter) throws InvalidConverterException {
        this.addConverter(addConverter);
        this.customConverters.add(addConverter);
    }
}
