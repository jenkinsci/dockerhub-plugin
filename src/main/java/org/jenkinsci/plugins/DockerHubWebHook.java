package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.UnprotectedRootAction;
import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Extension
public class DockerHubWebHook implements UnprotectedRootAction {

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "DockerHub web hook";
    }

    public String getUrlName() {
        return "/dockerhub-webhook";
    }

    public void doIndex(StaplerRequest request) throws IOException {

        String remote = parse(request);

        // seach all jobs for DockerHubTrigger
        OUTER:
        for (AbstractProject p : Jenkins.getInstance().getAllItems(AbstractProject.class)) {
            DockerHubTrigger trigger = (DockerHubTrigger) p.getTrigger(DockerHubTrigger.class);
            if (trigger == null) continue;

            LOGGER.fine("Inspecting candidate job "+p.getName());
            for (DockerImageExtractor extractor : Jenkins.getInstance().getExtensionList(DockerImageExtractor.class)) {
                if (extractor.getDockerImagesUsedByJob(p).contains(remote)) {
                    LOGGER.info("Schedule job "+p.getName() + " as Docker image " + remote + " has been rebuilt by DockerHub");
                    p.scheduleBuild2(0, new DockerHubWebHookCause(remote));
                    continue OUTER;
                }
            }
        }

    }

    private String parse(StaplerRequest req) throws IOException {

        // helper for development without Dockerub integration
        if (req.hasParameter("image")) return req.getParameter("image");

        String body = IOUtils.toString(req.getInputStream());
        String contentType = req.getContentType();
        if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
            body = URLDecoder.decode(body);
        }
        if (body.startsWith("payload=")) body = body.substring(8);

        LOGGER.fine("Received commit hook notification : " + body);
        JSONObject payload = JSONObject.fromObject(body);
        return parse(payload);
    }

    /**
     * See http://docs.docker.io/docker-hub/repos/#webhooks
     */
    private String parse(JSONObject payload) {
        JSONObject repository = payload.getJSONObject("repository");
        return repository.getString("namespace") + "/" + repository.getString("name");
    }

    private static final Logger LOGGER = Logger.getLogger(DockerHubWebHook.class.getName());
}
