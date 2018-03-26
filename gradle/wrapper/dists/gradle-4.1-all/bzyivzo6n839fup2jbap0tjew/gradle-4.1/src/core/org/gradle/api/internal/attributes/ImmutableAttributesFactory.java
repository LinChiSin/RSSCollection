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
package org.gradle.api.internal.attributes;

import org.gradle.api.attributes.Attribute;

public interface ImmutableAttributesFactory {
    DefaultImmutableAttributesFactory.Builder builder();

    DefaultImmutableAttributesFactory.Builder builder(ImmutableAttributes from);

    ImmutableAttributes of(Attribute<?> key, Object value);

    /**
     * Adds the given attribute to the given container. Note: the container _should not_ contain the given attribute.
     */
    ImmutableAttributes concat(ImmutableAttributes node, Attribute<?> key, Object value);

    /**
     * Merges the second container into the first container and returns the result. Values in the second container win.
     */
    ImmutableAttributes concat(ImmutableAttributes attributes1, ImmutableAttributes attributes2);
}
