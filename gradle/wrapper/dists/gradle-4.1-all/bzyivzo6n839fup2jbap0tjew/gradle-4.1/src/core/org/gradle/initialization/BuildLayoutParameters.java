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

package org.gradle.initialization;

import org.gradle.internal.SystemProperties;

import javax.annotation.Nullable;
import java.io.File;

import static org.gradle.internal.FileUtils.canonicalize;

public class BuildLayoutParameters {
    public static final String GRADLE_USER_HOME_PROPERTY_KEY = "gradle.user.home";
    private static final File DEFAULT_GRADLE_USER_HOME = new File(SystemProperties.getInstance().getUserHome() + "/.gradle");

    private boolean searchUpwards = true;
    private File currentDir = canonicalize(SystemProperties.getInstance().getCurrentDir());
    private File projectDir;
    private File gradleUserHomeDir;

    public BuildLayoutParameters() {
        String gradleUserHome = System.getProperty(GRADLE_USER_HOME_PROPERTY_KEY);
        if (gradleUserHome == null) {
            gradleUserHome = System.getenv("GRADLE_USER_HOME");
            if (gradleUserHome == null) {
                gradleUserHome = DEFAULT_GRADLE_USER_HOME.getAbsolutePath();
            }
        }
        gradleUserHomeDir = canonicalize(new File(gradleUserHome));
    }

    public BuildLayoutParameters setSearchUpwards(boolean searchUpwards) {
        this.searchUpwards = searchUpwards;
        return this;
    }

    public BuildLayoutParameters setProjectDir(File projectDir) {
        this.projectDir = projectDir;
        return this;
    }

    public BuildLayoutParameters setGradleUserHomeDir(File gradleUserHomeDir) {
        this.gradleUserHomeDir = gradleUserHomeDir;
        return this;
    }

    public BuildLayoutParameters setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
        return this;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    @Nullable
    public File getProjectDir() {
        return projectDir;
    }

    public File getSearchDir() {
        return projectDir != null ? projectDir : currentDir;
    }

    public File getGradleUserHomeDir() {
        return gradleUserHomeDir;
    }

    public boolean getSearchUpwards() {
        return searchUpwards;
    }
}
