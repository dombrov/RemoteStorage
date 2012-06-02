package com.storage.app.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "file")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileDetails implements Comparable<FileDetails> {
	
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private long size;
	
	@XmlAttribute
	private String dateModified;
	
	@XmlAttribute
	private boolean isDirectory;
	
	@XmlAttribute
	private String uri;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}
	
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileDetails) {
			FileDetails o = (FileDetails) obj;
			return id == o.id;
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(FileDetails o) {
		if (isDirectory && !o.isDirectory) {
			return -1;
		} else if (!isDirectory && o.isDirectory) {
			return 1;
		} else {
			return name.compareTo(o.name);
		}
	}
	

}
