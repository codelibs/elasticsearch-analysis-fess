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

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class JapaneseIterationMarkCharFilterFactory extends AbstractCharFilterFactory {

    private static final String KUROMOJI_ITERATION_MARK_CHAR_FILTER_FACTORY =
            "org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiIterationMarkCharFilterFactory";

    private CharFilterFactory charFilterFactory = null;

    @Inject
    public JapaneseIterationMarkCharFilterFactory(final Index index, final IndexSettingsService indexSettingsService,
            @Assisted final String name, @Assisted final Settings settings, final FessAnalysisService fessAnalysisService) {
        super(index, indexSettingsService.getSettings(), name);

        final Class<?> charFilterFactoryClass = fessAnalysisService.loadClass(KUROMOJI_ITERATION_MARK_CHAR_FILTER_FACTORY);
        if (logger.isInfoEnabled()) {
            logger.info("{} is not found.", KUROMOJI_ITERATION_MARK_CHAR_FILTER_FACTORY);
        }
        if (charFilterFactoryClass != null) {
            charFilterFactory = AccessController.doPrivileged(new PrivilegedAction<CharFilterFactory>() {
                @Override
                public CharFilterFactory run() {
                    try {
                        final Constructor<?> constructor = charFilterFactoryClass.getConstructor(Index.class, IndexSettingsService.class,
                                String.class, Settings.class);
                        return (CharFilterFactory) constructor.newInstance(index, indexSettingsService, name, settings);
                    } catch (final Exception e) {
                        throw new ElasticsearchException("Failed to load " + KUROMOJI_ITERATION_MARK_CHAR_FILTER_FACTORY, e);
                    }
                }
            });
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