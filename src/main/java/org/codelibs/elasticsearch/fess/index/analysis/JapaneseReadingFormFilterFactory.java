/*
 * Copyright 2009-2016 the CodeLibs Project and the Others.
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
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class JapaneseReadingFormFilterFactory extends AbstractTokenFilterFactory {

    private static final String[] FACTORIES = new String[] { //
            "org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiReadingFormFilterFactory",
            "org.codelibs.elasticsearch.ja.analysis.KuromojiReadingFormFilterFactory" };

    private TokenFilterFactory tokenFilterFactory = null;

    @Inject
    public JapaneseReadingFormFilterFactory(final Index index, final IndexSettingsService indexSettingsService, @Assisted final String name,
            @Assisted final Settings settings, final FessAnalysisService fessAnalysisService) {
        super(index, indexSettingsService.getSettings(), name, settings);

        for (final String factoryClass : FACTORIES) {
            final Class<?> tokenFilterFactoryClass = fessAnalysisService.loadClass(factoryClass);
            if (tokenFilterFactoryClass != null) {
                if (logger.isInfoEnabled()) {
                    logger.info("{} is found.", factoryClass);
                }
                tokenFilterFactory = AccessController.doPrivileged(new PrivilegedAction<TokenFilterFactory>() {
                    @Override
                    public TokenFilterFactory run() {
                        try {
                            final Constructor<?> constructor = tokenFilterFactoryClass.getConstructor(Index.class,
                                    IndexSettingsService.class, String.class, Settings.class);
                            return (TokenFilterFactory) constructor.newInstance(index, indexSettingsService, name, settings);
                        } catch (final Exception e) {
                            throw new ElasticsearchException("Failed to load " + factoryClass, e);
                        }

                    }
                });
            } else if (logger.isInfoEnabled()) {
                logger.info("{} is not found.", factoryClass);
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