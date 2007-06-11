/**
 * WindowViewer.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.owasp.jbrofuzz.ui.viewers;

import java.awt.event.*;

import javax.swing.*;

import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;
/**
 * <p>Class extending a JFrame for displaying fuzzed results in a linear 
 * graph.</p>
 * <p>This class launched a JFrame inside the "TCP Fuzzing" Panel.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 * @since 0.6
 */
public class WindowPlotter extends JFrame {
	// The main frame window
	private JBRFrame m;
	// The name of the JFrame displayed as a title
	private String name;

	// The x size of the frame
	private static final int x = 750;
	// The y size of the frame
	private static final int y = 450;

	private int [] y_data = null;
	private String [] x_data = null;

	private DataPlot _dataPlot;

	public WindowPlotter(JBRFrame m, String name) {
		super(name);
		this.name = name;
		this.m = m;
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		
		y_data = FileHandler.getFuzzDirFileHashes(this.m);
		x_data = FileHandler.getFuzzDirFileNames(this.m);

		normaliseData();

		GraphicsPlotCanvas plotCanvas = createPlotCanvas();

		_dataPlot = new DataPlot();
		_dataPlot.addElement(new DataCurve(""));
		plotCanvas.connect(_dataPlot);
		setLayout(new BorderLayout());
		add(plotCanvas.getGraphicsCanvas(), BorderLayout.CENTER);
		// Global frame issues
		setLocation(180, 140);
		setSize(x, y);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(2);

		drawData();
	}

	private GraphicsPlotCanvas createPlotCanvas() {
		int xMin = 0;
		int xMax = y_data.length + 1;
		int yMin = 0;
		int yMax = 1000;

		Properties props = new Properties();
		ConfigParameters config
		= new ConfigParameters(new PropertiesBasedConfigData(props));
		props.put("foreground", "0xffffff");
		props.put("background", "0");

		props.put("plot/legendVisible", "false");
		props.put("plot/coordinateSystem/xAxis/minimum", "" + xMin);
		props.put("plot/coordinateSystem/xAxis/maximum", "" + xMax);
		props.put("plot/coordinateSystem/xAxis/axisLabel", "Fuzzing Instance File Generated");

		props.put("plot/coordinateSystem/yAxis/minimum", "" + yMin);
		props.put("plot/coordinateSystem/yAxis/maximum", "" + yMax);
		props.put("plot/coordinateSystem/yAxis/axisLabel", "Normalised Fuzzing Hash Value [0 - 1000]");

		props.put("plot/curveFactory/definitions", "curve");
		props.put("plot/curveFactory/curve/withLine", "true");

		props.put("plot/initialHintForNextCurve/className", "jcckit.plot.PositionHint");
		props.put("plot/initialHintForNextCurve/position", "0 0.1");

		props.put("plot/curveFactory/curve/initialHintForNextPoint/className", "jcckit.plot.ShapeAttributesHint");
		props.put("plot/curveFactory/curve/initialHintForNextPoint/initialAttributes/fillColor", "0x00090");
		props.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", "0.0 0.0 0.018");
		props.put("plot/curveFactory/curve/withLine", "true");
		props.put("plot/curveFactory/curve/symbolFactory/className", "jcckit.plot.CircleSymbolFactory");
		props.put("plot/curveFactory/curve/symbolFactory/size", "0.015");
		
		StringBuffer xAxisMap = new StringBuffer();
		for(int i=0; i < x_data.length; i++) {
			if(i == 0) {
				xAxisMap.append("0=0;");
			} else {
				if(xAxisMap.length() < 100000) {
					xAxisMap.append((i+1) + "=" + x_data[i] + ";");
				}
			}
		}
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat/className", "jcckit.plot.TicLabelMap");
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat/map", xAxisMap.toString());
		return new GraphicsPlotCanvas(config);
	}

	private void drawData() {
		DataCurve curve = new DataCurve("");
		for (int i = 0; i < y_data.length; i++) {
			curve.addElement(new DataPoint(i, 0));
			int x = i + 1;
			int y = y_data[i];
			curve.replaceElementAt(i, new DataPoint(x, y));
		}
		_dataPlot.replaceElementAt(0, curve);
	}

	private void normaliseData() {
		int norm = y_data[0];
		for(int i = 0; i < y_data.length; i++) {
			y_data[i] = Math.abs((y_data[i] - norm + 10) % 1000);
		}
	}

}	// Frame class