package ttg.war.view.info;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import jo.ttg.beans.mw.MainWorldBean;
import jo.ttg.core.ui.swing.logic.FormatUtils;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.ui.swing.utils.MouseUtils;
import ttg.war.beans.GameInst;
import ttg.war.beans.ShipInst;
import ttg.war.beans.SideInst;
import ttg.war.beans.WorldInst;
import ttg.war.logic.ShipLogic;
import ttg.war.view.HelpPanel;
import ttg.war.view.SideRenderer;
import ttg.war.view.WarPanel;

public class GameInstInfoPanel extends HelpPanel
{
    private InfoPanel       mInfo;
    private GameInst        mGame;

    private JLabel          mName;
    private JLabel          mTurn;
    private JList<SideInst> mSides;
    private JLabel          mPoints;
    private JLabel          mPopulation;
    private JLabel          mShips;
    private JButton         mInstInfo;

    /**
     *
     */

    public GameInstInfoPanel(WarPanel panel, InfoPanel info)
    {
        mPanel = panel;
        mInfo = info;
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mName = new JLabel();
        mTurn = new JLabel();
        mPoints = new JLabel();
        mPopulation = new JLabel();
        mShips = new JLabel();
        mSides = new JList<>();
        mSides.setCellRenderer(new SideRenderer());
        mInstInfo = new JButton("Scenario Info");
    }

    private void initLink()
    {
        ListenerUtils.change(mSides, (ev) -> doSelect());
        MouseUtils.mouseClicked(mSides, (ev) -> {
            if (ev.getClickCount() == 2)
                doSideAction();
        });
        ListenerUtils.listen(mInstInfo,
                (ev) -> mInfo.setObject(mGame.getGame()));
    }

    private void initLayout()
    {
        setLayout(new TableLayout("anchor=w"));
        add("1,+ 2x1 fill=h", makeTitle("Game Info", "InfoGameInst.htm"));
        add("1,+", new JLabel("Name:"));
        add("1,+ 2x1 fill=h", mName);
        add("1,+", new JLabel("Turn:"));
        add("+,. fill=h", mTurn);
        add("1,+", new JLabel("Sides:"));
        add("1,+ 2x1 weighty=20 fill=hv", mSides);
        add("1,+", new JLabel("Points:"));
        add("+,. fill=h", mPoints);
        add("1,+", new JLabel("Population:"));
        add("+,. fill=h", mPopulation);
        add("1,+", new JLabel("Ships:"));
        add("+,. fill=h", mShips);
        add("2,+ fill=h", mInstInfo);
    }

    public void setObject(GameInst obj)
    {
        mGame = obj;
        if (mGame == null)
        {
            mName.setText(null);
            mTurn.setText(null);
            mPoints.setText(null);
            mPopulation.setText(null);
            mShips.setText(null);
            mSides.setListData(new SideInst[0]);
        }
        else
        {
            mName.setText(mGame.getGame().getName());
            mTurn.setText(
                    mGame.getTurn() + " of " + mGame.getGame().getGameLength());
            SideInst[] sides = new SideInst[mGame.getSides().size()];
            mGame.getSides().toArray(sides);
            // sort sides
            for (int i = 0; i < sides.length - 1; i++)
                for (int j = i + 1; j < sides.length; j++)
                    if (sides[i].getVictoryPoints() < sides[j]
                            .getVictoryPoints())
                    {
                        SideInst tmp = sides[i];
                        sides[i] = sides[j];
                        sides[j] = tmp;
                    }
            mSides.setListData(sides);
            mSides.setSelectedIndex(0);
        }
    }

    private void doSelect()
    {
        SideInst side = (SideInst)mSides.getSelectedValue();
        if (side == null)
        {
            mPoints.setText(null);
            mPopulation.setText(null);
            mShips.setText(null);
        }
        else
        {
            mPoints.setText(String.valueOf(side.getVictoryPoints()));
            double pop = 0;
            for (WorldInst world : side.getWorlds())
            {
                MainWorldBean mw = world.getWorld();
                pop += mw.getPopulatedStats().getUPP().getPop().getPopulation();
            }
            mPopulation.setText(FormatUtils.sPopulation(pop));
            int att = 0;
            int def = 0;
            for (ShipInst ship : side.getShips())
            {
                att += ShipLogic.getAttack(ship);
                def += ShipLogic.getDefense(ship);
            }
            mShips.setText("A:" + att + " D:" + def);
        }
    }

    private void doSideAction()
    {
        SideInst side = (SideInst)mSides.getSelectedValue();
        if (side != null)
            mInfo.setObject(side);
    }
}
