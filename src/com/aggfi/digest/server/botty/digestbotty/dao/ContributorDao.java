package com.aggfi.digest.server.botty.digestbotty.dao;

import com.aggfi.digest.server.botty.digestbotty.model.Contributor;

public interface ContributorDao {

	public Contributor save(Contributor entry);

	public Contributor get(String participantId);

}
