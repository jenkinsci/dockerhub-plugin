package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Project;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Pull and Run specified Docker image
 */
public class DockerPullImageBuilder extends Builder {

    private final String image;

    @DataBoundConstructor
    public DockerPullImageBuilder(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {

        // TODO could maybe use Docker REST API, need first to check Java can talk with linux sockets
        int status = 0;
        try {
            status = launcher.launch()
                    .cmds("docker", "pull", image)
                    .writeStdin().stdout(listener.getLogger()).stderr(listener.getLogger()).join();
            if (status != 0) {
                throw new RuntimeException("Failed to pull docker image");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to pull docker image", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to pull docker image", e);
        }


        listener.getLogger().println("docker pull "+image);
        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * This human readable image is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Pull Docker image from DockerHub";
        }
    }

    @Extension
    public static final DockerImageExtractor EXTRACTOR = new DockerImageExtractor() {
        @Override
        public Collection<String> getDockerImagesUsedByJob(AbstractProject job) {
            if (job instanceof Project) {
                Project p = (Project) job;

                Collection<String> images = new ArrayList<String>();

                // check DockerHub build step for matching image ID
                for (Builder b : (List<Builder>) p.getBuilders()) { // Not sure why a cast is required here
                    if (b instanceof DockerPullImageBuilder) {
                        images.add(((DockerPullImageBuilder) b).getImage());
                    }
                }
                return images;
            } else {
                return Collections.EMPTY_SET;
            }
        }
    };
}

