/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.elasticsearch.fess.service;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.core.Tuple;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.plugins.PluginsService;

public class FessAnalysisService extends AbstractLifecycleComponent {
    private static final Logger logger = LogManager.getLogger(FessAnalysisService.class);

    private PluginsService pluginsService;

    private List<Tuple<PluginInfo, Plugin>> plugins;

    @Override
    protected void doStart() throws ElasticsearchException {
        logger.debug("Starting FessAnalysisService");

        plugins = loadPlugins();
    }

    @Override
    protected void doStop() throws ElasticsearchException {
        logger.debug("Stopping FessAnalysisService");
    }

    @Override
    protected void doClose() throws ElasticsearchException {
        logger.debug("Closing FessAnalysisService");
    }

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

    public void setPluginsService(final PluginsService pluginsService) {
        this.pluginsService = pluginsService;
    }

}
