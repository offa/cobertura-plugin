package hudson.plugins.cobertura;

import hudson.model.*;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;

/**
 * Project level action.
 *
 * @author Stephen Connolly
 */
public class CoberturaProjectAction extends Actionable implements ProminentProjectAction {

    private final Project<?,?> project;

    public CoberturaProjectAction(Project project) {
        this.project = project;
    }

    public String getIconFileName() {
        if (new File(CoberturaPublisher.getCoberturaReportDir(project), "index.html").exists())
            return "graph.gif";
        else if (new File(CoberturaPublisher.getCoberturaReportDir(project), "cobertura.xml").exists())
            return "graph.gif";
        else
            return null;
    }

    public String getDisplayName() {
        if (new File(CoberturaPublisher.getCoberturaReportDir(project), "index.html").exists())
            return "Cobertura Coverage Report";
        else if (new File(CoberturaPublisher.getCoberturaReportDir(project), "cobertura.xml").exists())
            return "Coverage Report";
        else
            return null;
    }

    public String getUrlName() {
        if (new File(CoberturaPublisher.getCoberturaReportDir(project), "index.html").exists())
            return "cobertura";
        else if (new File(CoberturaPublisher.getCoberturaReportDir(project), "cobertura.xml").exists())
            return "lastBuild/cobertura";
        return "cobertura";
    }

    public CoberturaBuildAction getLastResult() {
        for (Build<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            CoberturaBuildAction r = b.getAction(CoberturaBuildAction.class);
            if (r != null)
                return r;
        }
        return null;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (getLastResult() != null)
            getLastResult().getResult().doGraph(req, rsp);
    }

    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException,
            InterruptedException {
        new DirectoryBrowserSupport(this).serveFile(req, rsp,
                new FilePath(CoberturaPublisher.getCoberturaReportDir(project)), "graph.gif", false);
    }

    public String getSearchUrl() {
        return getUrlName();
    }
}