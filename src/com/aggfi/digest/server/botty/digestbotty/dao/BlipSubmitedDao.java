package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.Date;
import java.util.List;

import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;

public interface BlipSubmitedDao {

	public abstract BlipSubmitted save(BlipSubmitted entry);

	public abstract List<BlipSubmitted> getBlipsDuringDate(String projectId, Date target);

	public abstract List<BlipSubmitted> getBlipsFromDate(String projectId, Date target);

	public abstract BlipSubmitted getBlipByWaveIdBlipIdVersion(String projectId, String waveId, String blipId, long version);

	public abstract List<BlipSubmitted> getBlipsDuringPeriod(String projectId, Date fromDate,Date toDate);

}