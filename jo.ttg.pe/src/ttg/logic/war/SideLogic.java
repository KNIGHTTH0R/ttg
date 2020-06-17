package ttg.logic.war;

import java.util.Iterator;

import ttg.beans.war.GameInst;
import ttg.beans.war.SideInst;
import ttg.beans.war.WorldInst;

public class SideLogic
{
    public static void victoryPoints(SideInst side, int delta)
    {
    	if (side != null)
        	side.setVictoryPoints(side.getVictoryPoints() + delta);
    }

    public static void addResources(SideInst side, int amnt)
    {
        side.setResources(side.getResources() + amnt);
    }
    
    public static int getResourceGeneration(GameInst game, SideInst side)
    {
    	int ret = 0;
    	for (Iterator i = side.getWorlds().iterator(); i.hasNext(); )
    	{
    		WorldInst world = (WorldInst)i.next();
    		ret += WorldLogic.getResourceGeneration(game, world);
    	}
    	return ret;
    }
    
	public static int getMaxTech(SideInst side)
	{
		int ret = 0;
		for (Iterator i = side.getWorlds().iterator(); i.hasNext(); )
		{
			WorldInst world = (WorldInst)i.next();
			if (world.getWorld() != null)
				ret = Math.max(ret, world.getWorld().getPopulatedStats().getUPP().getTech().getValue()); 
		}
		return ret;
	}
}