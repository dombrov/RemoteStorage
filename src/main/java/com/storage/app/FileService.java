package com.storage.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.WebServiceException;

import com.storage.app.resources.DirectoryTree;
import com.storage.app.resources.FileDetails;
import com.storage.app.resources.Files;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/rep")
@Consumes({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
public class FileService {
	
	
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;
	
	/**
	 * Get the file list of container's root directory  
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Files getFileList() {
		return readFiles(IndexService.getInstance().getRootDirectoryIndex());
	}

	/**
	 * Get the file list of directory with {dirId} id.
	 * @param dirId
	 * @return
	 */
	@GET
	@Path("{dir}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Files getFile(@PathParam("dir") String dirId) {
		DirectoryTree dirDetails = IndexService.getInstance().getDirectoryIndex(Integer.valueOf(dirId));
		return readFiles(dirDetails);
	}
	

	/**
	 * Get file defined by {fileId} from {dirId} directory
	 * @param dirId
	 * @param fileID
	 * @return
	 */
	@GET
	@Path("{dir}/{fileid}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("dir") String dirId, 
			@PathParam("fileid") String fileID) {
		
		DirectoryTree dirDetails = getIndexService().getDirectoryIndex(Integer.valueOf(dirId));
		FileDetails file = getIndexService().getIndexedFile(Integer.valueOf(fileID), dirDetails);
		
		File dir = getIndexService().getFile(dirDetails);
		return Response.ok(new File(dir, file.getName()))
				.header("content-disposition","attachment; filename = " + file.getName())
				.build();
	}

	/**
	 * Upload file in directory specified by {dirId}
	 * @param dirIdToUpload
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("dirId") String dirIdToUpload,
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("fileName") String fileName) {
		
		if (fileName == null) {
			fileName = fileDetail.getFileName();
		}
		
		DirectoryTree dirDetails = getIndexService().getDirectoryIndex(Integer.valueOf(dirIdToUpload));
		File dir = getIndexService().getFile(dirDetails);
		File destFile = new File(dir, fileName);
		
		// save it
		writeToFile(uploadedInputStream, destFile);
		
		getIndexService().reindexDirectory(dirDetails);
 
		String output = "File uploaded successfully";
 
		return Response.ok().entity(output).build();
 
	}

	/**
	 * Container reindex
	 * @return
	 */
	@GET
	@Path("/reindex")
	@Produces(MediaType.TEXT_PLAIN)
	public String reindexFiles() {
		getIndexService().reindexFiles();
		return "Reindex done successfully!!!";
	}
	
 
	/**
	 * save file
	 * @param uploadedInputStream
	 * @param uploadedFileLocation
	 */
	private void writeToFile(InputStream uploadedInputStream, File destFile) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(destFile);
			int read = 0;
			byte[] bytes = new byte[1024];
 
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new WebServiceException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
 
	}
	
	
	
	private Files readFiles(DirectoryTree dirDetails) {
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		String uri = uriBuilder.path(getClass()).build().toASCIIString();
		
		Files files = IndexService.getInstance().getIndexedFiles(dirDetails);
		
		for (FileDetails file : files.getFiles()) {
			if (file.isDirectory()) {
				file.setUri(uri + "/" + file.getId());
			} else {
				file.setUri(uri + "/" + dirDetails.getId() + "/" + file.getId());
			}
		}
	 return files;
		
	}
	
	private IndexService getIndexService() {
		return IndexService.getInstance();
	}
	
	
	
}
