package project.avery.configuration;

import lombok.Getter;
import project.avery.utilities.config.Config;

import java.io.File;

/**
 * Created by Pascal K. on 12.07.2023.
 */
@Getter
public class AccountConfig extends Config {

    private final String accountToken = "/";

    private final String accountDisplayName = "Avery";

    public AccountConfig(final File file) {
        this.CONFIG_FILE = file;
    }

}
