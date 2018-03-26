/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.plugin.use.resolve.service.internal;

import org.gradle.api.GradleException;
import org.gradle.plugin.management.internal.PluginRequestInternal;

import java.io.IOException;

public class OfflinePluginResolutionServiceClient implements PluginResolutionServiceClient {
    public Response<PluginUseMetaData> queryPluginMetadata(String portalUrl, boolean shouldValidate, PluginRequestInternal pluginRequest) {
        throw new GradleException(String.format("Plugin cannot be resolved from %s because Gradle is running in offline mode", portalUrl));
    }

    public Response<ClientStatus> queryClientStatus(String portalUrl, boolean shouldValidate, String checksum) {
        throw new GradleException(String.format("Client status cannot be resolved from %s because Gradle is running in offline mode", portalUrl));
    }

    public void close() throws IOException {

    }
}
