package us.codecraft.wifesays.me;

import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
public abstract class StandReadyWorker implements StandReady {

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#whatKindOfJobWillYouDo()
	 */
	@Override
	public Class<? extends JobTodo> whatKindOfJobWillYouDo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#setJobs(java.util.List)
	 */
	@Override
	public void setJobs(List<? extends JobTodo> jobs) {

	}

}
