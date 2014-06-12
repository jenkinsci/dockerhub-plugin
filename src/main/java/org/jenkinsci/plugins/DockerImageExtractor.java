package org.jenkinsci.plugins;

import hudson.model.AbstractProject;

import java.util.Collection;

/**
 * Identifies the Docker images used by a Jenkins job. Docker-related plugins can use this
 * extension point to integrate with DockerHub webhook support.
 *
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public abstract class DockerImageExtractor {

    public abstract Collection<String> getDockerImagesUsedByJob(AbstractProject job);

}