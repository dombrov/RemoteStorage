package com.storage.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.WebApplicationException;

import com.storage.app.resources.DirectoryTree;
import com.storage.app.resources.FileDescriptor;
import com.storage.app.resources.Files;
import com.storage.app.utils.DateFormatUtil;
import com.storage.app.utils.IndexGenerator;
import com.storage.app.utils.JAXBBuilder;

public class IndexService {
	
	private final static IndexService instance = new IndexService();
	
	private File directoriesIndexFile;
	private String indexFileName = "_index.xml";
	private String indexDirName = "_dir_index.xml";
	
	private DirectoryTree directoryTree;
	
	private IndexService() {
		File rootDir = ContextLoader.getContainerPath();
		directoriesIndexFile = new File(rootDir, indexDirName);
	}
	
	public static IndexService getInstance() {
		return instance;
	}
	
	/**
	 * 
	 */
	public void reindexFiles() {
		try {
			File rootDir = ContextLoader.getContainerPath();
			IndexGenerator.resetIndex();
			int rootIndex = IndexGenerator.nextIndex();
			directoryTree = new DirectoryTree(rootIndex, rootIndex, rootDir.getName());
			indexDirectoryFiles(rootDir, directoryTree);
			
			JAXBBuilder.toXML(directoryTree, directoriesIndexFile);
			
		} catch (FileNotFoundException e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * 
	 * @param directoryTree
	 */
	public void reindexDirectory(DirectoryTree directoryTree) {
		try {
			File dir = IndexService.getInstance().getFile(directoryTree);
			indexDirectoryFiles(dir, directoryTree);
			
			JAXBBuilder.toXML(directoryTree, directoriesIndexFile);
			
		} catch (FileNotFoundException e) {
			throw new WebApplicationException(e);
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public Files getIndexedFiles(DirectoryTree dirDetails) {
		File dir = IndexService.getInstance().getFile(dirDetails);
		File indexFile = new File(dir, indexFileName);
		if (!indexFile.exists()) {
			Files files = new Files();
			files.setDirId(dirDetails.getId());
			files.setDirParentId(dirDetails.getId());
			files.setFiles(new ArrayList<FileDescriptor>());
			return files;
		}
		
		Files files = JAXBBuilder.fromXML(new File(dir, indexFileName), Files.class);
		
		if (files.getFiles() == null) {
			files.setFiles(new ArrayList<FileDescriptor>());
		}
		return files;
	}

	/**
	 * 
	 * @param id
	 * @param dir
	 * @return
	 */
	public FileDescriptor getIndexedFile(int id, DirectoryTree dirDetails) {
		Files files = getIndexedFiles(dirDetails);
		for (FileDescriptor file : files.getFiles()) {
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
			return ContextLoader.getContainerPath();
		} else {
			File rootDir = ContextLoader.getContainerPath();
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
	
	private void indexDirectoryFiles(File dir, DirectoryTree directoryTree) throws FileNotFoundException {
		
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
				DirectoryTree child = directoryTree.getChild(fileHead.getName());
				if (child == null) {
					child = new DirectoryTree(IndexGenerator.nextIndex(), directoryTree.getId(), fileHead.getName());
				}
				directoryTree.addChild(child);
				
				fileHead.setId(child.getId());
				
				indexDirectoryFiles(file, child);
			}
		}

		File reindexFile = new File(dir, indexFileName);
		JAXBBuilder.toXML(new Files(filesToStore, directoryTree), reindexFile);

	}
	
	private DirectoryTree getDirectoryTree() {
		if (directoryTree == null) {
			if (directoriesIndexFile.exists()) {
				directoryTree = JAXBBuilder.fromXML(directoriesIndexFile, DirectoryTree.class);	
			} else {
				int rootIndex = IndexGenerator.nextIndex();
				directoryTree = new DirectoryTree(rootIndex, rootIndex, directoriesIndexFile.getParentFile().getName());
			}
			
		}
		return directoryTree;
	}
	

}
