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

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.elasticsearch.fess.analysis.EmptyTokenizer;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

import java.lang.reflect.Constructor;

public class JapanesePosConcatenationFilterFactory extends AbstractTokenFilterFactory {

    private static final String POS_CONCATENATION_FILTER_FACTORY =
            "org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.PosConcatenationFilterFactory";

    private TokenFilterFactory tokenFilterFactory = null;

    @Inject
    public JapanesePosConcatenationFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env, FessAnalysisService fessAnalysisService) {
        super(index, indexSettingsService.getSettings(), name, settings);

        Class<?> tokenFilterFactoryClass = fessAnalysisService.loadClass(POS_CONCATENATION_FILTER_FACTORY);
        if (logger.isInfoEnabled()) {
            logger.info("{} is not found.", POS_CONCATENATION_FILTER_FACTORY);
        }
        if (tokenFilterFactoryClass != null) {
            try {
                final Constructor<?> constructor = tokenFilterFactoryClass.getConstructor(Index.class, IndexSettingsService.class,
                        Environment.class, String.class, Settings.class);
                tokenFilterFactory = (TokenFilterFactory) constructor.newInstance(index, indexSettingsService, env, name, settings);
            } catch (final Exception e) {
                throw new ElasticsearchException("Failed to load " + POS_CONCATENATION_FILTER_FACTORY, e);
            }
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (tokenFilterFactory != null) {
            return tokenFilterFactory.create(tokenStream);
        }
        return new EmptyTokenizer();
    }

}
