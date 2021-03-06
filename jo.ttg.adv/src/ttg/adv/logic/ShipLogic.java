/*
 * Created on Jan 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ttg.adv.logic;

import java.util.ArrayList;
import java.util.List;

import jo.ttg.beans.DateBean;
import jo.ttg.beans.dist.DistCapabilities;
import jo.ttg.beans.trade.CargoBean;
import jo.ttg.logic.DateLogic;
import jo.ttg.ship.beans.ShipBean;
import jo.ttg.ship.beans.ShipBlockBean;
import jo.ttg.ship.beans.ShipStats;
import jo.ttg.ship.beans.comp.ShipComponent;
import jo.ttg.ship.logic.BlockGen;
import jo.ttg.ship.logic.ShipModify;
import jo.ttg.ship.logic.ShipReport;
import ttg.adv.beans.AdvCargoBean;
import ttg.adv.beans.Game;
import ttg.adv.beans.ShipInst;

/**
 * @author jjaquinta
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ShipLogic
{
	public static void selectDestination(Game game, String uri)
	{
		ShipInst si = game.getShip();
		si.setDestination(uri);
	}

	private static String[] mDefaultShipComponents =
	{
	    "100t Hull",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Stateroom",
	    "Low Berth",
	    "Low Berth",
	    "Low Berth",
	    "Low Berth",
	    "Radio",
	    "Active EMS",
	    "Passive EMS",
	    "Missile Turret",
	    "Sandcaster Turret",
	    "Computer",
	    "Jump Drive",
	    "Jump Drive",
	    "Maneuver Drive",
	    "Maneuver Drive",
	    "Airlock",
	    "20t Fuel Tank",
	    "100MW Power Plant",
	    "200MW Power Plant",
	    "Head's Up Holodisplay",
	    "Head's Up Holodisplay",
	};
	
    /**
     * @param ship
     */
    public static ShipBean makeDefaultShip()
    {
        ShipBean ship = new ShipBean();
        ship.setName("Good Fortune");
        List<ShipComponent> newComps = new ArrayList<>();
        for (int i = 0; i < mDefaultShipComponents.length; i++)
        {
            ShipBlockBean block = BlockGen.getBlock(mDefaultShipComponents[i], 14);
            if (block == null)
                throw new IllegalArgumentException("Cannot find block '"+mDefaultShipComponents[i]+"' while makeing default ship");
            newComps.add(block);
        }
        ShipModify.replace(ship, newComps);
        return ship;
    }
    
    public static DistCapabilities getCaps(ShipStats stats)
    {
        DistCapabilities caps = new DistCapabilities();
		caps.setVolume(stats.getDisplacement());
		caps.setAcceleration(stats.getManeuver());
		double fuel = stats.getFuel();
		double duration = stats.getDurationHours()*60;
		caps.setFuelPerMinute(fuel/duration);
        return caps;
    }

    /**
     * @param game
     * @param newName
     */
    public static void renameShip(Game game, String newName)
    {
	    game.getShip().setName(newName);
	    game.getShip().getStats().setCraftName(newName);
    }
    
    public static boolean isSufficientCrew(ShipInst ship)
    {
        int[] haveCrew = CrewLogic.totalCrew(ship);
        int[] needCrew = CrewLogic.neededCrew(ship);
        for (int i = 0; i < needCrew.length; i++)
        {
            if (needCrew[i] > haveCrew[i])
                return false;
        }
        return true;
    }
    
    public static boolean isNoCargoPending(DateBean now, ShipInst ship)
    {
        for (CargoBean cargo : ship.getCargo())
            if (cargo.getDestination().equals(ship.getLocation()) && DateLogic.earlierThan(now, ((AdvCargoBean)cargo).getDelivered()))
                return false;
        return true;
    }

    /**
     * @param game
     * @param selling
     */
    public static void sellComponents(Game game, List<ShipComponent> selling)
    {
        ShipInst ship = game.getShip();
        StringBuffer sb = new StringBuffer();
        for (ShipComponent block : selling)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(block.getName());
            ship.getDesign().getComponents().remove(block);
        }
        double price = jo.ttg.ship.logic.ShipLogic.totalCost(selling);
        MoneyLogic.creditToCash(game, price, "Selling ship's components: "+sb.toString());
        ship.setStats(ShipReport.report(ship.getDesign()));
    }

    /**
     * @param game
     * @param buying
     */
    public static void buyComponents(Game game, List<ShipComponent> buying)
    {
        ShipInst ship = game.getShip();
        double price = 0;
        StringBuffer sb = new StringBuffer();
        for (ShipComponent block : buying)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(block.getName());
            ship.getDesign().getComponents().add(block);
            price += jo.ttg.ship.logic.ShipLogic.totalCost((ShipBlockBean)block);
        }
        MoneyLogic.debitFromCash(game, price, "Buying ship's components: "+sb.toString());
        ship.setStats(ShipReport.report(ship.getDesign()));
    }
}
