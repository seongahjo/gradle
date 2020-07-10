/*
 * Copyright 2009 the original author or authors.
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

package org.gradle.api.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet;
import org.gradle.api.plugins.internal.DefaultWarPluginConvention;
import org.gradle.api.plugins.jvm.JvmEcosystemAttributesDetails;
import org.gradle.api.plugins.jvm.internal.JvmPluginServices;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.War;

import javax.inject.Inject;
import java.util.concurrent.Callable;

/**
 * <p>A {@link Plugin} which extends the {@link JavaPlugin} to add tasks which assemble a web application into a WAR
 * file.</p>
 */
public class WarPlugin implements Plugin<Project> {
    public static final String PROVIDED_COMPILE_CONFIGURATION_NAME = "providedCompile";
    public static final String PROVIDED_RUNTIME_CONFIGURATION_NAME = "providedRuntime";
    public static final String WAR_TASK_NAME = "war";
    public static final String WEB_APP_GROUP = "web application";
    public static final String COMPONENT_NAME = "web";

    private final SoftwareComponentFactory softwareComponentFactory;
    private final JvmPluginServices jvmPluginServices;

    @Inject
    public WarPlugin(SoftwareComponentFactory softwareComponentFactory, JvmPluginServices jvmPluginServices) {
        this.softwareComponentFactory = softwareComponentFactory;
        this.jvmPluginServices = jvmPluginServices;
    }

    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        final WarPluginConvention pluginConvention = new DefaultWarPluginConvention(project);
        project.getConvention().getPlugins().put("war", pluginConvention);

        project.getTasks().withType(War.class).configureEach(task -> {
            task.from((Callable) () -> pluginConvention.getWebAppDir());
            task.dependsOn((Callable) () -> project.getConvention()
                .getPlugin(JavaPluginConvention.class)
                .getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .getRuntimeClasspath());
            task.classpath((Callable) () -> {
                FileCollection runtimeClasspath = project.getConvention().getPlugin(JavaPluginConvention.class)
                    .getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
                Configuration providedRuntime = project.getConfigurations().getByName(PROVIDED_RUNTIME_CONFIGURATION_NAME);
                return runtimeClasspath.minus(providedRuntime);
            });
        });

        configurePublishing(project);
        configureConfigurations(project.getConfigurations());
    }

    private void configurePublishing(Project project) {
        TaskProvider<War> war = project.getTasks().register(WAR_TASK_NAME, War.class, warTask -> {
            warTask.setDescription("Generates a war archive with all the compiled classes, the web-app content and the libraries.");
            warTask.setGroup(BasePlugin.BUILD_GROUP);
        });
        // We create another component, "web", for backwards compatibility
        project.getComponents().add(softwareComponentFactory.adhoc("web"));
        // The name of the variant is also chosen for backwards compatibility
        jvmPluginServices.createOutgoingElements("master", builder ->
            builder.providesRuntime()
                .artifact(war)
                .providesAttributes(JvmEcosystemAttributesDetails::withEmbeddedDependencies)
                .published(COMPONENT_NAME)
        );
        // This is legacy, for 'maven' publishing (the old one)
        PublishArtifact warArtifact = new LazyPublishArtifact(war);
        project.getExtensions().getByType(DefaultArtifactPublicationSet.class).addCandidate(warArtifact);
    }

    public void configureConfigurations(ConfigurationContainer configurationContainer) {
        Configuration provideCompileConfiguration = configurationContainer.create(PROVIDED_COMPILE_CONFIGURATION_NAME).setVisible(false).
            setDescription("Additional compile classpath for libraries that should not be part of the WAR archive.");
        Configuration provideRuntimeConfiguration = configurationContainer.create(PROVIDED_RUNTIME_CONFIGURATION_NAME).setVisible(false).
            extendsFrom(provideCompileConfiguration).
            setDescription("Additional runtime classpath for libraries that should not be part of the WAR archive.");
        configurationContainer.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(provideCompileConfiguration);
        configurationContainer.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME).extendsFrom(provideRuntimeConfiguration);
    }

}
