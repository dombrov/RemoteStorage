package com.storage.app.resources;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "files")
@XmlAccessorType(XmlAccessType.FIELD)
public class Files {
	
	@XmlElement
	private int dirId;
	
	@XmlElement
	private int dirParentId;
	
	@XmlElement(name = "file")
	private Collection<FileDescriptor> files;
	
	public Files() {
	}
	
	public Files(Collection<FileDescriptor> files, DirectoryTree directory) {
		this.files = files;
		dirId = directory.getId();
		dirParentId = directory.getParent();
	}

	public Collection<FileDescriptor> getFiles() {
		return files;
	}
	
	public void setFiles(Collection<FileDescriptor> files) {
		this.files = files;
	}

	public int getDirId() {
		return dirId;
	}

	public void setDirId(int dirId) {
		this.dirId = dirId;
	}

	public int getDirParentId() {
		return dirParentId;
	}

	public void setDirParentId(int dirParentId) {
		this.dirParentId = dirParentId;
	}
	
	
	

}
