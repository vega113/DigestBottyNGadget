package com.aggfi.digest.server.botty.google.forumbotty.admin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.vegalabs.general.server.command.Command;
import com.vegalabs.general.server.command.CommandFetcher;
import com.aggfi.digest.server.botty.google.forumbotty.admin.CommandType;

public class CommandFetcherImpl implements CommandFetcher {
	private Injector injector;
	
	@Inject
	public CommandFetcherImpl(Injector injector){
		this.injector = injector;
	}

	@Override
	public Command fetchCommand(String method) {
		Class<? extends Command> commandClass = CommandType.valueOfIngoreCase(method).getClazz();
		Command command = injector.getInstance(commandClass);
		return command;
	}

}
