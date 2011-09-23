/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.config;

import simple.server.core.engine.SimpleRPZone;
import java.util.Map;

/**
 * Zone post-configuration.
 */
public interface ZoneConfigurator {

    /**
     * Configure a zone.
     *
     * @param zone
     *            The zone to be configured.
     * @param attributes
     *            Configuration attributes.
     */
    void configureZone(SimpleRPZone zone, Map<String, String> attributes);
}
