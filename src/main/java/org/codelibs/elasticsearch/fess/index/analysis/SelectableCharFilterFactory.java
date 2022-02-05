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
package org.codelibs.elasticsearch.fess.index.analysis;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.analysis.CharFilterFactory;

@SuppressWarnings("removal")
public class SelectableCharFilterFactory extends AbstractCharFilterFactory {

    protected CharFilterFactory charFilterFactory = null;

    protected SelectableCharFilterFactory(final IndexSettings indexSettings, final Environment env, final String name,
            final Settings settings, final FessAnalysisService fessAnalysisService, final String[] factories) {
        super(indexSettings, name);

        for (final String factoryClass : factories) {
            final Class<?> charFilterFactoryClass = fessAnalysisService.loadClass(factoryClass);
            if (charFilterFactoryClass != null) {
                logger.info("[{}] {} is found.", name, factoryClass);
                final PrivilegedAction<CharFilterFactory> privilegedAction = (PrivilegedAction<CharFilterFactory>) () -> {
                    try {
                        final Constructor<?> constructor =
                                charFilterFactoryClass.getConstructor(IndexSettings.class, Environment.class, String.class, Settings.class);
                        return (CharFilterFactory) constructor.newInstance(indexSettings, env, name, settings);
                    } catch (final Exception e) {
                        throw new ElasticsearchException("Failed to load " + factoryClass, e);
                    }
                };
                charFilterFactory = AccessController.doPrivileged(privilegedAction);
                break;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] {} is not found.", name, factoryClass);
            }
        }
    }

    @Override
    public Reader create(final Reader reader) {
        if (charFilterFactory != null) {
            return charFilterFactory.create(reader);
        }
        return reader;
    }
}