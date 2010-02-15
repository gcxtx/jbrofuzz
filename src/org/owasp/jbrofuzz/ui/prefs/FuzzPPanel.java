/**
 * JBroFuzz 1.9
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

class FuzzPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -6307578507750425289L;

	// Declare any static constants
	private static final String[] SOCKET_SECONDS = 
		{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
		 "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		 "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
		 "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
		 "41", "42", "43", "44", "45", "46", "47", "48", "49", "50" };

	// Declare all preference parameter Strings being used
	private static final String PARAM_1 = "Socket.Max.Timeout";
	
	private static final String PARAM_2 = "fuzz.end.of.line";
	
	// The Socket Timeout Combo Box
	private final JComboBox stoBox;
	
	public FuzzPPanel(final PrefDialog dialog) {
		
		super("Fuzzing");
		
		// Fuzzing... -> Socket Timeout

		int stoPrefValue = JBroFuzz.PREFS.getInt(PARAM_1, 7);
		// Validate
		if( (stoPrefValue < 1) || (stoPrefValue > 51) ) {
			stoPrefValue = 7;
		}

		stoBox = new JComboBox(SOCKET_SECONDS);
		stoBox.setSelectedIndex(stoPrefValue - 1);
		stoBox.setMaximumRowCount(5);
		
		// Re-enable the apply button in the event of a change
		stoBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent stoEvent) {
				dialog.setApplyEnabled(true);
			}
		});
		
		final JLabel stoLabel = new JLabel("Specify Socket Connection Timeout (in seconds): ");
		stoLabel.setToolTipText("Increase/Decrease the number of seconds you wait for an open connection");
		
		final JPanel sTimeOutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		// A very important line when it comes to BoxLayout
		sTimeOutPanel.setAlignmentX(0.0f);
		sTimeOutPanel.add(stoLabel);
		sTimeOutPanel.add(stoBox);
		
		add(sTimeOutPanel);
		
		// Fuzzing... -> End of Line Character

		final boolean endlinebox = JBroFuzz.PREFS.getBoolean(PARAM_2,
				false);
		final JCheckBox endlineCheckBox = new JCheckBox(
				" Use \"\\n\" instead of \"\\r\\n\" as an end of line character ",
				endlinebox);

		endlineCheckBox.setBorderPaintedFlat(true);
		endlineCheckBox
		.setToolTipText("Tick this box, if you want to use \"\\n\" for each line put on the wire");

		endlineCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent eolEvt) {
				if (endlineCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(PARAM_2, true);
				} else {
					JBroFuzz.PREFS.putBoolean(PARAM_2, false);
				}
			}
		});
		add(endlineCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));


		// Fuzzing... -> Word wrap request text panel

		final boolean wrap_req_box = JBroFuzz.PREFS.getBoolean(
				JBroFuzzPrefs.WRAP_REQUEST, false);
		final JCheckBox wReqBox = new JCheckBox(
				" Word wrap text in the \"Request\" area (requires restart) ",
				wrap_req_box);
		wReqBox.setBorderPaintedFlat(true);
		wReqBox
		.setToolTipText("If ticked, the request text area will wrap the text to fit the size of the area");

		wReqBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent wReqEvt) {
				if (wReqBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.WRAP_REQUEST, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.WRAP_REQUEST, false);
				}
			}
		});

		add(wReqBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> Word wrap response text panel

		final boolean wrap_res_bool = JBroFuzz.PREFS.getBoolean(
				JBroFuzzPrefs.WRAP_RESPONSE, false);
		final JCheckBox wRespBox = new JCheckBox(
				" Word wrap text in the \"Response\" window (requires restart) ",
				wrap_res_bool);
		wRespBox.setBorderPaintedFlat(true);
		wRespBox
		.setToolTipText("Tick this box, to see all output text wrapped to the size of the response window");

		wRespBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent wrap_res_evt) {
				if (wRespBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.WRAP_RESPONSE, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.WRAP_RESPONSE, false);
				}
			}
		});

		add(wRespBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing ...-> "Re-send POST Data if 100 Continue is received"
		final boolean cont_bool = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[2],
				true);
		final JCheckBox cont_check_box = new JCheckBox(
				"Re-send POST Data if 100 Continue is received", cont_bool);
		cont_check_box.setBorderPaintedFlat(true);
		cont_check_box
		.setToolTipText("Tick this box, to re-send the POST Data in a HTTP/1.1 message, if a 100 continue is received");

		cont_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event5) {
				if (cont_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[2], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[2], false);
				}
			}
		});

		add(cont_check_box);
		add(Box.createRigidArea(new Dimension(0, 20)));

		
		
	}
	
	public void apply() { 
		
		// Fuzzing... -> Socket Timeout
		final int stoIndex = stoBox.getSelectedIndex();
		JBroFuzz.PREFS.putInt(PARAM_1, stoIndex + 1);
		
	}

}