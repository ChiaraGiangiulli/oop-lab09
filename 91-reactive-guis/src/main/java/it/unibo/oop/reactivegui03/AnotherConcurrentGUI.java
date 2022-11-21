package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    protected static final long END = TimeUnit.SECONDS.toMillis(10);
    private final JLabel display = new JLabel();
    private final JButton stopButton = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");


    public AnotherConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JPanel buttons = new JPanel();
        buttons.add(up, BorderLayout.WEST);
        buttons.add(down, BorderLayout.CENTER);
        buttons.add(stopButton, BorderLayout.EAST);
        panel.add(buttons,BorderLayout.NORTH);
        this.getContentPane().add(panel);
        this.setVisible(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(END);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                agent.stopCounting();
            }
        }).start();

        stopButton.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.upCounting());
        down.addActionListener((e) -> agent.downCounting());
    }

    private class Agent implements Runnable{

        private volatile boolean stop;
        private int counter;
        private volatile boolean upCount = true;
        
        @Override
        public void run() {

            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    counter = (upCount == true)? counter + 1 : counter - 1;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void downCounting() {
           this.upCount = false;
        }

        public void upCounting() {
            this.upCount = true;
        }

        public void stopCounting(){
            this.stop = true;
            up.setEnabled(false);
            down.setEnabled(false);
            stopButton.setEnabled(false);
        }
        
    }
}
