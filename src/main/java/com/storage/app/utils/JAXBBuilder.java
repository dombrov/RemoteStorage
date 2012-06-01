package com.storage.app.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBBuilder {
	
	public static <T extends Object> void toXML(T object, File outFile) {
    	try {
			JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			marshaller.marshal(object, outFile);
			
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static <T extends Object> T fromXML(File file, Class<T> objClass) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T) unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
    }
}
