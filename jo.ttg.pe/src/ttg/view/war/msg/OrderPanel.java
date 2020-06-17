/*
 * Created on Mar 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ttg.view.war.msg;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import ttg.beans.war.PlayerMessage;
import ttg.beans.war.SideInst;
import ttg.logic.war.IconLogic;
import ttg.view.war.SideRenderer;
import ttg.view.war.WarButton;
import ttg.view.war.WarPanel;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OrderPanel extends JPanel
{
	private WarPanel		mPanel;
	private PlayerMessage	mMessage;
	
	private JList		mSides;
	private JButton		mOK;
	
	/**
	 *
	 */

	public OrderPanel(WarPanel panel)
	{
		mPanel = panel;
		initInstantiate();
		initLink();
		initLayout();
	}

	private void initInstantiate()
	{
		mSides = new JList();
		mSides.setCellRenderer(new SideRenderer());
		mOK = new WarButton("OK", IconLogic.mButtonDone);
	}

	private void initLink()
	{
        ListenerUtils.listen(mOK, (ev) -> doOK());
		mSides.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				doSideAction();			
			}
		});
	}

	private void initLayout()
	{
		setLayout(new TableLayout("anchor=w"));
		add("1,+ fill=h", new JLabel("Player Order:"));
		add("1,+ fill=hv weighty=30", new JScrollPane(mSides));
		add("1,+ fill=h", mOK);
	}
	
	public void setMessage(PlayerMessage msg)
	{
		mMessage = msg;
		if (mMessage == null)
		{
			mSides.setListData(new Object[0]);
			mOK.setEnabled(false);
		}
		else
		{
			mSides.setListData(((ArrayList)msg.getArg1()).toArray());
			mOK.setEnabled(true);
		}
	}

	/**
	 * 
	 */
	protected void doOK()
	{
		mPanel.setMode(WarPanel.DONE);
	}

	/**
	 * 
	 */
	protected void doSideAction()
	{
		SideInst side = (SideInst)mSides.getSelectedValue();
		if (side != null)
			mPanel.getInfoPanel().setObject(side);
	}
}
