package com.storage.app.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dir")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectoryTree {
	
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	private int parent;
	
	@XmlAttribute
	private String name;
	
	@XmlElement(name = "dir")
	private List<DirectoryTree> childs = new ArrayList<DirectoryTree>();
	
	public DirectoryTree() {
	}
	
	public DirectoryTree(int id, int parent, String name) {
		this.id = id;
		this.parent = parent;
		this.name = name;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getParent() {
		return parent;
	}
	
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	public List<DirectoryTree> getChilds() {
		return childs;
	}
	
	public void setChilds(List<DirectoryTree> childs) {
		this.childs = childs;
	}
	
	public void addChild(DirectoryTree node) {
		childs.add(node);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
