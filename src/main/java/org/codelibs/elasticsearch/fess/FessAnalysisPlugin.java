package org.codelibs.elasticsearch.fess;

import static java.util.Collections.singletonMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.elasticsearch.fess.index.analysis.JapaneseBaseFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapanesePartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseReadingFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.KoreanTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.ReloadableJapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchRequestParsers;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

public class FessAnalysisPlugin extends Plugin implements AnalysisPlugin {

    private PluginComponent pluginComponent = new PluginComponent();

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        Collection<Class<? extends LifecycleComponent>> services = new ArrayList<>();
        services.add(FessAnalysisService.class);
        return services;
    }

    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
            ResourceWatcherService resourceWatcherService, ScriptService scriptService, SearchRequestParsers searchRequestParsers,
            NamedXContentRegistry xContentRegistry) {
        Collection<Object> components = new ArrayList<>();
        components.add(pluginComponent);
        return components;
    }

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        return singletonMap("fess_japanese_iteration_mark", new AnalysisProvider<CharFilterFactory>() {
            @Override
            public CharFilterFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapaneseIterationMarkCharFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService());
            }
        });
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_baseform", new AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapaneseBaseFormFilterFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        extra.put("fess_japanese_part_of_speech", new AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapanesePartOfSpeechFilterFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        extra.put("fess_japanese_readingform", new AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapaneseReadingFormFilterFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        extra.put("fess_japanese_stemmer", new AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapaneseKatakanaStemmerFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_tokenizer", new AnalysisProvider<TokenizerFactory>() {
            @Override
            public TokenizerFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new JapaneseTokenizerFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        extra.put("fess_japanese_reloadable_tokenizer", new AnalysisProvider<TokenizerFactory>() {
            @Override
            public TokenizerFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new ReloadableJapaneseTokenizerFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        extra.put("fess_korean_tokenizer", new AnalysisProvider<TokenizerFactory>() {
            @Override
            public TokenizerFactory get(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
                return new KoreanTokenizerFactory(indexSettings, env, name, settings, pluginComponent.getFessAnalysisService());
            }
        });
        return extra;
    }

    public static class PluginComponent {
        private FessAnalysisService fessAnalysisService;

        public FessAnalysisService getFessAnalysisService() {
            return fessAnalysisService;
        }

        public void setFessAnalysisService(FessAnalysisService fessAnalysisService) {
            this.fessAnalysisService = fessAnalysisService;
        }
    }

}
