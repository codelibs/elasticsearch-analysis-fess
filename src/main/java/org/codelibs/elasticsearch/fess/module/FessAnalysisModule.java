package org.codelibs.elasticsearch.fess.module;

import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.common.inject.AbstractModule;

public class FessAnalysisModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FessAnalysisService.class).asEagerSingleton();
    }
}
