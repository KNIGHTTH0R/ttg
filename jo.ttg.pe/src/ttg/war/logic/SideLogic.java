package ttg.war.logic;

import ttg.war.beans.GameInst;
import ttg.war.beans.SideInst;
import ttg.war.beans.WorldInst;

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
    	for (WorldInst world : side.getWorlds())
    		ret += WorldLogic.getResourceGeneration(game, world);
    	return ret;
    }
    
	public static int getMaxTech(SideInst side)
	{
		int ret = 0;
		for (WorldInst world : side.getWorlds())
			if (world.getWorld() != null)
				ret = Math.max(ret, world.getWorld().getPopulatedStats().getUPP().getTech().getValue()); 
		return ret;
	}
}
