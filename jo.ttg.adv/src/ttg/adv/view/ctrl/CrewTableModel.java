/*
 * Created on Jan 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ttg.adv.view.ctrl;

import java.util.ArrayList;
import java.util.List;

import jo.ttg.beans.chr.CharBean;
import jo.ttg.core.ui.swing.ctrl.CharTableModel;
import ttg.adv.beans.CrewBean;
import ttg.adv.logic.CrewLogic;

/**
 * @author jjaquinta
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CrewTableModel extends CharTableModel
{
    public static final int COL_JOB = 7;

    private static final String[] mColumnNames = 
	{
		"Job",
	};
	
	public CrewTableModel()
	{
	    super();
	    addColumn(COL_JOB);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col)
	{
		CrewBean ch = (CrewBean)getChars().get(row);
		switch (getColumns()[col])
		{
			case COL_JOB:
			    return CrewLogic.jobNames()[ch.getJob()];
		}
		return super.getValueAt(row, col);
	}

    public Class<?> getColumnClass(int col)
    {
		switch (getColumns()[col])
		{
			case COL_JOB:
				return String.class;
		}
        return super.getColumnClass(col);
    }

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int col)
	{
	    int colOff = getColumns()[col];
	    if (colOff >= COL_JOB)
	        return mColumnNames[colOff - COL_JOB];
	    else
	        return super.getColumnName(col);
	}
	
	public void setCrew(List<CrewBean> crew)
	{
	    List<CharBean> chars = new ArrayList<>();
	    chars.addAll(crew);
	    setCrew(crew);
	}
}
