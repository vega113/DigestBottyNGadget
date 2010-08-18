package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.Date;

import com.aggfi.digest.server.botty.digestbotty.model.ComplReplyProb;

public interface ComplReplyProbDao {

	ComplReplyProb save(ComplReplyProb entry);

	ComplReplyProb getComplReplyProb(String projectId, Date forDate);

}
