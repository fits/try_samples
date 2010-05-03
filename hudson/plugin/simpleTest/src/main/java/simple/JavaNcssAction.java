package simple;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class JavaNcssAction implements Action {

	private static final long serialVersionUID = 1L;

	private AbstractBuild<?, ?> owner;
	private String resultFileName;

	public JavaNcssAction(AbstractBuild<?, ?> owner) {
		this.owner = owner;
	}

	@Override
	public String getDisplayName() {
		return "JavaNCSS Results";
	}

	@Override
	public String getIconFileName() {
		return "document.gif";
	}

	@Override
	public String getUrlName() {
		return "javaNcss";
	}

	public String getResultFileName() {
		return this.resultFileName;
	}
	
	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}

	public AbstractBuild<?, ?> getOwner() {
		return this.owner;
	}

	public List<FunctionMetrics> getFunctionMetricsList() {
		return this.parseResultXml();
	}
	
	private List<FunctionMetrics> parseResultXml() {
		List<FunctionMetrics> result = new ArrayList<FunctionMetrics>();

		if (this.resultFileName != null) {

	        SAXReader saxReader = new SAXReader();

			try {
				Document doc = saxReader.read(new File(this.owner.getRootDir(), this.resultFileName));
		        XPath xpath = DocumentHelper.createXPath("//function");

		        for (Element func : (List<Element>)xpath.selectNodes(doc)) {
		        	FunctionMetrics fm = new FunctionMetrics();
		        	
		        	fm.name = func.elementTextTrim("name");
		        	fm.ccn = this.parseInt(func.elementTextTrim("ccn"));
		        	fm.ncss = this.parseInt(func.elementTextTrim("ncss"));

		        	result.add(fm);
		        }
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private Integer parseInt(String number) {
		Integer result = null;
		
		if (number != null) {
			try {
				result = Integer.parseInt(number);
			}
			catch (NumberFormatException ex) {
			}
		}

		return result;
	}

	public class FunctionMetrics {
		public String name;
		public Integer ncss;
		public Integer ccn;
	}
}
