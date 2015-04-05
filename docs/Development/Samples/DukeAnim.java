/*
 * @(#)DukeAnim.java	1.6  98/12/03
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.net.URL;


/**
 * The DukeAnim class displays an animated gif with a transparent background.
 */
public class DukeAnim extends JApplet implements ImageObserver {

    private static Image agif, clouds;
    private static int aw, ah, cw;
    private int x;
    private BufferedImage bimg;


    public void init() {
        setBackground(Color.white);
        clouds = getDemoImage("clouds.jpg");
        agif = getDemoImage("duke.running.gif");
        aw = agif.getWidth(this) / 2;
        ah = agif.getHeight(this) / 2;
        cw = clouds.getWidth(this);
    }


    public Image getDemoImage(String name) {
        URL url = DukeAnim.class.getResource(name);
        Image img = getToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {}
        return img;
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        if ((x -= 3) <= -cw) {
            x = w;
        }
        g2.drawImage(clouds, x, 10, cw, h-20, this);
        g2.drawImage(agif, w/2-aw, h/2-ah, this);
    }


    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        } 
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }


    public void paint(Graphics g) {
	Dimension d = getSize();
        Graphics2D g2 = createGraphics2D(d.width, d.height);
        drawDemo(d.width, d.height, g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }


    // overrides imageUpdate to control the animated gif's animation
    public boolean imageUpdate(Image img, int infoflags,
                int x, int y, int width, int height)
    {
        if (isShowing() && (infoflags & ALLBITS) != 0)
            repaint();
        if (isShowing() && (infoflags & FRAMEBITS) != 0)
            repaint();
        return isShowing();
    }
  

    public static void main(String argv[]) {
        final DukeAnim demo = new DukeAnim();
        demo.init();
        JFrame f = new JFrame("Java 2D(TM) Demo - DukeAnim");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }
}
