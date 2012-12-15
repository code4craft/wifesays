package us.codecraft.wifesays.me;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class ShutDownMonitor implements StandReady {

	private List<ShutDownAble> shutDownList;

	private int delay = 1;

	private Logger logger = Logger.getLogger(getClass());

	private ExecutorService shutDownExecutors = ExecutorUtils
			.newBlockingExecutors(4);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#whatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (Commands.SHUTDOWN.equalsIgnoreCase(whatWifeSays)) {
			for (final ShutDownAble shutDownAble : shutDownList) {
				shutDownExecutors.execute(new Runnable() {

					@Override
					public void run() {
						try {
							shutDownAble.shutDown();
						} catch (Throwable e) {
							logger.warn("oops!My ears!", e);
						}
					}
				});
			}
			logger.info("Application will shut down in " + delay
					+ " seconds...");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(delay * 1000);
						logger.info("Shutting down success.");
						System.exit(0);
					} catch (Throwable e) {
						logger.error("Shutting down failed", e);
					}
				}
			}).start();
			return "success";
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#whatWillYouDo()
	 */
	@Override
	public Class<? extends JobTodo> whatKindOfJobWillYouDo() {
		return ShutDownAble.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#setJobs(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setJobs(List<? extends JobTodo> jobs) {
		shutDownList = (List<ShutDownAble>) jobs;
	}

}
