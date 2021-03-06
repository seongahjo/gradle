/*
 * Copyright 2018 the original author or authors.
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

package org.gradle.ide.visualstudio

import org.gradle.language.VariantContext
import org.gradle.nativeplatform.fixtures.app.CppLib
import org.gradle.nativeplatform.fixtures.app.CppSourceElement

class VisualStudioCppStaticLibraryLinkageProjectIntegrationTest extends AbstractVisualStudioProjectIntegrationTest {
    @Override
    String getBuildFile(VariantContext variantContext) {
        return staticLibraryName("build/lib/main/${variantContext.asPath}${rootProjectName}")
    }

    @Override
    void makeSingleProject() {
        buildFile << """
            apply plugin: "cpp-library"
            library.linkage = [Linkage.STATIC]
        """
    }

    @Override
    String getVisualStudioProjectName() {
        return "${rootProjectName}Lib"
    }

    @Override
    String getComponentUnderTestDsl() {
        return "library"
    }

    @Override
    CppSourceElement getComponentUnderTest() {
        return new CppLib()
    }

    @Override
    List<String> getTasksToBuildFromIde(String variant) {
        return [":compile${variant.capitalize()}Cpp", ":create${variant.capitalize()}"]
    }

    @Override
    String getIdeBuildTaskName(String variant) {
        return "create${variant.capitalize()}"
    }

    @Override
    List<String> getExpectedBaseIncludePaths() {
        return ["src/main/public"] + super.getExpectedBaseIncludePaths()
    }
}
