package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final AlgoutilsLibraryAccessors laccForAlgoutilsLibraryAccessors = new AlgoutilsLibraryAccessors(owner);
    private final JunitLibraryAccessors laccForJunitLibraryAccessors = new JunitLibraryAccessors(owner);
    private final MockitoLibraryAccessors laccForMockitoLibraryAccessors = new MockitoLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(providers, config);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers) {
        super(config, providers);
    }

        /**
         * Creates a dependency provider for annotations (org.jetbrains:annotations)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAnnotations() { return create("annotations"); }

        /**
         * Creates a dependency provider for flatlaf (com.formdev:flatlaf)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFlatlaf() { return create("flatlaf"); }

    /**
     * Returns the group of libraries at algoutils
     */
    public AlgoutilsLibraryAccessors getAlgoutils() { return laccForAlgoutilsLibraryAccessors; }

    /**
     * Returns the group of libraries at junit
     */
    public JunitLibraryAccessors getJunit() { return laccForJunitLibraryAccessors; }

    /**
     * Returns the group of libraries at mockito
     */
    public MockitoLibraryAccessors getMockito() { return laccForMockitoLibraryAccessors; }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class AlgoutilsLibraryAccessors extends SubDependencyFactory {

        public AlgoutilsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for student (org.tudalgo:algoutils-student)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getStudent() { return create("algoutils.student"); }

            /**
             * Creates a dependency provider for tutor (org.tudalgo:algoutils-tutor)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTutor() { return create("algoutils.tutor"); }

    }

    public static class JunitLibraryAccessors extends SubDependencyFactory {

        public JunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.junit.jupiter:junit-jupiter)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("junit.core"); }

    }

    public static class MockitoLibraryAccessors extends SubDependencyFactory {

        public MockitoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for inline (org.mockito:mockito-inline)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getInline() { return create("mockito.inline"); }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final AlgoVersionAccessors vaccForAlgoVersionAccessors = new AlgoVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: algoutils (0.4.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAlgoutils() { return getVersion("algoutils"); }

        /**
         * Returns the group of versions at versions.algo
         */
        public AlgoVersionAccessors getAlgo() { return vaccForAlgoVersionAccessors; }

    }

    public static class AlgoVersionAccessors extends VersionFactory  {

        public AlgoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: algo.student (0.6.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getStudent() { return getVersion("algo.student"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final JagrPluginAccessors baccForJagrPluginAccessors = new JagrPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for javafx to the plugin id 'org.openjfx.javafxplugin'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getJavafx() { return createPlugin("javafx"); }

            /**
             * Creates a plugin provider for style to the plugin id 'org.sourcegrade.style'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getStyle() { return createPlugin("style"); }

        /**
         * Returns the group of bundles at plugins.jagr
         */
        public JagrPluginAccessors getJagr() { return baccForJagrPluginAccessors; }

    }

    public static class JagrPluginAccessors extends PluginFactory {

        public JagrPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for jagr.gradle to the plugin id 'org.sourcegrade.jagr-gradle'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getGradle() { return createPlugin("jagr.gradle"); }

    }

}
