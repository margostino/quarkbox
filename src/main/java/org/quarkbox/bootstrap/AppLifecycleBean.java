package org.quarkbox.bootstrap;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import static java.lang.String.format;


@ApplicationScoped
public class AppLifecycleBean {

    void onStart(@Observes StartupEvent ev) {
        Log.info(format("Quarkbox is starting with profile `%s`", ProfileManager.getActiveProfile()));
    }

    void onStop(@Observes ShutdownEvent ev) {
        Log.info("Quarkbox is stopping...");
    }

}