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

package org.gradle.internal.service.scopes;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.ClassGenerator;
import org.gradle.api.internal.ClassPathRegistry;
import org.gradle.api.internal.DefaultClassPathProvider;
import org.gradle.api.internal.DefaultClassPathRegistry;
import org.gradle.api.internal.DependencyClassPathProvider;
import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.InstantiatorFactory;
import org.gradle.api.internal.artifacts.DefaultModule;
import org.gradle.api.internal.artifacts.DependencyManagementServices;
import org.gradle.api.internal.artifacts.Module;
import org.gradle.api.internal.artifacts.configurations.DependencyMetaDataProvider;
import org.gradle.api.internal.classpath.ModuleRegistry;
import org.gradle.api.internal.classpath.PluginModuleRegistry;
import org.gradle.api.internal.component.ComponentTypeRegistry;
import org.gradle.api.internal.component.DefaultComponentTypeRegistry;
import org.gradle.api.internal.file.FileLookup;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.collections.DirectoryFileTreeFactory;
import org.gradle.api.internal.hash.FileHasher;
import org.gradle.api.internal.initialization.DefaultScriptHandlerFactory;
import org.gradle.api.internal.initialization.ScriptHandlerFactory;
import org.gradle.api.internal.initialization.loadercache.ClassLoaderCache;
import org.gradle.api.internal.plugins.DefaultPluginRegistry;
import org.gradle.api.internal.plugins.PluginInspector;
import org.gradle.api.internal.plugins.PluginRegistry;
import org.gradle.api.internal.project.DefaultProjectAccessListener;
import org.gradle.api.internal.project.DefaultProjectRegistry;
import org.gradle.api.internal.project.DefaultProjectTaskLister;
import org.gradle.api.internal.project.IProjectFactory;
import org.gradle.api.internal.project.IsolatedAntBuilder;
import org.gradle.api.internal.project.ProjectFactory;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.ProjectRegistry;
import org.gradle.api.internal.project.ProjectTaskLister;
import org.gradle.api.internal.project.antbuilder.DefaultIsolatedAntBuilder;
import org.gradle.api.internal.project.taskfactory.AnnotationProcessingTaskFactory;
import org.gradle.api.internal.project.taskfactory.DefaultTaskClassInfoStore;
import org.gradle.api.internal.project.taskfactory.DefaultTaskClassValidatorExtractor;
import org.gradle.api.internal.project.taskfactory.DependencyAutoWireTaskFactory;
import org.gradle.api.internal.project.taskfactory.ITaskFactory;
import org.gradle.api.internal.project.taskfactory.PropertyAnnotationHandler;
import org.gradle.api.internal.project.taskfactory.TaskClassInfoStore;
import org.gradle.api.internal.project.taskfactory.TaskClassValidatorExtractor;
import org.gradle.api.internal.project.taskfactory.TaskFactory;
import org.gradle.api.internal.tasks.execution.statistics.TaskExecutionStatisticsEventAdapter;
import org.gradle.api.internal.tasks.execution.statistics.TaskExecutionStatisticsListener;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.CacheValidator;
import org.gradle.caching.internal.BuildCacheServices;
import org.gradle.configuration.BuildConfigurer;
import org.gradle.configuration.DefaultBuildConfigurer;
import org.gradle.configuration.DefaultInitScriptProcessor;
import org.gradle.configuration.DefaultScriptPluginFactory;
import org.gradle.configuration.ImportsReader;
import org.gradle.configuration.ScriptPluginFactory;
import org.gradle.configuration.ScriptPluginFactorySelector;
import org.gradle.configuration.project.BuildScriptProcessor;
import org.gradle.configuration.project.ConfigureActionsProjectEvaluator;
import org.gradle.configuration.project.DelayedConfigurationActions;
import org.gradle.configuration.project.LifecycleProjectEvaluator;
import org.gradle.configuration.project.PluginsProjectConfigureActions;
import org.gradle.configuration.project.ProjectEvaluator;
import org.gradle.execution.ProjectConfigurer;
import org.gradle.execution.TaskPathProjectEvaluator;
import org.gradle.groovy.scripts.DefaultScriptCompilerFactory;
import org.gradle.groovy.scripts.ScriptCompilerFactory;
import org.gradle.groovy.scripts.ScriptExecutionListener;
import org.gradle.groovy.scripts.internal.BuildScopeInMemoryCachingScriptClassCompiler;
import org.gradle.groovy.scripts.internal.CrossBuildInMemoryCachingScriptClassCache;
import org.gradle.groovy.scripts.internal.DefaultScriptCompilationHandler;
import org.gradle.groovy.scripts.internal.DefaultScriptRunnerFactory;
import org.gradle.groovy.scripts.internal.FileCacheBackedScriptClassCompiler;
import org.gradle.composite.internal.IncludedBuildFactory;
import org.gradle.initialization.BuildCancellationToken;
import org.gradle.initialization.BuildLoader;
import org.gradle.initialization.BuildRequestMetaData;
import org.gradle.initialization.ClassLoaderRegistry;
import org.gradle.initialization.ClassLoaderScopeRegistry;
import org.gradle.initialization.DefaultClassLoaderScopeRegistry;
import org.gradle.initialization.DefaultGradlePropertiesLoader;
import org.gradle.initialization.DefaultSettingsFinder;
import org.gradle.initialization.DefaultSettingsLoaderFactory;
import org.gradle.initialization.IGradlePropertiesLoader;
import org.gradle.initialization.InitScriptHandler;
import org.gradle.initialization.InstantiatingBuildLoader;
import org.gradle.initialization.NestedBuildFactory;
import org.gradle.initialization.NotifyingSettingsProcessor;
import org.gradle.initialization.ProjectAccessListener;
import org.gradle.initialization.ProjectPropertySettingBuildLoader;
import org.gradle.initialization.PropertiesLoadingSettingsProcessor;
import org.gradle.initialization.ScriptEvaluatingSettingsProcessor;
import org.gradle.initialization.SettingsFactory;
import org.gradle.initialization.SettingsLoaderFactory;
import org.gradle.initialization.SettingsProcessor;
import org.gradle.initialization.buildsrc.BuildSourceBuilder;
import org.gradle.initialization.buildsrc.BuildSrcBuildListenerFactory;
import org.gradle.initialization.buildsrc.BuildSrcProjectConfigurationAction;
import org.gradle.initialization.layout.BuildLayoutFactory;
import org.gradle.internal.actor.ActorFactory;
import org.gradle.internal.actor.internal.DefaultActorFactory;
import org.gradle.internal.authentication.AuthenticationSchemeRegistry;
import org.gradle.internal.authentication.DefaultAuthenticationSchemeRegistry;
import org.gradle.internal.classloader.ClassLoaderFactory;
import org.gradle.internal.classloader.ClassLoaderHierarchyHasher;
import org.gradle.internal.classpath.CachedClasspathTransformer;
import org.gradle.internal.cleanup.BuildOutputCleanupListener;
import org.gradle.internal.composite.CompositeContextBuilder;
import org.gradle.internal.concurrent.ExecutorFactory;
import org.gradle.internal.event.ListenerManager;
import org.gradle.internal.logging.LoggingManagerInternal;
import org.gradle.internal.logging.progress.ProgressLoggerFactory;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.operations.logging.BuildOperationLoggerFactory;
import org.gradle.internal.operations.logging.DefaultBuildOperationLoggerFactory;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.scripts.ScriptingLanguages;
import org.gradle.internal.service.CachingServiceLocator;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.internal.service.ServiceRegistration;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.time.TimeProvider;
import org.gradle.internal.time.TrueTimeProvider;
import org.gradle.model.internal.inspect.ModelRuleSourceDetector;
import org.gradle.plugin.repository.internal.PluginRepositoryFactory;
import org.gradle.plugin.repository.internal.PluginRepositoryRegistry;
import org.gradle.plugin.use.internal.PluginRequestApplicator;
import org.gradle.profile.ProfileEventAdapter;
import org.gradle.profile.ProfileListener;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;
import org.gradle.tooling.provider.model.internal.BuildScopeToolingModelBuilderRegistryAction;
import org.gradle.tooling.provider.model.internal.DefaultToolingModelBuilderRegistry;

import java.util.List;

/**
 * Contains the singleton services for a single build invocation.
 */
public class BuildScopeServices extends DefaultServiceRegistry {
    public BuildScopeServices(final ServiceRegistry parent) {
        super(parent);
        addProvider(new BuildCacheServices());
        register(new Action<ServiceRegistration>() {
            public void execute(ServiceRegistration registration) {
                for (PluginServiceRegistry pluginServiceRegistry : parent.getAll(PluginServiceRegistry.class)) {
                    pluginServiceRegistry.registerBuildServices(registration);
                }
            }
        });
    }

    protected TimeProvider createTimeProvider() {
        return new TrueTimeProvider();
    }

    protected ProjectRegistry<ProjectInternal> createProjectRegistry() {
        return new DefaultProjectRegistry<ProjectInternal>();
    }

    protected IProjectFactory createProjectFactory(Instantiator instantiator, ProjectRegistry<ProjectInternal> projectRegistry) {
        return new ProjectFactory(instantiator, projectRegistry);
    }

    protected ListenerManager createListenerManager(ListenerManager listenerManager) {
        return listenerManager.createChild();
    }

    protected ClassPathRegistry createClassPathRegistry() {
        ModuleRegistry moduleRegistry = get(ModuleRegistry.class);
        return new DefaultClassPathRegistry(
            new DefaultClassPathProvider(moduleRegistry),
            new DependencyClassPathProvider(moduleRegistry, get(PluginModuleRegistry.class)));
    }

    protected IsolatedAntBuilder createIsolatedAntBuilder(ClassPathRegistry classPathRegistry, ClassLoaderFactory classLoaderFactory, ModuleRegistry moduleRegistry) {
        return new DefaultIsolatedAntBuilder(classPathRegistry, classLoaderFactory, moduleRegistry);
    }

    protected ActorFactory createActorFactory() {
        return new DefaultActorFactory(get(ExecutorFactory.class));
    }

    protected IGradlePropertiesLoader createGradlePropertiesLoader() {
        return new DefaultGradlePropertiesLoader(get(StartParameter.class));
    }

    protected BuildLoader createBuildLoader() {
        return new ProjectPropertySettingBuildLoader(
            get(IGradlePropertiesLoader.class),
            new InstantiatingBuildLoader(get(IProjectFactory.class)));
    }

    protected ProjectEvaluator createProjectEvaluator(BuildOperationExecutor buildOperationExecutor, CachingServiceLocator cachingServiceLocator, ScriptPluginFactory scriptPluginFactory) {
        ConfigureActionsProjectEvaluator withActionsEvaluator = new ConfigureActionsProjectEvaluator(
            PluginsProjectConfigureActions.from(cachingServiceLocator),
            new BuildScriptProcessor(scriptPluginFactory),
            new DelayedConfigurationActions()
        );
        return new LifecycleProjectEvaluator(buildOperationExecutor, withActionsEvaluator);
    }

    protected TaskClassValidatorExtractor createTaskClassValidatorExtractor(List<PropertyAnnotationHandler> annotationHandlers) {
        return new DefaultTaskClassValidatorExtractor(annotationHandlers);
    }

    protected TaskClassInfoStore createTaskClassInfoStore(TaskClassValidatorExtractor validatorExtractor) {
        return new DefaultTaskClassInfoStore(validatorExtractor);
    }

    protected ITaskFactory createITaskFactory(TaskClassInfoStore taskClassInfoStore) {
        return new DependencyAutoWireTaskFactory(
            new AnnotationProcessingTaskFactory(
                taskClassInfoStore,
                new TaskFactory(
                    get(ClassGenerator.class))
            )
        );
    }

    protected ScriptCompilerFactory createScriptCompileFactory(ListenerManager listenerManager,
                                                               FileCacheBackedScriptClassCompiler scriptCompiler,
                                                               CrossBuildInMemoryCachingScriptClassCache cache) {
        ScriptExecutionListener scriptExecutionListener = listenerManager.getBroadcaster(ScriptExecutionListener.class);
        return new DefaultScriptCompilerFactory(
            new BuildScopeInMemoryCachingScriptClassCompiler(cache, scriptCompiler),
            new DefaultScriptRunnerFactory(
                scriptExecutionListener,
                DirectInstantiator.INSTANCE
            )
        );
    }

    protected FileCacheBackedScriptClassCompiler createFileCacheBackedScriptClassCompiler(
        CacheRepository cacheRepository, final StartParameter startParameter,
        ProgressLoggerFactory progressLoggerFactory, ClassLoaderCache classLoaderCache, ImportsReader importsReader,
        FileHasher hasher, ClassLoaderHierarchyHasher classLoaderHierarchyHasher) {
        CacheValidator scriptCacheInvalidator = new CacheValidator() {
            public boolean isValid() {
                return !startParameter.isRecompileScripts();
            }
        };
        return new FileCacheBackedScriptClassCompiler(
            cacheRepository,
            scriptCacheInvalidator,
            new DefaultScriptCompilationHandler(classLoaderCache, importsReader),
            progressLoggerFactory,
            hasher,
            classLoaderCache,
            classLoaderHierarchyHasher);
    }

    protected ScriptPluginFactory createScriptPluginFactory(ScriptingLanguages scriptingLanguages, InstantiatorFactory instantiatorFactory, BuildOperationExecutor buildOperationExecutor) {
        DefaultScriptPluginFactory defaultScriptPluginFactory = defaultScriptPluginFactory();
        ScriptPluginFactorySelector.ProviderInstantiator instantiator = ScriptPluginFactorySelector.defaultProviderInstantiatorFor(instantiatorFactory.inject(this));
        ScriptPluginFactorySelector scriptPluginFactorySelector = new ScriptPluginFactorySelector(defaultScriptPluginFactory, scriptingLanguages, instantiator, buildOperationExecutor);
        defaultScriptPluginFactory.setScriptPluginFactory(scriptPluginFactorySelector);
        return scriptPluginFactorySelector;
    }

    private DefaultScriptPluginFactory defaultScriptPluginFactory() {
        return new DefaultScriptPluginFactory(
            get(ScriptCompilerFactory.class),
            getFactory(LoggingManagerInternal.class),
            get(Instantiator.class),
            get(ScriptHandlerFactory.class),
            get(PluginRequestApplicator.class),
            get(FileLookup.class),
            get(DirectoryFileTreeFactory.class),
            get(DocumentationRegistry.class),
            get(ModelRuleSourceDetector.class),
            get(PluginRepositoryRegistry.class),
            get(PluginRepositoryFactory.class),
            get(ProviderFactory.class));
    }

    protected SettingsLoaderFactory createSettingsLoaderFactory(SettingsProcessor settingsProcessor, NestedBuildFactory nestedBuildFactory,
                                                                ClassLoaderScopeRegistry classLoaderScopeRegistry, CacheRepository cacheRepository,
                                                                BuildLoader buildLoader, BuildOperationExecutor buildOperationExecutor,
                                                                CachedClasspathTransformer cachedClasspathTransformer,
                                                                CachingServiceLocator cachingServiceLocator,
                                                                CompositeContextBuilder compositeContextBuilder,
                                                                IncludedBuildFactory includedBuildFactory) {
        return new DefaultSettingsLoaderFactory(
            new DefaultSettingsFinder(new BuildLayoutFactory()),
            settingsProcessor,
            new BuildSourceBuilder(
                nestedBuildFactory,
                classLoaderScopeRegistry.getCoreAndPluginsScope(),
                cacheRepository,
                buildOperationExecutor,
                cachedClasspathTransformer,
                new BuildSrcBuildListenerFactory(
                    PluginsProjectConfigureActions.of(
                        BuildSrcProjectConfigurationAction.class,
                        cachingServiceLocator))),
            buildLoader, compositeContextBuilder, includedBuildFactory
        );
    }

    protected InitScriptHandler createInitScriptHandler(ScriptPluginFactory scriptPluginFactory, ScriptHandlerFactory scriptHandlerFactory, BuildOperationExecutor buildOperationExecutor) {
        return new InitScriptHandler(
            new DefaultInitScriptProcessor(
                scriptPluginFactory,
                scriptHandlerFactory
            ),
            buildOperationExecutor
        );
    }

    protected SettingsProcessor createSettingsProcessor(ScriptPluginFactory scriptPluginFactory, ScriptHandlerFactory scriptHandlerFactory, Instantiator instantiator,
                                                        ServiceRegistryFactory serviceRegistryFactory, IGradlePropertiesLoader propertiesLoader, BuildOperationExecutor buildOperationExecutor) {
        return new NotifyingSettingsProcessor(
            new PropertiesLoadingSettingsProcessor(
                new ScriptEvaluatingSettingsProcessor(
                    scriptPluginFactory,
                    scriptHandlerFactory,
                    new SettingsFactory(
                        instantiator,
                        serviceRegistryFactory
                    ),
                    propertiesLoader
                ),
                propertiesLoader
            ),
            buildOperationExecutor);
    }

    protected ScriptHandlerFactory createScriptHandlerFactory() {
        return new DefaultScriptHandlerFactory(
            get(DependencyManagementServices.class),
            get(FileResolver.class),
            get(DependencyMetaDataProvider.class)
        );
    }

    protected ProjectConfigurer createProjectConfigurer(BuildCancellationToken cancellationToken) {
        return new TaskPathProjectEvaluator(cancellationToken);
    }

    protected BuildConfigurer createBuildConfigurer(ProjectConfigurer projectConfigurer) {
        return new DefaultBuildConfigurer(projectConfigurer);
    }

    protected ProjectAccessListener createProjectAccessListener() {
        return new DefaultProjectAccessListener();
    }

    protected ProfileEventAdapter createProfileEventAdapter() {
        return new ProfileEventAdapter(get(BuildRequestMetaData.class), get(TimeProvider.class), get(ListenerManager.class).getBroadcaster(ProfileListener.class));
    }

    protected PluginRegistry createPluginRegistry(ClassLoaderScopeRegistry scopeRegistry, PluginInspector pluginInspector) {
        return new DefaultPluginRegistry(pluginInspector, scopeRegistry.getCoreAndPluginsScope());
    }

    protected TaskExecutionStatisticsEventAdapter createTaskExecutionStatisticsEventAdapter(ListenerManager listenerManager) {
        return new TaskExecutionStatisticsEventAdapter(listenerManager.getBroadcaster(TaskExecutionStatisticsListener.class));
    }

    protected ServiceRegistryFactory createServiceRegistryFactory(final ServiceRegistry services) {
        return new BuildScopeServiceRegistryFactory(services);
    }

    protected ClassLoaderScopeRegistry createClassLoaderScopeRegistry(ClassLoaderRegistry classLoaderRegistry, ClassLoaderCache classLoaderCache) {
        return new DefaultClassLoaderScopeRegistry(classLoaderRegistry, classLoaderCache);
    }

    protected ProjectTaskLister createProjectTaskLister() {
        return new DefaultProjectTaskLister();
    }

    protected DependencyMetaDataProvider createDependencyMetaDataProvider() {
        return new DependencyMetaDataProviderImpl();
    }

    protected ComponentTypeRegistry createComponentTypeRegistry() {
        return new DefaultComponentTypeRegistry();
    }

    protected PluginInspector createPluginInspector(ModelRuleSourceDetector modelRuleSourceDetector) {
        return new PluginInspector(modelRuleSourceDetector);
    }

    private class DependencyMetaDataProviderImpl implements DependencyMetaDataProvider {
        public Module getModule() {
            return new DefaultModule("unspecified", "unspecified", Project.DEFAULT_VERSION, Project.DEFAULT_STATUS);
        }
    }

    protected BuildOperationLoggerFactory createBuildOperationLoggerFactory() {
        return new DefaultBuildOperationLoggerFactory();
    }

    AuthenticationSchemeRegistry createAuthenticationSchemeRegistry() {
        return new DefaultAuthenticationSchemeRegistry();
    }

    protected BuildOutputCleanupListener createBuildOutputCleanupListener() {
        return new BuildOutputCleanupListener();
    }

    protected ToolingModelBuilderRegistry createBuildScopedToolingModelBuilders(List<BuildScopeToolingModelBuilderRegistryAction> registryActions) {
        ToolingModelBuilderRegistry registry = new DefaultToolingModelBuilderRegistry();
        for (BuildScopeToolingModelBuilderRegistryAction registryAction : registryActions) {
            registryAction.execute(registry);
        }
        return registry;
    }
}
