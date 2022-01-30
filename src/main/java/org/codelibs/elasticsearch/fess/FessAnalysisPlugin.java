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
package org.codelibs.elasticsearch.fess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.codelibs.elasticsearch.fess.action.AnalysisAction;
import org.codelibs.elasticsearch.fess.action.TransportAnalysisAction;
import org.codelibs.elasticsearch.fess.index.analysis.ChineseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseBaseFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapanesePartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseReadingFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.KoreanTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.ReloadableJapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.TraditionalChineseConvertCharFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.VietnameseTokenizerFactory;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.repositories.RepositoriesService;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;
import org.elasticsearch.xcontent.NamedXContentRegistry;

public class FessAnalysisPlugin extends Plugin implements ActionPlugin, AnalysisPlugin {

    private FessAnalysisService service;

    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        return Arrays.asList(//
                new ActionHandler<>(AnalysisAction.INSTANCE, TransportAnalysisAction.class));
    }

    @Override
    public Collection<Object> createComponents(final Client client, final ClusterService clusterService, final ThreadPool threadPool,
            final ResourceWatcherService resourceWatcherService, final ScriptService scriptService,
            final NamedXContentRegistry xContentRegistry, final Environment environment, final NodeEnvironment nodeEnvironment,
            final NamedWriteableRegistry namedWriteableRegistry, final IndexNameExpressionResolver indexNameExpressionResolver,
            final Supplier<RepositoriesService> repositoriesServiceSupplier) {
        final Collection<Object> components = new ArrayList<>();
        service = new FessAnalysisService();
        components.add(service);
        return components;
    }

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        final Map<String, AnalysisProvider<CharFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_iteration_mark", (indexSettings, env, name,
                settings) -> new JapaneseIterationMarkCharFilterFactory(indexSettings, env, name, settings, service));
        extra.put("fess_traditional_chinese_convert", (indexSettings, env, name,
                settings) -> new TraditionalChineseConvertCharFilterFactory(indexSettings, env, name, settings, service));
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_baseform",
                (indexSettings, env, name, settings) -> new JapaneseBaseFormFilterFactory(indexSettings, env, name, settings, service));
        extra.put("fess_japanese_part_of_speech",
                (indexSettings, env, name, settings) -> new JapanesePartOfSpeechFilterFactory(indexSettings, env, name, settings, service));
        extra.put("fess_japanese_readingform",
                (indexSettings, env, name, settings) -> new JapaneseReadingFormFilterFactory(indexSettings, env, name, settings, service));
        extra.put("fess_japanese_stemmer",
                (indexSettings, env, name, settings) -> new JapaneseKatakanaStemmerFactory(indexSettings, env, name, settings, service));
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        final Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_tokenizer",
                (indexSettings, env, name, settings) -> new JapaneseTokenizerFactory(indexSettings, env, name, settings, service));
        extra.put("fess_japanese_reloadable_tokenizer", (indexSettings, env, name,
                settings) -> new ReloadableJapaneseTokenizerFactory(indexSettings, env, name, settings, service));
        extra.put("fess_korean_tokenizer",
                (indexSettings, env, name, settings) -> new KoreanTokenizerFactory(indexSettings, env, name, settings, service));
        extra.put("fess_vietnamese_tokenizer",
                (indexSettings, env, name, settings) -> new VietnameseTokenizerFactory(indexSettings, env, name, settings, service));
        extra.put("fess_simplified_chinese_tokenizer",
                (indexSettings, env, name, settings) -> new ChineseTokenizerFactory(indexSettings, env, name, settings, service));
        return extra;
    }

}
