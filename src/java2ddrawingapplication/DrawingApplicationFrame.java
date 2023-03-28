/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame {
    
    private final JFrame frame;
    private Color color1 = new Color(0);
    private Color color2 = new Color(0);
    private String shape;
    static ArrayList<MyShapes> shapesDrawn; 

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private final JPanel firstLine = new JPanel();
    private final JPanel secondLine = new JPanel();
    private final JPanel menu = new JPanel();

    // create the widgets for the firstLine Panel.
    private JLabel shapeLabel = new JLabel("              Shape: ");
    private String[] shapeChoice = {"Line","Oval","Rectangle"};
    private JComboBox<String> shapeBox = new JComboBox<>(shapeChoice);
    private JButton color1Button = new JButton("1st Color...");
    private JButton color2Button = new JButton("2nd Color...");
    private JButton undo = new JButton("Undo");
    private JButton clear = new JButton("Clear");

    //create the widgets for the secondLine Panel.
    private JLabel options = new JLabel("           Options: ");
    private JCheckBox filledBox = new JCheckBox("Filled");
    private JCheckBox gradient = new JCheckBox("Use Gradient");
    private JCheckBox dashed = new JCheckBox("Dashed");
    private JPanel widthHolder = new JPanel();
    private JLabel lineWidth = new JLabel("Line Width: ");
    private JSpinner widthChooser = new JSpinner();
    private JPanel lengthHolder = new JPanel();
    private JLabel dashLength = new JLabel("Dash Length: ");
    private JSpinner lengthChooser = new JSpinner();

    // Variables for drawPanel.
    private DrawPanel drawPanel = new DrawPanel();
    private final JPanel colorJPanel = new JPanel();
    
    // add status label
    private JLabel statusLabel = new JLabel(" ");
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        setLayout(new BorderLayout());
        frame = new JFrame();
        
        // add widgets to panels
        widthHolder.add(lineWidth);
        widthHolder.add(widthChooser);
        lengthHolder.add(dashLength);
        lengthHolder.add(lengthChooser);
        
        // firstLine widgets
        firstLine.setLayout(new GridLayout(1,6));
        firstLine.add(shapeLabel);
        firstLine.add(shapeBox);
        firstLine.add(color1Button);
        firstLine.add(color2Button);
        firstLine.add(undo);
        firstLine.add(clear);
        firstLine.setBackground(Color.cyan);
        
        // secondLine widgets
        secondLine.setLayout(new GridLayout(1,6));
        secondLine.add(options);
        secondLine.add(filledBox);
        secondLine.add(gradient);
        secondLine.add(dashed);
        secondLine.add(widthHolder);
        widthHolder.setBackground(Color.cyan);
        secondLine.add(lengthHolder);
        lengthHolder.setBackground(Color.cyan);
        secondLine.setBackground(Color.cyan);
        // add top panel of two panels
        menu.add(firstLine);
        menu.add(secondLine);
        menu.setBackground(Color.cyan);
        menu.setLayout(new GridLayout(2,1));
        add(menu, BorderLayout.NORTH);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        drawPanel.setBackground(Color.white);
        // forces drawPanel to open at desired size
        drawPanel.setPreferredSize(new Dimension(600,600));
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        // prevents JSpinner from moving to next level when text box goes to double digits
        lengthChooser.setPreferredSize(new Dimension(45,30));
        //add listeners and event handlers
        colorJPanel.setBackground(Color.LIGHT_GRAY);
        // makes the jcolorchooser window appear
        color1Button.addActionListener(
                //Anonymous inner class
                new ActionListener() {
                    
            @Override
            public void actionPerformed(ActionEvent e) {
                 color1 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color", color1);
            }
                }
        );
        color2Button.addActionListener(
                new ActionListener() {
                    
            @Override
            public void actionPerformed(ActionEvent e) {
                 color2 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color", color2);
            }
                }
                
        );
        // resets shapesDrawn list and repaints it with nothing, clearing it
        clear.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapesDrawn = new ArrayList<>();
                drawPanel.repaint();
            }
                });
        // removes the last drawn shape from the list and repaints
        undo.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapesDrawn.remove(shapesDrawn.size() - 1);
                drawPanel.repaint();
            }
                });
    } 

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {

        public DrawPanel()
        {
            shapesDrawn = new ArrayList<MyShapes>();
            // adds mouse function to DrawPanel
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
        
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes Shape: shapesDrawn) {
                Shape.draw(g2d);
            }
    

        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            // gets object selected fromo JComboBox and converts it to a string
            String shapeType = shapeBox.getSelectedItem().toString();
            Stroke stroke;
            Paint paint;
            Point start;
            Point end;
            boolean filled;
            // MyShapes object to allow the specific shape objects to be added to the array list
            MyShapes currentShape;

            public void mousePressed(MouseEvent event)
            {
                // gets inital points
                start = end = new Point(event.getX(), event.getY());
                if (gradient.isSelected()) {
                    paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                } else
                {
                    paint = color1;
                }
                // converts dash value into integer and then into float list
                int dashValue = (Integer) lengthChooser.getValue();
                float dash[] = {dashValue};
                int widthValue = (Integer) widthChooser.getValue();
                if (dashed.isSelected()) {
                     stroke = new BasicStroke(widthValue, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 0);
                } else
                {
                     stroke = new BasicStroke(widthValue, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                // changes filled boolean value
                if (filledBox.isSelected()) {
                    filled = true;
                } else
                {
                    filled = false;
                }
                // checks the chosen shape and creates an object based on it
                if (shapeType.contains("Line")) {
                    MyLine line = new MyLine(start, end, paint, stroke);
                    // makes object a MyShapes object
                    currentShape = line;
                    // sent to the for loop to be drawn
                    shapesDrawn.add(currentShape);
                }
                if (shapeType.contains("Oval")) {
                    MyOval oval = new MyOval(start, end, paint, stroke, filled);
                    currentShape = oval;
                    shapesDrawn.add(currentShape);
                }
                if (shapeType.contains("Rectangle")) {
                    MyRectangle rectangle = new MyRectangle(start, end, paint, stroke, filled);
                    currentShape = rectangle;
                    shapesDrawn.add(currentShape);                            
                }
                
            }

            public void mouseReleased(MouseEvent event)
            {
                end = new Point(event.getX(), event.getY());
                currentShape.setEndPoint(end);
                currentShape = null;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                int x = event.getX();
                int y = event.getY();
                statusLabel.setText("(" + x + "," + y + ")");
                end = new Point(event.getX(), event.getY());
                currentShape.setEndPoint(end);
                repaint();
                
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                // guarantees a refresh of chosen shape
                shapeType = shapeBox.getSelectedItem().toString();
                // gets x and y coordinates of mouse and dispays them in JLabel
                int x = event.getX();
                int y = event.getY();
                statusLabel.setText("(" + x + "," + y + ")");
                repaint();
            }
        }

    }
}
