package com.storage.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.WebApplicationException;

import com.storage.app.resources.DirectoryTree;
import com.storage.app.resources.FileDescriptor;
import com.storage.app.resources.Files;
import com.storage.app.utils.DateFormatUtil;
import com.storage.app.utils.IndexFactory;
import com.storage.app.utils.JAXBBuilder;

public class IndexService {
	
	private final static IndexService instance = new IndexService();
	
	private File directoriesIndexFile;
	private String indexFileName = "_index.xml";
	private String indexDirName = "_dir_index.xml";
	
	private DirectoryTree directoryTree;
	
	private IndexService() {
		ContextLoader cl = ContextLoader.getInstance();
		File rootDir = cl.getContainerPath();
		directoriesIndexFile = new File(rootDir, indexDirName);
	}
	
	public static IndexService getInstance() {
		return instance;
	}
	
	/**
	 * 
	 */
	public synchronized void reindexFiles() {
		ContextLoader cl = ContextLoader.getInstance();
		try {
			File rootDir = cl.getContainerPath();
			int rootIndex = IndexFactory.getIndex();
			directoryTree = new DirectoryTree(rootIndex, rootIndex, rootDir.getName());
			reindexDir(rootDir, directoryTree);
			
			JAXBBuilder.toXML(directoryTree, directoriesIndexFile);
			
		} catch (FileNotFoundException e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * 
	 * @param directoryTree
	 */
	public synchronized void reindexFiles(DirectoryTree directoryTree) {
		try {
			File dir = IndexService.getInstance().getFile(directoryTree);
			reindexDir(dir, directoryTree);
		} catch (FileNotFoundException e) {
			throw new WebApplicationException(e);
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public Collection<FileDescriptor> getIndexedFiles(File dir) {
		Files files = JAXBBuilder.fromXML(new File(dir, indexFileName), Files.class);
		return files.getFiles();
	}

	/**
	 * 
	 * @param id
	 * @param dir
	 * @return
	 */
	public FileDescriptor getIndexedFile(int id, File dir) {
		Collection<FileDescriptor> files = getIndexedFiles(dir);
		for (FileDescriptor file : files) {
			if (file.getId() == id) {
				return file;
			}
		}
		throw new RuntimeException("Cannot find file with id = " + id);
	}
	
	public DirectoryTree getRootDirectoryIndex() {
		return getDirectoryTree();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public DirectoryTree getDirectoryIndex(int id) {
		DirectoryTree dirNode = getDirIndex(getDirectoryTree(), id);
		if (dirNode == null) {
			throw new RuntimeException("Cannot find directory with id = " + id);	
		}
		
		return dirNode;
	}
	
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public File getFile(DirectoryTree dir) {
		if (dir.getId() == dir.getParent()) {
			return ContextLoader.getInstance().getContainerPath();
		} else {
			File rootDir = ContextLoader.getInstance().getContainerPath();
			String path = "";
			while (dir.getId() != dir.getParent()) {
				path = dir.getName() + File.separator + path ;
				dir = getDirectoryIndex(dir.getParent());
			}
			path = rootDir.getAbsolutePath() + File.separator + path;
			return new File(path);	
		}
	}
	

	
	private DirectoryTree getDirIndex(DirectoryTree dTree, int id) {
		if (dTree.getId() == id) {
			return dTree;
		} else {
			for (DirectoryTree child : dTree.getChilds()) {
				DirectoryTree node = getDirIndex(child, id);
				if (node != null) {
					return node;
				}
			}
		}
		return null;
	}
	
	private void reindexDir(File dir, DirectoryTree directoryTree) throws FileNotFoundException {
		
		if (!dir.exists()) {
			return;
		}
		
		SortedSet<FileDescriptor> filesToStore = new TreeSet<FileDescriptor>();
		
		for (File file : dir.listFiles()) {
			
			if (file.getName().equals(indexFileName) || file.getName().equals(indexDirName)) {
				continue;
			}
			
			FileDescriptor fileHead = new FileDescriptor();
			fileHead.setId(filesToStore.size() + 1);
			fileHead.setName(file.getName());
			fileHead.setSize(file.length());
			fileHead.setDateModified(DateFormatUtil.YYYY_MM_DD_HHMM.format(new Date(file.lastModified())));
			fileHead.setDirectory(file.isDirectory());
			
			filesToStore.add(fileHead);

			
			if (file.isDirectory()) {
				DirectoryTree child = new DirectoryTree(fileHead.getId(), directoryTree.getId(), fileHead.getName());
				directoryTree.addChild(child);
				reindexDir(file, child);
			}
		}

		File reindexFile = new File(dir, indexFileName);
		JAXBBuilder.toXML(new Files(filesToStore, directoryTree), reindexFile);

	}
	
	private synchronized DirectoryTree getDirectoryTree() {
		if (directoryTree == null) {
			directoryTree = JAXBBuilder.fromXML(directoriesIndexFile, DirectoryTree.class);
		}
		return directoryTree;
	}


}
