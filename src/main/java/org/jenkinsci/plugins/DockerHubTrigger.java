package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.model.Item;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class DockerHubTrigger extends Trigger {

    @DataBoundConstructor
    public DockerHubTrigger() {
        super();
    }

    @Override
    public void start(Item project, boolean newInstance) {
        // TODO register jenkins instance to dockerhub hook
    }

    @Extension
    public static class DescriptorImpl extends TriggerDescriptor {

        @Override
        public boolean isApplicable(Item item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Run when a new image is built onDockerHub";
        }
    }
}
