package net.neoforged.neoforgegradle.dsl;

import net.neoforged.neoforgegradle.internal.utils.StringUtils;
import org.gradle.api.Named;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public abstract class RunModel implements Named, Dependencies {
    private final String name;
    /**
     * Sanitized name: converted to upper camel case and with invalid characters removed.
     */
    private final String baseName;

    private final Configuration configuration;

    @Inject
    public RunModel(String name, Project project) {
        this.name = name;
        this.baseName = StringUtils.toCamelCase(name, false);
        getMods().convention(project.getExtensions().getByType(NeoForgeExtension.class).getMods());

        getGameDirectory().convention(project.getLayout().getProjectDirectory().dir("run"));

        configuration = project.getConfigurations().create(nameOf("", "additionalRuntimeClasspath"), configuration -> {
            configuration.setCanBeResolved(false);
            configuration.setCanBeConsumed(false);
        });
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract DirectoryProperty getGameDirectory();

    public abstract MapProperty<String, String> getSystemProperties();

    public void systemProperty(String key, String value) {
        getSystemProperties().put(key, value);
    }

    public abstract ListProperty<String> getProgramArguments();

    public void programArgument(String arg) {
        getProgramArguments().add(arg);
    }

    public abstract SetProperty<ModModel> getMods();

    public abstract Property<String> getType();

    public void client() {
        getType().set("client");
    }

    public void data() {
        getType().set("data");
    }

    public void server() {
        getType().set("server");
    }

    public Configuration getAdditionalRuntimeClasspathConfiguration() {
        return configuration;
    }

    public abstract DependencyCollector getAdditionalRuntimeClasspath();

    // TODO: Move out of DSL class
    @ApiStatus.Internal
    public String getBaseName() {
        return baseName;
    }

    // TODO: Move out of DSL class
    @ApiStatus.Internal
    public String nameOf(@Nullable String prefix, @Nullable String suffix) {
        return StringUtils.uncapitalize((prefix == null ? "" : prefix) + this.baseName + (suffix == null ? "" : StringUtils.capitalize(suffix)));
    }

    @Override
    public String toString() {
        return "Run[" + getName() + "]";
    }
}
