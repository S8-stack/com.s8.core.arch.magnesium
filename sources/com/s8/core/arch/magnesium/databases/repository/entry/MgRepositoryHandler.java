package com.s8.core.arch.magnesium.databases.repository.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.repository.store.RepoMgStore;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.io.json.types.JSON_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends H3MgHandler<MgRepository> {
	
	public final static String METADATA_FILENAME = "repo-meta.js";
	
	
	
	private final IOModule ioModule;
	
	public final RepoMgStore store;
	
	public final String address;
	
	public final Path folderPath;

	
	public MgRepositoryHandler(SiliconEngine ng, RepoMgStore store, String address) throws JSON_CompilingException {
		super(ng);
		this.store = store;
		this.address = address;
		this.folderPath = store.composeRepositoryPath(address);
		ioModule = new IOModule(this);
	}

	
	/**
	 * 
	 * @return
	 */
	public RepoMgStore getStore() {
		return store;
	}


	@Override
	public String getName() {
		return "repository handler of: "+address;
	}

	@Override
	public H3MgIOModule<MgRepository> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		MgRepository repository = getResource();
		if(repository != null) { 
			return repository.crawl();
		}
		else {
			return new ArrayList<>();
		}
	}


	public Path getFolderPath() {
		return folderPath;
	}
	
	
	public Path getMetadataFilePath() {
		return folderPath.resolve(METADATA_FILENAME);
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepo(long t, S8User initiator, DbMgCallback callback, 
			MgRepositoryHandler targetRepositoryHandler, ForkRepositoryS8Request request) {
		pushOpLast(new ForkRepoOp(t, initiator, callback, this, targetRepositoryHandler, request));
	}
	

	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, DbMgCallback callback, ForkBranchS8Request request) {
		pushOpLast(new ForkBranchOp(t, initiator, callback, this, request));
	}
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, DbMgCallback callback, CommitBranchS8Request request) {
		pushOpLast(new CommitBranchOp(t, initiator, callback, this, request));
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, DbMgCallback callback, CloneBranchS8Request request) {
		pushOpLast(new CloneBranchOp(t, initiator, callback, this, request));
	}


	

	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, DbMgCallback callback, GetRepositoryMetadataS8Request request) {
		pushOpLast(new GetRepoMetadataOp(t, initiator, callback, this, request));
	}


	public void getBranchMetadata(long t, S8User initiator, DbMgCallback callback, GetBranchMetadataS8Request request) {
		pushOpLast(new GetBranchMetadataOp(t, initiator, callback, this, request));
	}
	

}