package com.metaworkflows;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.HTTPUtils;

public class MetaWorkflow {
    
	protected static final String gnServiceURL = "http://localhost:8005/geonetwork";
    protected static final String gnUsername = "admin";
    protected static final String gnPassword = "admin";
    
    public GNClient client;
    
    private final static Logger LOGGER = Logger.getLogger(MetaWorkflowsTest.class);

	String tempDir= "C:\\tmp\\"; // windows
	
    
    public MetaWorkflow() {    	
    	System.out.println("GeoNetwork: constructing metaworkflow obj. Creating the geonet connection...");
        client = createClientAndCheckConnection();
    }
    
    protected GNClient createClientAndCheckConnection() {        
        GNClient client = new GNClient(gnServiceURL, gnUsername, gnPassword);
        boolean logged = client.ping();
        assertTrue("Error pinging GN", logged);
        return client;
    }
       

    protected GNInsertConfiguration createDefaultInsertConfiguration() {
        GNInsertConfiguration cfg = new GNInsertConfiguration();
        
        cfg.setCategory("datasets");
        cfg.setGroup("1"); // group 1 is usually "all"
        cfg.setStyleSheet("_none_");
        cfg.setValidate(Boolean.FALSE);
        return cfg;
    }
    
    
    
	/**
	 * takes a metadata element id for a catalogue:
	 * this is designed to get metadata from the configured geonetwork instance
	 * @param metadataElemId: unique id referencing the metadata record
	 * @return 
	 * @result result: metadata Element
	 */
    public Element GetMetadata(Long metadataElemId) throws Exception {
    	System.out.println("GeoNetwork: start retrieving metadata");

        GNClient client = createClientAndCheckConnection();
        Element metadataElement  = client.get(metadataElemId);
                        
        //pretty string printing of xml        
        XMLOutputter outp = new XMLOutputter();
        String metadataXmlString = outp.outputString(metadataElement);        
        //System.out.println(metadataXmlString);
              
        
        dumpTextToFile(metadataXmlString, metadataElemId + "_geonetwork response");
        //MetadataInfo metaDataInfo = client.getInfo(metadataElemId, false); // 

    	System.out.println("GeoNetwork: retrieved metadata. Id: " + metadataElement );
		return metadataElement;
    }
      
    
    
 	/**
 	 * takes a process output to be recorded in the catalogue
 	 * this is designed to set metadata in the configured geonetwork instance. The return value is 
 	 * the metadata ID of the newly inserted record.
 	 * @param metadataElemId: unique id referencing the metadata record
 	 * @return 
 	 * @result result: metadataId Long
 	 */
     public long RegisterResult(String newTitleElementString,String newDataUrlString) throws Exception {
     	System.out.println("GeoNetwork: start inserting metadata");
     	
         GNInsertConfiguration cfg = createDefaultInsertConfiguration();
         GNPrivConfiguration pcfg = new GNPrivConfiguration();

         pcfg.addPrivileges(GNPrivConfiguration.GROUP_GUEST,    EnumSet.of(GNPriv.FEATURED));
         pcfg.addPrivileges(GNPrivConfiguration.GROUP_INTRANET, EnumSet.of(GNPriv.DYNAMIC, GNPriv.FEATURED));
         pcfg.addPrivileges(GNPrivConfiguration.GROUP_ALL,      EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
         pcfg.addPrivileges(2, EnumSet.allOf(GNPriv.class));
         
         //Take a template ISO metadata document. Should set as a resource...
         File file = loadFile("C:\\geonetwork_metadata\\metadata_workflow_single_download.xml");        
         assertNotNull(file); 
         
         Element elementToInsert = parseFile(file);

         //pretty string printing of xml        
         /*
         XMLOutputter outp = new XMLOutputter();
         String idInfoMetadataXmlString = outp.outputString(idInfo);        
         System.out.println(idInfoMetadataXmlString );
         */
                 
         
         Element chstr2 = getTitleElement(elementToInsert);  
         System.out.println("GeoNetwork: getTitleElement " + chstr2.getText()); 
         chstr2.setText(newTitleElementString);
         
         
         
         Element locationElement = getLocationElement(elementToInsert);  
         System.out.println("GeoNetwork: getLocationElement " + locationElement.getText()); 
         locationElement.setText(newDataUrlString);
         
         //Create the XML file for inserting
         File tempFile = File.createTempFile("jbpm_data_update", ".xml");
         FileUtils.forceDeleteOnExit(tempFile);
         XMLOutputter fileOutputter = new XMLOutputter(Format.getCompactFormat());
         FileUtils.writeStringToFile(tempFile, fileOutputter.outputString(elementToInsert));
                         
         XMLOutputter outp = new XMLOutputter();
         String metadataXmlString = outp.outputString(elementToInsert);        
         System.out.println(metadataXmlString);
                  
         //Do insertion of the file
         GNClient client = createClientAndCheckConnection();  
         long metaDataId = client.insertMetadata(cfg, tempFile);
         
         System.out.println("GeoNetwork: inserted metadata. Id: " + metaDataId );
         
         return metaDataId;
     }
     
     

 	/**
 	 * Takes an XML Element object (i.e. ISO 19115/191389 definition) and returns the title of this artefact
 	 * @param metadata: a metadata record
 	 * @result result: metadata Element
 	 */
     //Function from GN lib
     public Element getTitleElement(Element metadata) {
         //    xmlns:gmd="http://www.isotc211.org/2005/gmd"
         //    xmlns:gco="http://www.isotc211.org/2005/gco"        
         //            
         //    <gmd:identificationInfo>
         //      <gmd:MD_DataIdentification>
         //         <gmd:citation>
         //            <gmd:CI_Citation>
         //               <gmd:title>
         //                  <gco:CharacterString>TEST GeoBatch Action: GeoNetwork</gco:CharacterString>
         final Namespace NS_GMD = Namespace.getNamespace("gmd","http://www.isotc211.org/2005/gmd");
         final Namespace NS_GCO = Namespace.getNamespace("gco","http://www.isotc211.org/2005/gco");

         Element idInfo = metadata.getChild("identificationInfo", NS_GMD);        
         Element dataId = idInfo.getChild("MD_DataIdentification", NS_GMD);
         Element cit    = dataId.getChild("citation", NS_GMD);
         Element cicit  = cit.getChild("CI_Citation", NS_GMD);
         Element title  = cicit.getChild("title", NS_GMD);
         Element chstr  = title.getChild("CharacterString", NS_GCO);
         
         return chstr;
     }
     
     
 	/**
 	 *Takes an XML Element object (i.e. ISO 19115/191389 definition) and returns the URL of the object 
 	 * @param metadata: a metadata record
 	 * @result result: metadata Element
 	 */
     //Function from GN lib
     public Element getLocationElement(Element metadata) {
         //    xmlns:gmd="http://www.isotc211.org/2005/gmd"
         //    xmlns:gco="http://www.isotc211.org/2005/gco"        
         //            
         //    <gmd:identificationInfo>
         //      <gmd:MD_DataIdentification>
         //         <gmd:citation>
         //            <gmd:CI_Citation>
         //               <gmd:title>
         //                  <gco:CharacterString>TEST GeoBatch Action: GeoNetwork</gco:CharacterString>
         final Namespace NS_GMD = Namespace.getNamespace("gmd","http://www.isotc211.org/2005/gmd");
         final Namespace NS_GCO = Namespace.getNamespace("gco","http://www.isotc211.org/2005/gco");

         Element idInfo = metadata.getChild("distributionInfo", NS_GMD);        
         Element dataId = idInfo.getChild("MD_Distribution", NS_GMD);
         Element cit    = dataId.getChild("transferOptions", NS_GMD);
         Element cicit  = cit.getChild("MD_DigitalTransferOptions", NS_GMD);
         Element title  = cicit.getChild("onLine", NS_GMD);
         Element location  = title.getChild("CI_OnlineResource", NS_GMD);
         Element linkage  = location.getChild("linkage", NS_GMD);
         Element urllocation  = linkage.getChild("URL", NS_GMD);
         
         return urllocation;
     }
     
       
        
      
      
     
 	/**
 	 * loads a file by String location
 	 * @param file: a file of the metadata record
 	 * @result result: theFile File
 	 */    
     protected File loadFile(String name) {
     	/*
         try {
             URL url = this.getClass().getClassLoader().getResource(name);
             if(url == null)
                 throw new IllegalArgumentException("GeoNetwork: cant get file '"+name+"'");
             File file = new File(url.toURI());
             return file;
         } catch (URISyntaxException e) {
             System.out.println("GeoNetwork: can't load file " + name);
             return null;
         }    */    	

             File file = new File(name);
             return file;
     }
     
     
 	/**
 	 * Takes an XML file, parses it and returns a JDOM XML Element obect
 	 * @param file: a file of the metadata record
 	 * @result result: metadata Element
 	 */
     //Function from GN lib
     private static Element parseFile(File file) throws Exception {
         try{
 			SAXBuilder builder = new SAXBuilder();
 			Document doc = builder.build(file);
 			return  (Element)doc.getRootElement().detach();
 		} catch (Exception ex) {
             throw new Exception ("Error parsing input file " + file, ex);
 		}
     }    
     
     
	
	//Write data to a file for debugging of requests and responses
	private void dumpTextToFile(String textToDump, String outputName) {
		try {	        
			Date date = new Date() ;
			UUID uuid = UUID.randomUUID();
			
			//String randomUUIDString = uuid.toString(); //Create unique
			long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();//Create near unique 
			String randomUUIDString = Long.toString(l, Character.MAX_RADIX);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			File file = new File(tempDir + dateFormat.format(date) +"_" + randomUUIDString +"_"+ outputName +".xml") ;
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(textToDump);
			out.close();
		} catch (IOException iox) {
			//do stuff with exception
			iox.printStackTrace();
		}
	}
}
