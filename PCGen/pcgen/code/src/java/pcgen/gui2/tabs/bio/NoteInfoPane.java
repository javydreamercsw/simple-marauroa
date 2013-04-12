/*
 * NoteInfoPane.java
 * Copyright James Dempsey, 2012
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 04/03/2012 4:24:46 PM
 *
 * $Id: NoteInfoPane.java 17418 2012-09-23 00:33:48Z jdempsey $
 */
package pcgen.gui2.tabs.bio;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.DescriptionFacade;
import pcgen.core.facade.NoteFacade;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.TextFieldListener;
import pcgen.system.LanguageBundle;

/**
 * The NoteInfoPane displays a named text area that the user can fill in for her
 * character. This allows the creation of custom notes about the character.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2012-09-22 19:33:48 -0500 (Sat, 22 Sep 2012) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 17418 $
 */
@SuppressWarnings("serial")
public class NoteInfoPane extends JPanel implements CharacterInfoTab
{
	private final TabTitle title;
	private final JTextField nameField; 
	private final JTextArea noteField;
	private final JButton removeButton;
	private String name;
	private final NoteFacade note; 

	/**
	 * Create a new instance of NoteInfoPane
	 * @param note The note we are to manage.
	 */
	public NoteInfoPane(NoteFacade note)
	{
		this.note = note;
		this.nameField = new JTextField(15);
		this.name = note.getName();
		this.noteField = new JTextArea(8, 20);
		this.title = new TabTitle(name);
		this.removeButton = new JButton(LanguageBundle.getString("in_descDelNote")); //$NON-NLS-1$
		nameField.setEditable(!note.isRequired());
		removeButton.setEnabled(!note.isRequired());
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		Box hbox = Box.createHorizontalBox();
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(new JLabel(LanguageBundle.getString("in_descNoteName"))); //$NON-NLS-1$
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(nameField);
		nameField.setText(name);
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(removeButton);
		hbox.add(Box.createHorizontalGlue());

		noteField.setLineWrap(true);
		noteField.setWrapStyleWord(true);
		
		add(hbox, BorderLayout.NORTH);
		JScrollPane pane =
				new JScrollPane(noteField,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(pane, BorderLayout.CENTER);
	}
	
	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(NoteHandler.class, new NoteHandler(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	/**
	 * A NoteHandler handles changes to the note text or name as well as 
	 * removeButton button presses.
	 */
	private class NoteHandler implements ActionListener
	{

		private final DescriptionFacade descFacade;

		public NoteHandler(CharacterFacade character)
		{
			descFacade = character.getDescriptionFacade();
			noteField.setText(note.getValue());
			
			new TextFieldListener(nameField)
			{
				@Override
				protected void textChanged(String text)
				{
					descFacade.renameNote(note, text);
					name = text;
				}

			};
			
			new TextFieldListener(noteField)
			{
				@Override
				protected void textChanged(String text)
				{
					descFacade.setNote(note, text);
				}

			};
			removeButton.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			descFacade.deleteNote(note);
		}

	}

	/**
	 * @return the name
	 */
	public NoteFacade getNote()
	{
		return note;
	}

}
