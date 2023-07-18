package trawel.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeContext {

	public final ContextType owntype;
	private ContextType type;
	private List<TimeEvent> events = new ArrayList<TimeEvent>();
    //private final Lock lock = new ReentrantLock();
	public final CanPassTime scope;
	private TimeContext caller;
	
	//TODO: for now, timecontext itself is not thread safe, but the rest of the program
	//is expected to only ever access it in a thread safe way
	//likely, this will be by only accessing global contexts from the main thread, and local contexts do not access each other
	//but are instead resolved by the global context they are in
	
	public TimeContext(ContextType type, CanPassTime scope) {
		this.owntype = type;
		this.scope = scope;
	}
	
	/**
	 * calls with another forced type
	 * most of the time you will just want to not update it until later instead of this
	 * @param parent
	 * @param calltime
	 * @param type
	 * @return
	 */
	public TimeContext call(TimeContext parent, double calltime,ContextType type) {
		this.type = type;
		caller = parent;
		double timeleft = calltime;
		double time;
		while (timeleft > 0) {
			if (timeleft > type.time_span) {
				time = type.time_span;
				timeleft -=type.time_span;
			}else {
				time = timeleft;
				timeleft = 0;
			}
			addEvents(scope.passTime(time, this));
		}
		return this;
	}
	
	public TimeContext call(TimeContext parent, double calltime) {
		return call(parent,calltime,owntype);
	}
	/**
	 * used internally to recursively pull together contexts, which then get processed, and unprocessed ones get passed on
	 * (sometimes they may get altered and then passed on as mere notifications)
	 * @param es
	 */
	private void addEvents(List<TimeEvent> es) {
		if (es == null) {
			return;
		}
		events.addAll(es);
	}
	
	/**
	 * call to process events without popping them
	 */
	public void processEvents() {
		
	}

	/**
	 * used to add local events- for when a context contains recursive areas without their own contexts
	 * @param passTime
	 */
	public void localEvents(List<TimeEvent> es) {
		if (es == null) {
			return;
		}
		events.addAll(es);
	}
	
	/**
	 * give up all our events to another context
	 * @return
	 */
	public List<TimeEvent> pop(){
		processEvents();
		List<TimeEvent> ret = events;
		events = new ArrayList<TimeEvent>();
		return ret;
	}
	
	public TimeContext caller() {
		return caller;
	}
}
