package project.avery.configuration;

import lombok.Getter;
import lombok.experimental.Accessors;
import project.avery.utilities.config.InvalidConfigurationException;

/**
 * Created by Pascal K. on 12.07.2023.
 */
@Accessors(fluent = true)
@Getter
public class Configurations {

    private final AccountConfig accountConfig;

    public Configurations() {
        this.accountConfig = new AccountConfig();

        try {
            this.accountConfig.init();
        } catch (final InvalidConfigurationException exception) {
            exception.printStackTrace();
        }

    }
}
