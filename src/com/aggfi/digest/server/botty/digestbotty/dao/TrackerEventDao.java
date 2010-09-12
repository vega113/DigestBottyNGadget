package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.Date;
import java.util.List;

import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;

public interface TrackerEventDao {

	TrackerEvent save(TrackerEvent entry);

	List<TrackerEvent> getEventsDuringDate(String projectId, Date startDate, String viewPostEventType);


	List<TrackerEvent> getEventsDuringPeriod(String projectId, Date fromDate,
			Date toDate, String viewPostEventType);

	List<TrackerEvent> getTrackerEventsFromDate(String projectId, Date target);

	List<TrackerEvent> getTrackerEventsByWaveId(String waveId);

}
