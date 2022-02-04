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
package org.codelibs.elasticsearch.fess.index.analysis.chinese;

import org.codelibs.elasticsearch.fess.index.analysis.SelectableTokenFilterFactory;
import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class ChineseStopTokenFilterFactory extends SelectableTokenFilterFactory {

    private static final String[] FACTORIES = { //
            "org.elasticsearch.plugin.analysis.smartcn.SmartChineseStopTokenFilterFactory" };

    public ChineseStopTokenFilterFactory(final IndexSettings indexSettings, final Environment env, final String name,
            final Settings settings, final FessAnalysisService fessAnalysisService) {
        super(indexSettings, env, name, settings, fessAnalysisService, FACTORIES);
    }
}