package com.storage.app.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_HTML)
public class FilesWriter implements MessageBodyWriter<Files> {
	
	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		return Files.class.isAssignableFrom(arg0);
	}
	
	@Override
	public long getSize(Files arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4) {
		return -1;
	}
	
	@Override
	public void writeTo(Files arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream out)
			throws IOException, WebApplicationException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<body><table width=\"100%\"><tr> <th>File name</th> <th>size</th> <th>last modified</th></tr>");
		
		for (FileDescriptor fileHead : arg0.getFiles()) {
			sb.append("<tr>");
			
			sb.append("<td><a href=\"" + fileHead.getUri() + "\">" + fileHead.getName() + "</a></td>");
			sb.append("<td>" + fileHead.getSize() + "</td>");
			sb.append("<td>" + fileHead.getDateModified() + "</td>");
			
			sb.append("</tr>");
		}
		
		sb.append("</table></body>");
		
		out.write(sb.toString().getBytes());
	}
	
	
	
	

}
