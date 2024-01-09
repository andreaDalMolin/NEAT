package ui;

import model.genes.Genome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Frame extends JFrame {
    private Panel panel;
    private Genome genome;

    public Frame(Genome genome) {
        this();
        setGenome(genome);
        this.repaint();
    }

    public void setGenome(Genome genome){
        panel.setGenome(genome);
        this.genome = genome;
    }

    public Frame() throws HeadlessException {
        this.setDefaultCloseOperation(3);

        this.setTitle("NEAT");
        this.setMinimumSize(new Dimension(1000,700));
        this.setPreferredSize(new Dimension(1000,700));

        this.setLayout(new BorderLayout());


        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        try {
            UIManager.setLookAndFeel(looks[3].getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(1000,100));
        menu.setLayout(new GridLayout(1,6));

        JButton buttonB = new JButton("random weight");
        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutateWeightRandom();
                repaint();
            }
        });
        menu.add(buttonB);

        JButton buttonZ = new JButton("weight shift");
        buttonZ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutateWeightShift();
                repaint();
            }
        });
        menu.add(buttonZ);

        JButton buttonC = new JButton("Link mutate");
        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutateLink();
                repaint();
            }
        });
        menu.add(buttonC);

        JButton buttonD = new JButton("Node mutate");
        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutateNode();
                repaint();
            }
        });
        menu.add(buttonD);



        JButton buttonE = new JButton("on/off");
        buttonE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutateLinkToggle();
                repaint();
            }
        });
        menu.add(buttonE);

        JButton buttonF = new JButton("Mutate");
        buttonF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate();
                repaint();
            }
        });
        menu.add(buttonF);

        JButton buttonG = new JButton("Calculate");
        buttonG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                Calculator c = new Calculator(genome);
//                System.out.println(Arrays.toString(c.calculate(1,1,1,1,1,1,1,1,1,1)));
                double[] input = new double[] {1,1,1};
                double[] output = genome.calculateOutput(input);
                System.out.println(Arrays.toString(output));
                repaint();
            }
        });
        menu.add(buttonG);


        this.add(menu, BorderLayout.NORTH);

        this.panel = new Panel();
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
    }
}
