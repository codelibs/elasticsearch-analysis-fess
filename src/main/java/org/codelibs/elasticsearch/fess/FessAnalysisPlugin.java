package org.codelibs.elasticsearch.fess;

import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.elasticsearch.fess.index.analysis.ChineseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseBaseFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapanesePartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseReadingFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.KoreanTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.ReloadableJapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.VietnameseTokenizerFactory;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

public class FessAnalysisPlugin extends Plugin implements AnalysisPlugin {

    private final PluginComponent pluginComponent = new PluginComponent();

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        final Collection<Class<? extends LifecycleComponent>> services = new ArrayList<>();
        services.add(FessAnalysisService.class);
        return services;
    }

    @Override
    public Collection<Object> createComponents(final Client client, final ClusterService clusterService, final ThreadPool threadPool,
            final ResourceWatcherService resourceWatcherService, final ScriptService scriptService,
            final NamedXContentRegistry xContentRegistry) {
        final Collection<Object> components = new ArrayList<>();
        components.add(pluginComponent);
        return components;
    }

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        return singletonMap("fess_japanese_iteration_mark",
                (indexSettings, env, name, settings) -> new JapaneseIterationMarkCharFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_baseform", (indexSettings, env, name, settings) -> new JapaneseBaseFormFilterFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_part_of_speech",
                (indexSettings, env, name, settings) -> new JapanesePartOfSpeechFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_readingform", (indexSettings, env, name, settings) -> new JapaneseReadingFormFilterFactory(indexSettings,
                env, name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_stemmer", (indexSettings, env, name, settings) -> new JapaneseKatakanaStemmerFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        final Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_tokenizer", (indexSettings, env, name, settings) -> new JapaneseTokenizerFactory(indexSettings, env, name,
                settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_reloadable_tokenizer",
                (indexSettings, env, name, settings) -> new ReloadableJapaneseTokenizerFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        extra.put("fess_korean_tokenizer", (indexSettings, env, name, settings) -> new KoreanTokenizerFactory(indexSettings, env, name,
                settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_vietnamese_tokenizer", (indexSettings, env, name, settings) -> new VietnameseTokenizerFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_simplified_chinese_tokenizer", (indexSettings, env, name, settings) -> new ChineseTokenizerFactory(indexSettings,
                env, name, settings, pluginComponent.getFessAnalysisService()));
        return extra;
    }

    public static class PluginComponent {
        private FessAnalysisService fessAnalysisService;

        public FessAnalysisService getFessAnalysisService() {
            return fessAnalysisService;
        }

        public void setFessAnalysisService(final FessAnalysisService fessAnalysisService) {
            this.fessAnalysisService = fessAnalysisService;
        }
    }

}
