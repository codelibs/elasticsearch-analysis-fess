package org.codelibs.elasticsearch.fess.service;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.fess.FessAnalysisPlugin;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.plugins.PluginsService;

public class FessAnalysisService extends AbstractLifecycleComponent {
    private static final Logger logger = LogManager.getLogger(FessAnalysisService.class);

    private final PluginsService pluginsService;

    private List<Tuple<PluginInfo, Plugin>> plugins;

    @Inject
    public FessAnalysisService(final Settings settings, final PluginsService pluginsService,
            final FessAnalysisPlugin.PluginComponent pluginComponent) {
        super(settings);
        this.pluginsService = pluginsService;
        pluginComponent.setFessAnalysisService(this);
    }

    @Override
    protected void doStart() throws ElasticsearchException {
        logger.debug("Starting FessAnalysisService");

        plugins = loadPlugins();
    }

    @SuppressWarnings("unchecked")
    private List<Tuple<PluginInfo, Plugin>> loadPlugins() {
        return AccessController.doPrivileged((PrivilegedAction<List<Tuple<PluginInfo, Plugin>>>) () -> {
            try {
                final Field pluginsField = pluginsService.getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                return (List<Tuple<PluginInfo, Plugin>>) pluginsField.get(pluginsService);
            } catch (final Exception e) {
                throw new ElasticsearchException("Failed to access plugins in PluginsService.", e);
            }
        });
    }

    @Override
    protected void doStop() throws ElasticsearchException {
        logger.debug("Stopping FessAnalysisService");
    }

    @Override
    protected void doClose() throws ElasticsearchException {
        logger.debug("Closing FessAnalysisService");
    }

    public Class<?> loadClass(final String className) {
        return AccessController.doPrivileged((PrivilegedAction<Class<?>>) () -> {
            for (final Tuple<PluginInfo, Plugin> p : plugins) {
                final Plugin plugin = p.v2();
                try {
                    return plugin.getClass().getClassLoader().loadClass(className);
                } catch (final ClassNotFoundException e) {
                    // ignore
                }
            }
            return null;
        });
    }

}
