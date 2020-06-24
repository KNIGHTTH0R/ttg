/*
 * Created on Jan 26, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ttg.logic.adv;

import java.util.ArrayList;
import java.util.HashMap;

import ttg.beans.adv.AdvEvent;
import ttg.beans.adv.Game;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AdvEventLogic
{
    private static HashMap	mHandlers = new HashMap();
    
    private static ArrayList getHandlers(Game game)
    {
        ArrayList handlers = (ArrayList)mHandlers.get(game);
        if (handlers == null)
        {
            handlers = new ArrayList();
            mHandlers.put(game, handlers);
        }
        return handlers;
    }
    
    public static void addEventHandler(Game game, AdvEventHandler handler)
    {
        synchronized (mHandlers)
        {
            getHandlers(game).add(handler);
        }
    }
    
    public static void removeEventHandler(Game game, AdvEventHandler handler)
    {
        synchronized (mHandlers)
        {
            getHandlers(game).remove(handler);
        }
    }
    
    public static void fireEvent(Game game, AdvEvent evt)
    {
        Object[] handlers;
        synchronized (mHandlers)
        {
            handlers = getHandlers(game).toArray();
        }
        for (int i = 0; i < handlers.length; i++)
            ((AdvEventHandler)handlers[i]).advEvent(evt);
    }
    
    public static void fireEvent(Game game, int evt)
    {
        fireEvent(game, new AdvEvent(game, evt));
    }
    
    public static void fireEvent(Game game, int evt, Object noun)
    {
        fireEvent(game, new AdvEvent(game, evt, noun));
    }
    
    public static void fireEvent(Game game, int evt, Object noun, Object verb)
    {
        fireEvent(game, new AdvEvent(game, evt, noun, verb));
    }
}