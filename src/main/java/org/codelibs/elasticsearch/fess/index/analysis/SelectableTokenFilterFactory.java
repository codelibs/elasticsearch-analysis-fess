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

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;

@SuppressWarnings("removal")
public abstract class SelectableTokenFilterFactory extends AbstractTokenFilterFactory {

    protected TokenFilterFactory tokenFilterFactory = null;

    protected SelectableTokenFilterFactory(final IndexSettings indexSettings, final Environment env, final String name,
            final Settings settings, final FessAnalysisService fessAnalysisService, final String[] factories) {
        super(indexSettings, name, settings);

        for (final String factoryClass : factories) {
            final Class<?> TokenFilterFactoryClass = fessAnalysisService.loadClass(factoryClass);
            if (TokenFilterFactoryClass != null) {
                logger.info("[{}] {} is found.", name, factoryClass);
                final PrivilegedAction<TokenFilterFactory> privilegedAction = (PrivilegedAction<TokenFilterFactory>) () -> {
                    try {
                        final Constructor<?> constructor = TokenFilterFactoryClass.getConstructor(IndexSettings.class, Environment.class,
                                String.class, Settings.class);
                        return (TokenFilterFactory) constructor.newInstance(indexSettings, env, name, settings);
                    } catch (final Exception e) {
                        throw new ElasticsearchException("Failed to load " + factoryClass, e);
                    }
                };
                tokenFilterFactory = AccessController.doPrivileged(privilegedAction);
                break;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("{} is not found.", factoryClass);
            }
        }
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        if (tokenFilterFactory != null) {
            return tokenFilterFactory.create(tokenStream);
        }
        return tokenStream;
    }
}