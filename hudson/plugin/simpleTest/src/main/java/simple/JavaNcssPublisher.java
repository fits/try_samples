package simple;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;

public class JavaNcssPublisher extends Publisher {

    JavaNcssPublisher() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	
    	FilePath[] files = build.getProject().getWorkspace().list("**/javancss*report.xml");

    	if (files.length == 0) {
    		listener.getLogger().println("not found : javancss report file");
    		build.setResult(Result.FAILURE);
    	}
    	else {
    		FilePath root = new FilePath(build.getRootDir());
    		FilePath target = new FilePath(root, "javancss_result.xml");
   		
    		files[0].copyTo(target);

    		JavaNcssAction a = new JavaNcssAction(build);
    		a.setResultFileName(target.getName());
    		
    		build.addAction(a);
    	}
    	
    	return true;
    }

    /*
    //下記のメソッドを有効にするとプロジェクト画面の左メニューに表示される
    @Override
    public Action getProjectAction(Project project) {
        return new JavaNcssAction(project);
    }
*/
    
    public Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {
        DescriptorImpl() {
            super(JavaNcssPublisher.class);
        }

        public String getDisplayName() {
            return "JavaNCSS reports";
        }

        public JavaNcssPublisher newInstance(StaplerRequest req) throws FormException {
            return new JavaNcssPublisher();
        }
    }
}
