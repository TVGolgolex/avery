package project.avery.bootstrap;

import project.avery.Avery;

import java.util.logging.Logger;

/**
 * Created by Pascal K. on 12.07.2023.
 */
public class MainClassBootstrap {

    public static void main(final String[] args) {
        new Avery(Bootstrap.MAIN_CLASS, Logger.getLogger("Avery"));
    }

}
