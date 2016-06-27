package org.codelibs.elasticsearch.fess.analysis;

import org.apache.lucene.analysis.CharFilter;

import java.io.IOException;
import java.io.Reader;

public class EmptyCharFilter extends CharFilter {

    public EmptyCharFilter(Reader input) {
        super(input);
    }

    @Override
    protected int correct(int i) {
        return 0;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return 0;
    }
}
