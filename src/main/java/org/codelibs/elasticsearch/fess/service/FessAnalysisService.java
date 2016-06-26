package org.codelibs.elasticsearch.fess.service;

import java.lang.reflect.Field;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.plugins.PluginsService;

public class FessAnalysisService extends AbstractLifecycleComponent<FessAnalysisService> {

    private PluginsService pluginsService;

    private List<Tuple<PluginInfo, Plugin>> plugins;

    @Inject
    public FessAnalysisService(final Settings settings, final PluginsService pluginsService) {
        super(settings);
        this.pluginsService = pluginsService;
    }

    @Override
    protected void doStart() throws ElasticsearchException {
        logger.info("START FessAnalysisService");

        plugins = loadPlugins();
    }

    @SuppressWarnings("unchecked")
    private List<Tuple<PluginInfo, Plugin>> loadPlugins() {
        try {
            final Field pluginsField = pluginsService.getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
            return (List<Tuple<PluginInfo, Plugin>>) pluginsField.get(pluginsService);
        } catch (Exception e) {
            throw new ElasticsearchException("Failed to access plugins in PluginsService.", e);
        }
    }

    @Override
    protected void doStop() throws ElasticsearchException {
        logger.info("Stopping FessAnalysisService");
    }

    @Override
    protected void doClose() throws ElasticsearchException {
        logger.info("Closing FessAnalysisService");
    }

    public Class<?> loadClass(String className) {
        for (Tuple<PluginInfo, Plugin> p : plugins) {
            final Plugin plugin = p.v2();
            try {
                return plugin.getClass().getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return null;
    }

}
