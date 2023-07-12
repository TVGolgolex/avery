package project.avery.utilities.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BaseConfigMapper extends BaseConfig {
    private final transient Yaml yaml;
    protected transient ConfigSection root;
    private final transient Map<String, ArrayList<String>> comments = new LinkedHashMap<>();
    private final transient Representer yamlRepresenter = new Representer(new DumperOptions());

    protected BaseConfigMapper() {
        final DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(new CustomClassLoaderConstructor(
                BaseConfigMapper.class.getClassLoader(),
                new LoaderOptions()
        ), this.yamlRepresenter, yamlOptions);

        /*
        Configure the settings for serializing via the annotations present.
         */
        this.configureFromSerializeOptionsAnnotation();
    }

    protected void loadFromYaml() throws InvalidConfigurationException {
        this.root = new ConfigSection();

        try (final InputStreamReader fileReader = new InputStreamReader(
                new FileInputStream(this.CONFIG_FILE),
                StandardCharsets.UTF_8
        )) {
            final Object object = this.yaml.load(fileReader);

            if (object != null) this.convertMapsToSections((Map<?, ?>) object, this.root);
        } catch (final IOException | ClassCastException | YAMLException e) {
            throw new InvalidConfigurationException("Could not load YML", e);
        }
    }

    private void convertMapsToSections(final Map<?, ?> input, final ConfigSection section) {
        if (input == null) return;

        for (final Map.Entry<?, ?> entry : input.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();

            if (value instanceof Map) this.convertMapsToSections((Map<?, ?>) value, section.create(key));
            else
                section.set(key, value, false);
        }
    }

    protected void saveToYaml() throws InvalidConfigurationException {
        try (final OutputStreamWriter fileWriter = new OutputStreamWriter(
                new FileOutputStream(this.CONFIG_FILE),
                StandardCharsets.UTF_8
        )) {
            if (this.CONFIG_HEADER != null) {
                for (final String line : this.CONFIG_HEADER) fileWriter.write("# " + line + "\n");

                fileWriter.write("\n");
            }

            int depth = 0;
            ArrayList<String> keyChain = new ArrayList<>();
            final String yamlString = this.yaml.dump(this.root.getValues(true));
            final StringBuilder writeLines = new StringBuilder();
            for (final String line : yamlString.split("\n")) {
                if (line.startsWith(new String(new char[depth]).replace("\0", " "))) {
                    keyChain.add(line.split(":")[0].trim());
                    depth = depth + 2;
                } else {
                    if (line.startsWith(new String(new char[depth - 2]).replace("\0", " ")))
                        keyChain.remove(keyChain.size() - 1);
                    else {
                        //Check how much spaces are infront of the line
                        int spaces = 0;
                        for (int i = 0; i < line.length(); i++)
                            if (line.charAt(i) == ' ') spaces++;
                            else break;

                        depth = spaces;

                        if (spaces == 0) {
                            keyChain = new ArrayList<>();
                            depth = 2;
                        } else {
                            final ArrayList<String> temp = new ArrayList<>();
                            int index = 0;
                            for (int i = 0; i < spaces; i = i + 2, index++) temp.add(keyChain.get(index));

                            keyChain = temp;

                            depth = depth + 2;
                        }
                    }

                    keyChain.add(line.split(":")[0].trim());
                }

                final String search;
                if (keyChain.size() > 0) search = join(keyChain, ".");
                else search = "";


                if (this.comments.containsKey(search)) for (final String comment : this.comments.get(search)) {
                    writeLines.append(new String(new char[depth - 2]).replace("\0", " "));
                    writeLines.append("# ");
                    writeLines.append(comment);
                    writeLines.append("\n");
                }

                writeLines.append(line);
                writeLines.append("\n");
            }

            fileWriter.write(writeLines.toString());
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Could not save YML", e);
        }
    }

    private static String join(final List<String> list, final String conjunction) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String item : list) {
            if (first) first = false;
            else sb.append(conjunction);
            sb.append(item);
        }

        return sb.toString();
    }

    public void addComment(final String key, final String value) {
        if (!this.comments.containsKey(key)) this.comments.put(key, new ArrayList<>());

        this.comments.get(key).add(value);
    }

    public void clearKeyComments(final String key) {
        if (!this.comments.containsKey(key)) return;
        this.comments.get(key).clear();
    }

    public List<String> listKeyComments(final String key) {
        if (!this.comments.containsKey(key)) return Collections.emptyList();
        return this.comments.get(key);
    }

    public void clearComments() {
        this.comments.clear();
    }
}
