package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.Date;

import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.Influence;

public interface InfluenceDao {

	Influence save(Influence entry);

	Influence getInfluence(String projectId, Date forDate);

}