package com.s8.core.arch.magnesium.demos.db;

import java.nio.file.Path;

import com.s8.core.arch.magnesium.db.MgDbSwitcher;
import com.s8.core.arch.magnesium.db.MgResourceStatus;
import com.s8.core.arch.magnesium.db.requests.AccessMgRequest;
import com.s8.core.arch.magnesium.demos.db.resource.MainStubObject;
import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.json.types.JSON_CompilingException;

public class DbTest03 {
	
	
	public static void main(String[] args) throws JSON_CompilingException, InterruptedException {

		SiliconConfiguration siConfiguration = SiliconConfiguration.createDefault4Cores();
		SiliconEngine ng = new SiliconEngine(siConfiguration);
		ng.start();
		
		
		MgDbSwitcher<MainStubObject> db = DbCreator.createDb(ng);
		
		db.processRequest(new AccessMgRequest<MainStubObject>(0, "asset-18672", true) {

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public String describe() {
				return "test-access";
			}

			@Override
			public boolean onResourceAccessed(Path path, MgResourceStatus status, MainStubObject resource) {
				System.out.println(resource.address);
				resource.address = "hobbitland";
				return true;
			}
		});
		
		Thread.sleep(1000);

		
		ng.stop();
		
	}

}
