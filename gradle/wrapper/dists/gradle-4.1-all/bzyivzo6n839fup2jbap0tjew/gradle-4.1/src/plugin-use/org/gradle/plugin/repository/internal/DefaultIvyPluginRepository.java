/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.plugin.repository.internal;

import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.AuthenticationContainer;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.repositories.RepositoryLayout;
import org.gradle.api.credentials.Credentials;
import org.gradle.api.internal.artifacts.DependencyResolutionServices;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionSelectorScheme;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.artifacts.repositories.AuthenticationSupportedInternal;
import org.gradle.plugin.repository.IvyPluginRepository;

class DefaultIvyPluginRepository extends AbstractArtifactPluginRepository implements IvyPluginRepository {
    private static final String IVY = "ivy";

    private String layoutName;
    private Action<? extends RepositoryLayout> layoutAction;
    private String artifactPattern;
    private String ivyPattern;

    public DefaultIvyPluginRepository(
        FileResolver fileResolver, DependencyResolutionServices dependencyResolutionServices,
        VersionSelectorScheme versionSelectorScheme, AuthenticationSupportedInternal delegate) {
        super(IVY, fileResolver, dependencyResolutionServices, versionSelectorScheme, delegate);
    }

    @Override
    protected ArtifactRepository internalCreateArtifactRepository(RepositoryHandler repositoryHandler) {
        return repositoryHandler.ivy(new Action<IvyArtifactRepository>() {
            @Override
            public void execute(IvyArtifactRepository ivyArtifactRepository) {
                ivyArtifactRepository.setName(getArtifactRepositoryName());
                ivyArtifactRepository.setUrl(getUrl());
                Credentials credentials = authenticationSupport().getConfiguredCredentials();
                if (credentials != null) {
                    ((AuthenticationSupportedInternal)ivyArtifactRepository).setConfiguredCredentials(credentials);
                    ivyArtifactRepository.authentication(new Action<AuthenticationContainer>() {
                        @Override
                        public void execute(AuthenticationContainer authenticationContainer) {
                            authenticationContainer.addAll(authenticationSupport().getConfiguredAuthentication());
                        }
                    });
                }

                if(layoutName != null) {
                    if(layoutAction != null) {
                        ivyArtifactRepository.layout(layoutName, layoutAction);
                    } else {
                        ivyArtifactRepository.layout(layoutName);
                    }
                }

                if(artifactPattern != null) {
                    ivyArtifactRepository.artifactPattern(artifactPattern);
                }

                if(ivyPattern != null) {
                    ivyArtifactRepository.ivyPattern(ivyPattern);
                }
            }
        });
    }

    @Override
    public void artifactPattern(String pattern) {
        this.artifactPattern = pattern;
    }

    @Override
    public void ivyPattern(String pattern) {
        this.ivyPattern = pattern;
    }

    @Override
    public void layout(String layoutName) {
        this.layoutName = layoutName;
    }

    @Override
    public void layout(String layoutName, Action<? extends RepositoryLayout> config) {
        layout(layoutName);
        layoutAction = config;
    }
}
