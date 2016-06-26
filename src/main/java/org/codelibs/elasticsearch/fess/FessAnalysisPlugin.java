package org.codelibs.elasticsearch.fess;

import java.util.Collection;

import org.codelibs.elasticsearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.elasticsearch.fess.index.analysis.JapaneseTokenizerFactory;
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

    public void onModule(AnalysisModule module) {
        //        module.addCharFilter("kuromoji_neologd_iteration_mark", KuromojiIterationMarkCharFilterFactory.class);
        //        module.addAnalyzer("kuromoji_neologd", KuromojiAnalyzerProvider.class);
        module.addTokenizer("fess_japanese_tokenizer", JapaneseTokenizerFactory.class);
        //        module.addTokenFilter("kuromoji_neologd_baseform", KuromojiBaseFormFilterFactory.class);
        //        module.addTokenFilter("kuromoji_neologd_part_of_speech", KuromojiPartOfSpeechFilterFactory.class);
        //        module.addTokenFilter("kuromoji_neologd_readingform", KuromojiReadingFormFilterFactory.class);
        module.addTokenFilter("fess_japanese_stemmer", JapaneseKatakanaStemmerFactory.class);

        //        module.addTokenizer("reloadable_kuromoji_neologd_tokenizer", ReloadableKuromojiTokenizerFactory.class);
        //        module.addTokenizer("reloadable_kuromoji_neologd", ReloadableKuromojiTokenizerFactory.class);

        //        module.addTokenFilter("kuromoji_neologd_pos_concat", PosConcatenationFilterFactory.class);
    }
}
