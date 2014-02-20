package core;

/* SWEN20003 Object Oriented Software Development
 * Shadow Wing
 * Author: Matt Giuca <mgiuca>
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** The status panel.
 * Renders itself to a fixed position at the bottom of the screen, displaying
 * the given stats for the player. (See the render() method.)
 */
public class Panel
{
    /** Height of the status panel in pixels. */
    public static final int PANEL_HEIGHT = 70;

    /** Image for the panel background. */
    private Image panel;
    /** Image for the firepower icon. */
    private Image fp_icon;

    /** Creates a new Panel. */
    public Panel() throws SlickException
    {
        panel = new Image(Game.ASSETS_PATH + "/panel.png");
        fp_icon = new Image(Game.ASSETS_PATH + "/items/firepower.png");
    }

    /** Renders the status panel for the player.
     * @param g The current Slick graphics context.
     * @param xo The xoffset to render the panel at
     * @param shield The player's current Shield level.
     * @param full_shield The player's current Full-Shield level.
     * @param firepower The player's current Firepower level.
     */
    public void render(Graphics g, int xo, int view_width, int shield, int full_shield, int firepower)
    {
    	// Panel colours
        Color LABEL = new Color(0.9f, 0.9f, 0.4f);          // Gold
        Color VALUE = new Color(1.0f, 1.0f, 1.0f);          // White
        Color BAR_BG = new Color(0.0f, 0.0f, 0.0f, 0.6f);   // Black, transp
        Color BAR_FS = new Color(1.0f, 0.3f, 0.3f, 0.05f);  // Red, transp
        Color BAR_SH = new Color(0.3f, 0.7f, 0.2f);         // Green

        // Variables for layout
        int panel_top;              // Top y coordinate of panel
        String text;                // Text to display
        int text_x, text_y;         // Coordinates to draw text
        int bar_x, bar_y;           // Coordinates to draw rectangles
        int bar_width, bar_height;  // Size of rectangle to draw
        int full_shield_bar_width;  // Size of red (Full-Shield) rectangle
        int shield_bar_width;       // Size of green (Shield) rectangle
        int fp_x, fp_y;             // Coordinates to draw firepower

        float shield_percent;       // Player's Shield, % of MAX_SHIELD

        panel_top = Game.screenheight - PANEL_HEIGHT;

        // Panel background image
        panel.draw(xo, panel_top);
        
        // Calculate the amount we need to shift right for this to work
    	int lo = (800 - view_width)/2;
        
        // Display the player's shields
        text_x = xo + 15;
        text_y = panel_top + 29;
        
        if(view_width < 800) {
        	text_x = xo+lo;
        }
        
        g.setColor(LABEL);
        g.drawString("Shields:", text_x, text_y);
        text = shield + "/" + full_shield;
        
        bar_x = xo + 100;
        bar_y = panel_top + 24;
        bar_width = 410;
        bar_height = 30;
        
        if(view_width < 800) {
        	bar_x = xo +lo + 70;
        	bar_width = 150;
        }
        
        shield_percent = (float) shield / full_shield;
        full_shield_bar_width = (int) (bar_width);
        shield_bar_width = (int) (bar_width * shield_percent);
        
        text_x = bar_x +
            (full_shield_bar_width - g.getFont().getWidth(text)) / 2;
        g.setColor(BAR_BG);
        g.fillRect(bar_x, bar_y, bar_width, bar_height);
        g.setColor(BAR_FS);
        g.fillRect(bar_x, bar_y, full_shield_bar_width, bar_height);
        g.setColor(BAR_SH);
        g.fillRect(bar_x, bar_y, shield_bar_width, bar_height);
        g.setColor(VALUE);
        g.drawString(text, text_x, text_y);
        
        text_x = xo + 525;
        
        if(view_width < 800) {
        	text_x = xo + lo + 225;
        }
        
        // Display the player's firepower
        g.setColor(LABEL);
        g.drawString("Firepower:", text_x, text_y);
        bar_x = xo + 626;
        bar_y = panel_top + 14;
        bar_width = 152;
        bar_height = bar_height + 20;
        g.setColor(BAR_BG);
        g.fillRect(bar_x, bar_y, bar_width, bar_height);

        fp_x = xo + 626;
        fp_y = panel_top + 4;
        
        if(view_width < 800) {
        	fp_x = xo + lo + 300;
        }
        
        for (int i=0; i<firepower; i++)
        {
            fp_icon.draw(fp_x, fp_y);
            if(view_width >= 800) {
            	fp_x += 40;
            } else {
            	fp_x += 20;
            }
        }
    }
}