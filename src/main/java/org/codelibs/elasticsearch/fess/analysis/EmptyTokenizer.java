package org.codelibs.elasticsearch.fess.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;

public class EmptyTokenizer extends Tokenizer {

    @Override
    public final boolean incrementToken() throws IOException {
        return false;
    }

}
