package org.jenkinsci.plugins;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Pull and Run specified Docker image
 */
public class DockerImageBuilder extends Builder {

    private final String image;

    @DataBoundConstructor
    public DockerImageBuilder(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {

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
            return "Pull and Run Docker image";
        }
    }
}

