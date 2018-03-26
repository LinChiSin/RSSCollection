/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.api.internal.project.taskfactory;

import org.gradle.api.internal.TaskInternal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskOutputFilePropertyBuilder;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Callable;

import static org.gradle.api.internal.tasks.TaskOutputsUtil.ensureDirectoryExists;
import static org.gradle.api.internal.tasks.TaskOutputsUtil.validateDirectory;

public class OutputDirectoryPropertyAnnotationHandler extends AbstractOutputPropertyAnnotationHandler {

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return OutputDirectory.class;
    }

    @Override
    protected void validate(String propertyName, Object value, Collection<String> messages) {
        if (value != null) {
            validateDirectory(propertyName, toFile(value), messages);
        }
    }

    @Override
    protected TaskOutputFilePropertyBuilder createPropertyBuilder(TaskPropertyActionContext context, TaskInternal task, Callable<Object> futureValue) {
        return task.getOutputs().dir(futureValue);
    }

    @Override
    protected void beforeTask(final Callable<Object> futureValue) {
        File directory = toFile(futureValue);
        if (directory != null) {
            ensureDirectoryExists(directory);
        }
    }

    private File toFile(Object value) {
        Object unpacked = GFileUtils.unpack(value);
        if (unpacked instanceof Path) {
            return ((Path) unpacked).toFile();
        }
        return (File) unpacked;
    }
}
