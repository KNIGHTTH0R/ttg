package ttg.war.view.act;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import ttg.war.beans.ShipInst;
import ttg.war.beans.WorldInst;
import ttg.war.logic.IconLogic;
import ttg.war.logic.PhaseLogic;
import ttg.war.logic.ShipLogic;
import ttg.war.view.HelpPanel;
import ttg.war.view.ObjectButton;
import ttg.war.view.ShipRenderer;
import ttg.war.view.WarButton;
import ttg.war.view.WarPanel;

public class TargetShipsPanel extends HelpPanel implements ListSelectionListener
{
    private List<ShipInst>  mShipList;
    private List<ShipInst>  mTargetList;

    private ObjectButton    mWorld;
    private JLabel          mRound;
    private JList<ShipInst> mShips;
    private JList<ShipInst> mTargets;
    private JLabel          mOdds;
    private JButton         mAttack;
    private JButton         mFlee;
    private JButton         mReset;
    private JButton         mDone;

    /**
     *
     */

    public TargetShipsPanel(WarPanel panel)
    {
        mPanel = panel;
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mShipList = new ArrayList<>();
        mTargetList = new ArrayList<>();
        mWorld = new ObjectButton(mPanel);
        mRound = new JLabel();
        mShips = new JList<>();
        mShips.setCellRenderer(new ShipRenderer(IconLogic.SEL_TODO));
        mTargets = new JList<>();
        mTargets.setCellRenderer(new ShipRenderer(IconLogic.SEL_DONT));
        mTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mOdds = new JLabel();
        mAttack = new WarButton("Target", IconLogic.mButtonSet);
        mFlee = new WarButton("Flee", IconLogic.mButtonFlee);
        mReset = new WarButton("Reset", IconLogic.mButtonReset);
        mDone = new WarButton("Done", IconLogic.mButtonDone);
    }

    private void initLink()
    {
        ListenerUtils.listen(mAttack, (ev) -> doAttack());
        ListenerUtils.listen(mFlee, (ev) -> doFlee());
        ListenerUtils.listen(mReset, (ev) -> doReset());
        ListenerUtils.listen(mDone, (ev) -> doDone());
        mShips.addListSelectionListener(this);
        mTargets.addListSelectionListener(this);
    }

    private void initLayout()
    {
        setLayout(new TableLayout("anchor=w"));
        add("1,+ 2x1 fill=h", makeTitle("Target Ships:", "ActionTarget.htm"));
        add("1,+", new JLabel("World:"));
        add("+,. fill=h", mWorld);
        add("1,+ 2x1 fill=h", mRound);
        add("1,+ 2x1 fill=h", new JLabel("Attacking Ships:"));
        add("1,+ 2x1 fill=hv weighty=30", new JScrollPane(mShips));
        add("1,+ 2x1 fill=h", new JLabel("Target Ships:"));
        add("1,+ 2x1 fill=hv weighty=30", new JScrollPane(mTargets));
        add("1,+", new JLabel("Odds:"));
        add("+,. fill=h", mOdds);
        add("1,+ 2x1 fill=h", mAttack);
        add("1,+ 2x1 fill=h", mFlee);
        add("1,+ 2x1 fill=h", mReset);
        add("1,+ 2x1 fill=h", mDone);
    }

    public void init()
    {
        WorldInst world = (WorldInst)mPanel.getArg1();
        mWorld.setObject(world);
        mRound.setText("Round " + mPanel.getGame().getRound());
        mShipList.clear();
        mTargetList.clear();
        for (ShipInst ship : world.getShips())
        {
            if (ship.isFleeing())
                continue;
            if (ship.getSideInst() != mPanel.getSide())
            {
                mTargetList.add(ship);
                ship.setToDo(ship.getTarget() == null);
            }
            else
                mShipList.add(ship);
        }
        if (mTargetList.size() == 0)
        {
            doDone();
            return;
        }
        for (ShipInst ship : mShipList)
        {
            if (!mTargetList.contains(ship.getTarget()))
                ship.setTarget(null);
        }
        mShips.setListData(mShipList.toArray(new ShipInst[0]));
        mTargets.setListData(mTargetList.toArray(new ShipInst[0]));
        mPanel.getInfoPanel().setObject(world);
    }

    protected void doAttack()
    {
        List<ShipInst> ships = mShips.getSelectedValuesList();
        if (ships.size() == 0)
        {
            mPanel.getGame().setStatus("Select some ships first");
            return;
        }
        ShipInst target = mTargets.getSelectedValue();
        if (target == null)
        {
            mPanel.getGame().setStatus("Select target first");
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (ShipInst ship : ships)
        {
            ship.setTarget(target);
            ship.setToDo(false);
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(ship.getShip().getName());
        }
        sb.append(" targeting ");
        sb.append(target.getShip().getName());
        mPanel.getGame().setStatus(sb.toString());
        mShips.repaint();
        mTargets.repaint();
        calcOdds();
    }

    protected void doFlee()
    {
        List<ShipInst> ships = mShips.getSelectedValuesList();
        if (ships.size() == 0)
        {
            mPanel.getGame().setStatus("Select some ships first");
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (ShipInst ship : ships)
        {
            ship.setTarget(ship);
            ship.setToDo(false);
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(ship.getShip().getName());
        }
        sb.append(" fleeing");
        mPanel.getGame().setStatus(sb.toString());
        mShips.repaint();
        calcOdds();
    }

    protected void doReset()
    {
        for (ShipInst ship : mShipList)
        {
            ship.setTarget(null);
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
        if ((e.getSource() == mShips) && (mShips.getSelectedValue() != null))
            mPanel.getInfoPanel().setObject(mShips.getSelectedValue());
        else if ((e.getSource() == mTargets)
                && (mTargets.getSelectedValue() != null))
            mPanel.getInfoPanel().setObject(mTargets.getSelectedValue());
        calcOdds();
    }

    private void calcOdds()
    {
        mOdds.setText("");
        ShipInst target = (ShipInst)mTargets.getSelectedValue();
        if (target == null)
            return;
        int att = 0;
        Object[] ships = mShipList.toArray();
        if (ships.length == 0)
            return;
        for (int i = 0; i < ships.length; i++)
        {
            ShipInst ship = (ShipInst)ships[i];
            if (ship.getTarget() == target)
                att += ShipLogic.getAttack(ship);
        }
        int def = ShipLogic.getDefense(target);
        mOdds.setText(PhaseLogic.getOddsDesc(att, def));
    }
}
