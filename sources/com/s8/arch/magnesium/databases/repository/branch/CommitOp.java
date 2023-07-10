package com.s8.arch.magnesium.databases.repository.branch;

import java.io.IOException;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitOp extends RequestH3MgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	
	public final NdObject[] objects;
	
	
	public final String comment;


	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitOp(long timestamp, S8User initiator,
			MgBranchHandler branchHandler,  NdObject[] objects, String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		super(timestamp, initiator);
		this.branchHandler = branchHandler;
		this.objects = objects;
		this.comment = comment;
		this.onSucceed = onSucceed;
		this.options = options;
	}

	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return branchHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(branchHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+branchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {
				BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();

				long version = branch.commit(objects, timeStamp, initiator.getUsername(), comment);
				output.version = version;

				onSucceed.call(output);
				return true;
			}

			@Override
			public void catchException(Exception exception) {
				BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};
	}


}
