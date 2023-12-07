package com.s8.core.arch.magnesium.databases.repository.store;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;


/**
 * 
 * @author pc
 *
 */
public class RepoMgDatabase extends H3MgHandler<RepoMgStore> {

	
	public final NdCodebase codebase;
	
	public final IOModule ioModule;

	private Path rootFolderPath;
	
	
	public RepoMgDatabase(SiliconEngine ng, NdCodebase codebase, Path rootFolderPath) throws JSON_CompilingException {
		super(ng);
		this.codebase = codebase;
		this.rootFolderPath = rootFolderPath;
		
		ioModule = new IOModule(this);
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<RepoMgStore> getIOModule() {
		return ioModule;
	}
	

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		RepoMgStore store = getResource();
		if(store != null) { 
			return store.crawl(); 
		}
		else {
			return new ArrayList<>();
		}
	}
	

	public Path getRootFolderPath() {
		return rootFolderPath;
	}
	
	
	public Path getMetadataPath() {
		return rootFolderPath.resolve(RepoMgStore.METADATA_FILENAME);
	}


	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void createRepository(long t, S8User initiator, DbMgCallback callback, CreateRepositoryS8Request request) {
		pushOpLast(new CreateRepoOp(t, initiator, callback, this, request));
	}
	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepository(long t, S8User initiator, DbMgCallback callback, ForkRepositoryS8Request request) {
		pushOpLast(new ForkRepoOp(t, initiator, callback, this, request));
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
	public void getRepositoryMetadata(long t,  S8User initiator, DbMgCallback callback, 
			GetRepositoryMetadataS8Request request) {
		pushOpLast(new GetRepositoryMetadataOp(t, initiator, callback, this, request));
	}
	

	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getBranchMetadata(long t,  S8User initiator, DbMgCallback callback, 
			GetBranchMetadataS8Request request) {
		pushOpLast(new GetBranchMetadataOp(t, initiator, callback, this, request));
	}
	
	
	
	
	/* <utilities> */
	
	public static void init(String rootFolderPathname) throws IOException, JSON_CompilingException {
		RepoMgStoreMetadata metadata = new RepoMgStoreMetadata();
		metadata.rootPathname = rootFolderPathname;
		
		JSON_Lexicon lexicon = JSON_Lexicon.from(RepoMgStoreMetadata.class);
		FileChannel channel = FileChannel.open(Path.of(rootFolderPathname).resolve(RepoMgStore.METADATA_FILENAME), 
				new OpenOption[]{ StandardOpenOption.WRITE, StandardOpenOption.CREATE });
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, metadata, "   ", false);
		writer.close();
	}
	
	
}