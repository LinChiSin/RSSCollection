/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.language.swift.tasks;

import org.gradle.api.Incubating;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.language.nativeplatform.tasks.AbstractNativeSourceCompileTask;
import org.gradle.language.swift.internal.DefaultSwiftCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.NativeCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.SwiftCompileSpec;

/**
 * Compiles Swift source files into object files, executables and libraries.
 *
 * @since 4.1
 */
@Incubating
public class SwiftCompile extends AbstractNativeSourceCompileTask {
    private String moduleName;

    @Override
    protected NativeCompileSpec createCompileSpec() {
        SwiftCompileSpec spec = new DefaultSwiftCompileSpec();
        spec.setModuleName(moduleName);
        return spec;
    }

    @Optional
    @Input
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
