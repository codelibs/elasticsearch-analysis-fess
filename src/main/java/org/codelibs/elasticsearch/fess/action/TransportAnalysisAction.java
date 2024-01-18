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
package org.codelibs.elasticsearch.fess.action;

import org.codelibs.elasticsearch.fess.service.FessAnalysisService;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;

public class TransportAnalysisAction extends HandledTransportAction<AnalysisRequest, AnalysisResponse> {

    @Inject
    public TransportAnalysisAction(final TransportService transportService, final ActionFilters actionFilters,
            final PluginsService pluginsService, final FessAnalysisService fessAnalysisService) {
        super(AnalysisAction.NAME, transportService, actionFilters, AnalysisRequest::new, EsExecutors.DIRECT_EXECUTOR_SERVICE);
        fessAnalysisService.setPluginsService(pluginsService);
    }

    @Override
    protected void doExecute(final Task task, final AnalysisRequest request, final ActionListener<AnalysisResponse> listener) {
        // nothing
        listener.onResponse(new AnalysisResponse(true));
    }
}
