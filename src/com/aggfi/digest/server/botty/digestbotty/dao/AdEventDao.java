package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.Date;
import java.util.List;

import com.aggfi.digest.server.botty.digestbotty.model.AdEvent;


public interface AdEventDao {

	AdEvent save(AdEvent entry);

	List<AdEvent> getEventsDuringDate(String projectId, Date startDate, String viewPostEventType);


	List<AdEvent> getEventsDuringPeriod(String projectId, Date fromDate,
			Date toDate, String viewPostEventType);

}
