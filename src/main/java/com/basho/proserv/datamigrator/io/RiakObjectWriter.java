package com.basho.proserv.datamigrator.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basho.riak.pbc.RiakObject;
import com.basho.riak.pbc.RiakObjectIO;
import com.google.protobuf.ByteString;

public class RiakObjectWriter {
	private final Logger log = LoggerFactory.getLogger(RiakObjectWriter.class);
	
	DataOutputStream dataOutputStream = null;
	RiakObjectIO riakObjectIo = new RiakObjectIO();
	
	public RiakObjectWriter(File file) {
		try {
			this.dataOutputStream = new DataOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File could not be created " + file.getAbsolutePath());
		}

	}
	
	public boolean writeRiakObject(RiakObject riakObject) {
		try {
			riakObjectIo.writeRiakObject(this.dataOutputStream, riakObject);
		} catch (IOException ex) {
			log.error("Could not write RiakObject to outputStream", ex);
			this.close();
			return false;
		}
		
		return true;
	}
	
	public void close() {
		try {
			this.writeEOFRiakObject();
			this.dataOutputStream.flush();
			this.dataOutputStream.close();
		} catch (IOException e) {
			log.error("Could not close RiakObjectWriter file", e);
		}
	}
	
	private void writeEOFRiakObject() throws IOException {
		RiakObject riakObject = new RiakObject(ByteString.copyFromUtf8(""),
											   ByteString.copyFromUtf8(""),
											   ByteString.copyFromUtf8(""),
											   ByteString.copyFromUtf8(""));
		riakObjectIo.writeRiakObject(this.dataOutputStream, riakObject, 255);
	}
	
}
