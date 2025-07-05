package model

import org.gradle.api.provider.Property
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsPluginLicense
    @Inject
    constructor() {
        public abstract val name: Property<String>
        public abstract val url: Property<URI>
        public abstract val distribution: Property<String>
        public abstract val comments: Property<String>

        init {
            name.convention("Apache License Version 2.0")
            url.convention(URI("https://www.apache.org/licenses/LICENSE-2.0.txt"))
            distribution.convention("repo")
            comments.convention("LICENSE")
        }
    }
