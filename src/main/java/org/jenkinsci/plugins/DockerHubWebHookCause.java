package org.jenkinsci.plugins;


import hudson.model.Cause;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class DockerHubWebHookCause extends Cause {

    private final String image;

    public DockerHubWebHookCause(String image) {
        this.image = image;
    }

    @Override
    public String getShortDescription() {
        return "Triggered by DockerHub WebHook";
    }
}
