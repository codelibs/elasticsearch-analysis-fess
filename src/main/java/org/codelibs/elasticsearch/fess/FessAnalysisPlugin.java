package org.codelibs.elasticsearch.fess;

import java.util.Collection;

import org.codelibs.elasticsearch.fess.index.analysis.JapaneseBaseFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapanesePartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapanesePosConcatenationFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseReadingFormFilterFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.KoreanTokenizerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.ReloadableJapaneseTokenizerFactory;
import org.codelibs.elasticsearch.fess.module.FessAnalysisModule;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

import com.google.common.collect.Lists;

public class FessAnalysisPlugin extends Plugin {
    @Override
    public String name() {
        return "analysis-fess";
    }

    @Override
    public String description() {
        return "This plugin provides an analysis library for Fess.";
    }

    @Override
    public Collection<Module> nodeModules() {
        final Collection<Module> modules = Lists.newArrayList();
        modules.add(new FessAnalysisModule());
        return modules;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Class<? extends LifecycleComponent>> nodeServices() {
        final Collection<Class<? extends LifecycleComponent>> services = Lists.newArrayList();
        services.add(FessAnalysisService.class);
        return services;
    }

    public void onModule(final AnalysisModule module) {
        module.addCharFilter("fess_japanese_iteration_mark", JapaneseIterationMarkCharFilterFactory.class);
        //        module.addAnalyzer("kuromoji_neologd", KuromojiAnalyzerProvider.class);
        module.addTokenizer("fess_japanese_tokenizer", JapaneseTokenizerFactory.class);
        module.addTokenFilter("fess_japanese_baseform", JapaneseBaseFormFilterFactory.class);
        module.addTokenFilter("fess_japanese_part_of_speech", JapanesePartOfSpeechFilterFactory.class);
        module.addTokenFilter("fess_japanese_readingform", JapaneseReadingFormFilterFactory.class);
        module.addTokenFilter("fess_japanese_stemmer", JapaneseKatakanaStemmerFactory.class);
        module.addTokenizer("fess_japanese_reloadable_tokenizer", ReloadableJapaneseTokenizerFactory.class);
        module.addTokenFilter("fess_japanese_pos_concat", JapanesePosConcatenationFilterFactory.class);

        module.addTokenizer("fess_korean_tokenizer", KoreanTokenizerFactory.class);
    }
}
