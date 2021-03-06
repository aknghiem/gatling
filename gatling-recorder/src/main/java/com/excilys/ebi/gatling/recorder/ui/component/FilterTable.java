/**
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.recorder.ui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.excilys.ebi.gatling.recorder.configuration.Pattern;
import com.excilys.ebi.gatling.recorder.ui.enumeration.PatternType;

@SuppressWarnings("serial")
public class FilterTable extends JPanel implements MouseListener {

	private DefaultTableModel model = new DefaultTableModel();
	private JTable table = new JTable();

	public FilterTable() {
		model.addColumn("Filter");
		model.addColumn("Style");

		table.setModel(model);
		this.setLayout(new GridLayout(1, 1));
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, 0, 0);

		initPopupMenu();
		TableColumn styleColumn = table.getColumn("Style");
		styleColumn.setCellRenderer(new RadioButtonRenderer());
		styleColumn.setCellEditor(new RadioButtonEditor());
		styleColumn.setMinWidth(150);
		styleColumn.setMaxWidth(150);
		table.setRowHeight(30);

		scrollPane.addMouseListener(this);
	}

	public void validateCells() {
		stopCellEditing();
		List<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0; i < model.getRowCount(); i++) {
			if (("").equals((String) model.getValueAt(i, 0))) {
				toRemove.add(i);
			}
		}
		removeRows(toRemove);
		ensureUnicity();
	}

	public void removeRows(List<Integer> toRemove) {
		Integer[] selected = new Integer[toRemove.size()];
		selected = toRemove.toArray(selected);
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--)
			model.removeRow(selected[i]);
	}

	public void stopCellEditing() {
		if (table.isEditing() && table.getSelectedRow() != -1)
			table.getCellEditor().stopCellEditing();
	}

	public void ensureUnicity() {
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getRowCount(); j++) {
				if (i != j && getPattern(i).equals(getPattern(j))) {
					model.removeRow(j);
				}
			}
		}
	}

	public void setEnabled(boolean enabled) {
		if (!enabled) {
			table.setEnabled(false);
			table.setBackground(Color.LIGHT_GRAY);
		} else {
			table.setEnabled(true);
			table.setBackground(Color.WHITE);
		}
	}

	public void addRow() {
		stopCellEditing();
		model.addRow(new Object[] { "" });
	}

	public void addRow(Pattern pattern) {
		model.addRow(new Object[] { pattern.getPattern(), new CustomPanel(pattern.getPatternType()) });
	}

	public void removeSelectedRow() {
		int[] selected = getSelectedRows();
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--)
			model.removeRow(selected[i]);
	}

	public void removeAllElements() {
		while (model.getRowCount() > 0)
			model.removeRow(0);
	}

	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}

	public int getRowCount() {
		return model.getRowCount();
	}

	public Pattern getPattern(int row) {
		Pattern p = new Pattern(getPatternTypeAt(row), (String) model.getValueAt(row, 0));
		return p;
	}

	private PatternType getPatternTypeAt(int row) {
		CustomPanel p = (CustomPanel) table.getValueAt(row, 1);
		if (p == null)
			return null;
		return p.isFirstSelected() ? PatternType.JAVA : PatternType.ANT;
	}

	private void initPopupMenu() {
		// Create the popup menu
		final JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("delete");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedRow();
			}
		});
		popup.add(menuItem);

		table.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		stopCellEditing();
	}
}

class RadioButtonRenderer implements TableCellRenderer {

	public RadioButtonRenderer() {
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			value = new CustomPanel();
			table.setValueAt(value, row, 1);
		}
		return (CustomPanel) value;
	}
}

@SuppressWarnings("serial")
class RadioButtonEditor extends AbstractCellEditor implements TableCellEditor {

	CustomPanel customPanel = new CustomPanel();

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value == null) {
			value = new CustomPanel();
			table.setValueAt(value, row, 1);
		}
		customPanel = (CustomPanel) value;
		return customPanel;
	}

	public Object getCellEditorValue() {
		return customPanel;
	}

}

@SuppressWarnings("serial")
class CustomPanel extends JPanel {

	JRadioButton radio1 = new JRadioButton("Java", true);
	JRadioButton radio2 = new JRadioButton("Ant", false);

	public CustomPanel() {
		this(PatternType.JAVA);
	}

	public CustomPanel(PatternType patternType) {
		this.add(radio1);
		this.add(radio2);
		ButtonGroup group = new ButtonGroup();
		group.add(radio1);
		group.add(radio2);
		if (patternType == PatternType.JAVA) {
			radio1.setSelected(true);
		} else {
			radio2.setSelected(true);
		}
	}

	public boolean isFirstSelected() {
		return radio1.isSelected();
	}

}