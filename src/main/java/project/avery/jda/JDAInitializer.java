package project.avery.jda;

import net.dv8tion.jda.api.JDA;
import project.avery.Avery;
import project.avery.utilities.AnsiColor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pascal K. on 12.07.2023.
 */
public class JDAInitializer {

    private final Logger logger = Logger.getLogger("JDAInitializer");

    private final Avery avery;

    public JDAInitializer(final Avery avery) {
        this.avery = avery;
    }

    public JDA initialize() {
        this.logger.log(
                Level.INFO,
                AnsiColor.BLUE.code() + "Avery » " + AnsiColor.RESET.code() + "Starting JDA initialization" + AnsiColor.RESET.code()
        );

        try {

        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
