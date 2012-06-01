package com.storage.app;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/reindex")
@Produces(MediaType.TEXT_PLAIN)
@Consumes({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
public class ReindexService {
	
	@GET
	public String reindexFiles() {
		IndexService.getInstance().reindexFiles();
		return "Reindex done successfully!!!";
	}
	
	

}
