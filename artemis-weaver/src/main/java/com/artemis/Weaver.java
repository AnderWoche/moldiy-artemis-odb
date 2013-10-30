package com.artemis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.objectweb.asm.ClassReader;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.MetaScanner;
import com.artemis.weaver.ComponentTypeWeaver;

public class Weaver {
	public static final String PACKED_ANNOTATION = "Lcom/artemis/annotations/PackedWeaver;";
	public static final String POOLED_ANNOTATION = "Lcom/artemis/annotations/PooledWeaver;";
	public static final String WOVEN_ANNOTATION = "Lcom/artemis/annotations/internal/Transmuted";

	private static void processClass(ExecutorService threadPool, String file, List<ClassMetadata> processed) {
		
		ClassReader cr = classReaderFor(file);
		ClassMetadata meta = scan(cr);
		
		if (meta.annotation == WeaverType.NONE)
			return;

		threadPool.submit(new ComponentTypeWeaver(file, cr, meta));
		processed.add(meta);
	}
	
	static ClassReader classReaderFor(InputStream file) {
		try {
			return new ClassReader(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static ClassReader classReaderFor(String file) {
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			return classReaderFor(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ClassMetadata scan(ClassReader source) {
		ClassMetadata info = new ClassMetadata();
		source.accept(new MetaScanner(info), 0);
		return info;
	}
}