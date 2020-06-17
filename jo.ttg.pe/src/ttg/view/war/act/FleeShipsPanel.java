package ttg.view.war.act;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import ttg.beans.war.ShipInst;
import ttg.beans.war.WorldInst;
import ttg.logic.war.IconLogic;
import ttg.logic.war.ShipLogic;
import ttg.view.war.HelpPanel;
import ttg.view.war.ObjectButton;
import ttg.view.war.ShipRenderer;
import ttg.view.war.WarButton;
import ttg.view.war.WarPanel;

public class FleeShipsPanel extends HelpPanel implements ListSelectionListener
{
	private boolean		mEclipse;
	private ArrayList	mShipList;

	private ObjectButton	mWorld;
	private JLabel	mRound;	
	private JList	mShips;
	private JButton	mFlee;
	private JButton	mFleeCarriers;
	private JButton	mReset;
	private JButton	mDone;
	
	/**
	 *
	 */

	public FleeShipsPanel(WarPanel panel)
	{
		mPanel = panel;
		initInstantiate();
		initLink();
		initLayout();
	}

	private void initInstantiate()
	{		
		mShipList = new ArrayList();
		mWorld = new ObjectButton(mPanel);
		mRound = new JLabel();
		mShips = new JList();
		mShips.setCellRenderer(new ShipRenderer(IconLogic.SEL_TODO));
		mFlee = new WarButton("Flee", IconLogic.mButtonSet);
		mFleeCarriers = new WarButton("Flee Carriers", IconLogic.mButtonSet);
		mReset = new WarButton("Reset", IconLogic.mButtonReset);
		mDone = new WarButton("Done", IconLogic.mButtonDone);
	}

	private void initLink()
	{
		ListenerUtils.listen(mFlee, (ev) -> doFlee(mShips.getSelectedValues()));
		ListenerUtils.listen(mFleeCarriers, (ev) -> doFleeCarriers());
		ListenerUtils.listen(mReset, (ev) -> doReset());
		ListenerUtils.listen(mDone, (ev) -> doDone());
		mShips.addListSelectionListener(this);
	}

	private void initLayout()
	{
		setLayout(new TableLayout("anchor=w"));
		add("1,+ 2x1 fill=h", makeTitle("Flee Ships", "ActionFlee.htm"));
		add("1,+", new JLabel("World:"));
		add("+,. fill=h", mWorld);
		add("2,+ fill=h", mRound);
		add("1,+ 2x1 fill=h", new JLabel("Pick Ships to Flee:"));
		add("1,+ 2x1 fill=hv weighty=30", new JScrollPane(mShips));
		add("1,+ 2x1 fill=h", mFleeCarriers);
		add("1,+ 2x1 fill=h", mFlee);
		add("1,+ 2x1 fill=h", mReset);
		add("1,+ 2x1 fill=h", mDone);
	}
	
	public void init()
	{
		WorldInst world = (WorldInst)mPanel.getArg1();
		mWorld.setObject(world);
		mRound.setText("Round "+mPanel.getGame().getRound());
		mShipList.clear();
		for (Iterator i = world.getShips().iterator(); i.hasNext(); )
		{
			ShipInst ship = (ShipInst)i.next();
			if (ship.getSideInst() != mPanel.getSide())
				continue;
			if (ship.isFleeing())
				continue;
			mShipList.add(ship);
		}
		mShips.setListData(mShipList.toArray());
		mPanel.getInfoPanel().setObject(world);
	}

	protected void doFlee(Object[] ships)
	{
		if (ships.length == 0)
		{
			mPanel.getGame().setStatus("Select some ships first");
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ships.length; i++)
		{
			ShipInst ship = (ShipInst)ships[i];
			ship.setToDo(false);
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(ship.getShip().getName());
		}
		sb.append(" fleeing to outer system");
		mPanel.getGame().setStatus(sb.toString());
		mShips.repaint();
	}
	
	protected void doFleeCarriers()
	{
		ArrayList carriers = new ArrayList();
		for (Iterator i = mShipList.iterator(); i.hasNext(); )
		{
			ShipInst ship = (ShipInst)i.next();
			if (ShipLogic.getAttack(ship) == 0)
				carriers.add(ship);
		}
		doFlee(carriers.toArray());
	}

	protected void doReset()
	{
		WorldInst world = (WorldInst)mPanel.getArg1();
		for (Iterator i = mShipList.iterator(); i.hasNext(); )
		{
			ShipInst ship = (ShipInst)i.next();
			ship.setToDo(true);
		}
		mShips.repaint();
	}

	protected void doDone()
	{
		mPanel.setMode(WarPanel.DONE);
	}

    public void valueChanged(ListSelectionEvent e)
    {
        if (!mEclipse && (mShips.getSelectedValue() != null))
        	mPanel.getInfoPanel().setObject(mShips.getSelectedValue());
    }
}