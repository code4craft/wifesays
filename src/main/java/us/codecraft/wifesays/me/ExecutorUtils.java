package us.codecraft.wifesays.me;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
public class ExecutorUtils {

	private static Logger LOGGER = Logger.getLogger(ExecutorUtils.class);

	/**
	 * �??�?��?��???���??�??�??使�?{@link ExecutorService#execute(Runnable)}?��??�线�??已满�??�??�?��线�?
	 * 
	 * @param size
	 *            大�?
	 * @return
	 */
	public static ExecutorService newBlockingExecutors(int size) {
		return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(1) {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					/**
					 * (non-Jsdoc)
					 * 
					 * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
					 */
					@Override
					public boolean offer(Runnable e) {
						try {
							this.put(e);
						} catch (Exception e1) {
							LOGGER.warn("offer error ", e1);
						}
						return true;
					}
				});
	}
}