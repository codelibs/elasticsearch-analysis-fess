package org.codelibs.elasticsearch.fess.index.analysis;

import java.lang.reflect.Constructor;

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

public class JapaneseKatakanaStemmerFactory extends AbstractTokenFilterFactory {

    private static final String KUROMOJI_KATAKANA_STEMMER_FACTORY =
            "org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiKatakanaStemmerFactory";

    private TokenFilterFactory tokenFilterFactory;

    @Inject
    public JapaneseKatakanaStemmerFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, final FessAnalysisService fessAnalysisService) {
        super(index, indexSettingsService.getSettings(), name, settings);

        Class<?> tokenizerFactoryClass = fessAnalysisService.loadClass(KUROMOJI_KATAKANA_STEMMER_FACTORY);
        if (logger.isInfoEnabled()) {
            logger.info("{} is not found.", KUROMOJI_KATAKANA_STEMMER_FACTORY);
        }
        if (tokenizerFactoryClass != null) {
            try {
                final Constructor<?> constructor =
                        tokenizerFactoryClass.getConstructor(Index.class, IndexSettingsService.class, String.class, Settings.class);
                tokenFilterFactory = (TokenFilterFactory) constructor.newInstance(index, indexSettingsService, name, settings);
            } catch (final Exception e) {
                throw new ElasticsearchException("Failed to load " + KUROMOJI_KATAKANA_STEMMER_FACTORY, e);
            }
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (tokenFilterFactory != null) {
            return tokenFilterFactory.create(tokenStream);
        }
        return tokenStream;
    }

}
