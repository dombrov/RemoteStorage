package com.storage.app;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.util.ApplicationDescriptor;





public class MainTest extends JerseyTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
    public MainTest() throws Exception {
    	super();
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor();
                appDescriptor.setContextPath("/storage-app");
                appDescriptor.setRootResourcePackageName("com.storage.app");
        super.setupTestEnvironment(appDescriptor);

	}

	/**
	 * Use java reflection to change the container path to a temporary folder 
	 */
	@Override
    public void setUp() throws Exception {
		
		super.setUp();
		
		//setup temporary container
		Class<ContextLoader> contecClass = ContextLoader.class;
		Field contextField = contecClass.getDeclaredField("context");
		contextField.setAccessible(true);
		
		ContextLoader context = (ContextLoader) contextField.get(contecClass);
		Field containerPath = contecClass.getDeclaredField("containerPath");
		containerPath.setAccessible(true);
		containerPath.set(context, temporaryFolder.getRoot());
		
    }
	
	@Test
	public void testFlow () throws Exception {
		
		getRootFilesList();
		
		uploadFile();
		
		checkDirectoryFileList();
		
		//get uploaded file
		getFile();
		
		reindex();
		
	}
	
	
	
	
	public void getRootFilesList() {
		
		System.out.println();
		System.out.println("------ Get root file list ------");
		ClientResponse response = webResource.path("rest").path("rep").accept(
				MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
	}

	
	public void uploadFile() throws FileNotFoundException, JSONException {
		
		System.out.println();
		System.out.println("------ Upoad file ------");
		
		String rootDirFileList = webResource.path("rest").path("rep").accept(
				MediaType.APPLICATION_JSON).get(String.class);
		
		JSONObject json = new JSONObject(rootDirFileList);
		String directoryId = json.getString("dirId");
		
		String fileName = "TestUpload.txt";
		String fileContent = "It is a test";
		
		ByteArrayInputStream is = new ByteArrayInputStream(fileContent.getBytes());
		
		//InputStream stream = new FileInputStream(fileToUpload);
		FormDataMultiPart part = new FormDataMultiPart();
		part.field("file", is, MediaType.TEXT_PLAIN_TYPE);
		part.field("fileName", fileName);
		part.field("dirId", directoryId);

		ClientResponse response  = webResource.path("rest").path("rep").path("upload").type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, part);
		
		Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
		
	}
	
	public void checkDirectoryFileList() throws JSONException {
		
		System.out.println();
		System.out.println("------ Get directory file list ------");
		
		String rootDirFileList = webResource.path("rest").path("rep").accept(
				MediaType.APPLICATION_JSON).get(String.class);
		
		JSONObject json = new JSONObject(rootDirFileList);
		String directoryId = json.getString("dirId");

		
		String dirFileList = webResource.path("rest").path("rep").path(directoryId).accept(
				MediaType.APPLICATION_JSON).get(String.class);
		
		Assert.assertTrue(dirFileList.contains("TestUpload.txt"));
		
	}
	
	public void getFile() throws JSONException {
		
		System.out.println();
		System.out.println("------ Get file ------");
		
		String fileName = "TestUpload.txt";
		String fileContent = "It is a test";

		String rootDirFileList = webResource.path("rest").path("rep").accept(
				MediaType.APPLICATION_JSON).get(String.class);

		JSONObject json = new JSONObject(rootDirFileList);
		String directoryId = json.getString("dirId");

		String fileId = null;

		if (json.get("file") instanceof JSONArray) {
			JSONArray jsonArray = json.getJSONArray("file");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jObj = jsonArray.getJSONObject(i);
				String fn = jObj.getString("@name");
				if (fn.equals(fileName)) {
					fileId = jObj.getString("@id");
				}
			}
			
		} else {
			JSONObject jObj = (JSONObject) json.get("file");
			String fn = jObj.getString("@name");
			if (fn.equals(fileName)) {
				fileId = jObj.getString("@id");
			}
		}
		
		
		Assert.assertFalse(fileId == null);
		
		String response = webResource.path("rest").path("rep").path(directoryId).path(fileId).accept(
				MediaType.APPLICATION_OCTET_STREAM).get(String.class);
		
		Assert.assertEquals(response, fileContent);

	}
	
	
	public void reindex() {
		System.out.println();
		System.out.println("------ Reindex ------");
		ClientResponse response = webResource.path("rest").path("rep").path("reindex").accept(
				MediaType.TEXT_PLAIN).get(ClientResponse.class);
		
		Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
		
	}
	
	
	
	
	
}
