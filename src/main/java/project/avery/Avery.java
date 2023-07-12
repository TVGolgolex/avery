package project.avery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import project.avery.bootstrap.Bootstrap;
import project.avery.jda.JDAInitializer;
import project.avery.utilities.AnsiColor;

import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

/**
 * Created by Pascal K. on 12.07.2023.
 */
@Accessors(fluent = true)
@Getter
public class Avery {

    @Getter(AccessLevel.PUBLIC)
    private static Avery instance;

    private final Bootstrap bootstrap;

    private final Logger logger;

    private final JDA jda;

    private final Guild guild;

    private String guildId;

    public Avery(final Bootstrap bootstrap, final Logger logger) {
        this.bootstrap = bootstrap;
        this.logger = logger;
        instance = this;

        this.logger.log(
                INFO,
                AnsiColor.BLUE.code() + "Avery » " + AnsiColor.RESET.code() + "Bootstrap client: " + bootstrap.name() + AnsiColor.RESET.code()
        );

        this.logger.log(
                INFO,
                AnsiColor.BLUE.code() + "Avery » " + AnsiColor.GREEN.code() + "discord instance will be starting..." + AnsiColor.RESET.code()
        );

        this.jda = new JDAInitializer(this).initialize();
        this.guild = null;

    }
}
