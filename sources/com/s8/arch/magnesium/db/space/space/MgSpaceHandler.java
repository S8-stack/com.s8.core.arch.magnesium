package com.s8.arch.magnesium.db.space.space;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.db.space.store.MgS1Store;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.magnesium.handlers.h3.H3MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.branches.LiBranch;

public class MgSpaceHandler extends H3MgHandler<LiBranch> {

	
	private final MgS1Store store;
	
	private final IOModule ioModule = new IOModule(this);
	
	
	private final String id;
	
	private final Path path;
	
	public MgSpaceHandler(SiliconEngine ng, MgS1Store store, String id, Path path) {
		super(ng);
		this.store = store;
		this.id = id;
		this.path = path;
	}

	@Override
	public String getName() {
		return "workspace hanlder";
	}

	@Override
	public H3MgIOModule<LiBranch> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<H3MgUnmountable> unmountables) {
		// final node
	}

	public Path getPath() {
		return path;
	}

	public MgS1Store getStore() {
		return store;
	}

	public String getIdentifier() {
		return id;
	}
	
	

	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposed(long t, int slot, ObjectMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposedOp(t, this, slot, onSucceed, onFailed));
	}
	
	
	public void accessExposure(long t, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposureOp(t, this, onSucceed, onFailed));
	}


}