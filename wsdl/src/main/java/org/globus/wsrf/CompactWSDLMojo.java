package org.globus.wsrf;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.commons.io.FileUtils;
import org.globus.wsrf.tools.wsdl.GenerateBinding;
import org.globus.wsrf.tools.wsdl.WSDLPreprocessor;

import java.io.File;

/**
 * @author turtlebender
 * @goal generateWSDL
 */
public class CompactWSDLMojo extends AbstractMojo {

    /**
     * @parameter expression="${generateWSDL.outputDir}" default-value="target/wsdl"
     */
    private File outputDir;

    /**
     * @parameter expression="${generateWSDL.wsdlFile}"
     */
    private File wsdlFile;

    /**
     * @parameter expression="${generateFile.bindingProtocol}" default-value="http"
     */
    private String bindingProtocol;

    /**
     * @parameter expression="${generateFile.portTypeName}"
     */
    private String portTypeName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            WSDLPreprocessor pp = new WSDLPreprocessor();
            String wfn = wsdlFile.getName();
            int sep = wfn.lastIndexOf(".");
            String basename = wfn.substring(0, sep);
            String flatName = basename + "_flattened.wsdl";
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            if (!outputDir.isDirectory()) {
                throw new MojoFailureException("Output directory is a file");
            }
            FileUtils.copyDirectory(wsdlFile.getParentFile(), outputDir);
            File flatFile = new File(outputDir, flatName);
            pp.setInputFile(wsdlFile.getAbsolutePath());
            pp.setOutputFile(flatFile.getAbsolutePath());
            pp.setPortTypeName(portTypeName);
            this.getLog().warn("writing flattened file to " + flatFile.getAbsolutePath());
            pp.execute();
            GenerateBinding gb = new GenerateBinding();
            gb.setFileRoot(new File(outputDir, basename).getAbsolutePath());
            gb.setProtocol(bindingProtocol);
            gb.setPortTypeFile(flatFile.getAbsolutePath());
            gb.execute();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage());
        }
    }


    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public File getWsdlFile() {
        return wsdlFile;
    }

    public void setWsdlFile(File wsdlFile) {
        this.wsdlFile = wsdlFile;
    }

    public String getBindingProtocol() {
        return bindingProtocol;
    }

    public void setBindingProtocol(String bindingProtocol) {
        this.bindingProtocol = bindingProtocol;
    }
}
