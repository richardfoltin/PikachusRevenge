package pikachusrevenge.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import javax.swing.JPanel;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.TileLayer;
import org.mapeditor.view.MapRenderer;
import org.mapeditor.view.OrthogonalRenderer;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.unit.NPC;
import static pikachusrevenge.unit.NPC.EXCLAMATION_SIZE;
import static pikachusrevenge.unit.PokeBall.BALLSIZE;
import static pikachusrevenge.unit.Unit.SPRITE_SIZE;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

/**
 * A játékteret megjelenítő panel.
 * @author Csaba Foltin
 */
public final class MapView extends JPanel {
    
    private final Map map;
    private final Model model;
    private final MapRenderer renderer;

    public static final int GRIDSIZE = 16;
    public static final double ZOOM = 0.5;
    
    public MapView(Map map, Model model) {
        this.map = map;
        this.model = model;
        this.renderer = new OrthogonalRenderer(map);

        setPreferredSize(renderer.getMapSize());
        setOpaque(true);
    }

    /**
     * Kirajzolja a játkost, azt őt követő pokémonokat, az NPC-ket és az általuk
     * eldobott pokélabdákat.
     * A pályán, az egyégek felett lévő sprite-ok kirajzolása előtt hívandó.
     * @param g 
     */
    private void paintUnits(Graphics2D g){     
        for (PokeBall ball : model.getThrownBalls()){
            Position pos = ball.getPosition();
            int size = (int) ((double)BALLSIZE*ZOOM);
            g.drawImage(ball.getImg(), (int)pos.x - size/2, (int)pos.y - size/2, size, size,null);
        }
        
        for (NPC npc : model.getNpcs()){
                  
            Position pos = npc.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            g.drawImage(npc.getImg(), (int)pos.x - size/2, (int)pos.y - size/2, size , size,null);
            
        }
        
        for (Pokemon p : model.getMapPokemons()){
            Position pos = p.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            if (p.isFound()) g.drawImage(p.getImg(),(int)pos.x - size/2, (int)pos.y - size/2, size , size,null);
        }
        
        Player player = model.getPlayer();
        Position pos = player.getPosition();
        int size = (int) ((double)SPRITE_SIZE*ZOOM);
        g.drawImage(player.getImg(),(int)pos.x - size/2, (int)pos.y - size/2, size , size,null);
    }
    
    /**
     * Kirajzolja az NPC-khez tartozó látöszögüket jelző ívet, és a fejük felett
     * megjelnő esetleges buborékot.
     * A pályán, az egyégek felett lévő sprite-ok kirajzolása után hívandó.
     * @param g 
     */
    private void paintOverAll(Graphics2D g){  
        for (NPC npc : model.getNpcs()){
            
            // no line of sight at level 9
            if (model.getDifficulty() == Model.Difficulty.CASUAL && model.getActualLevelId() != 9) {
                Arc2D arc = npc.getLos();
                Color c = MainWindow.PIKACHU_RED;
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
                g.draw(arc);
                g.setColor(new Color(255,255,255,50));
                g.fill(arc);

                Arc2D instantArc = npc.getInstantLos();
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g.fill(instantArc);
            }
            
            if (npc.seesPlayer()) {
                Position pos = npc.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
                int exSize = (int) ((double)EXCLAMATION_SIZE*ZOOM);
                g.drawImage(npc.getExclamation(),(int)pos.x - size/2 + 17,(int)pos.y - size/2-exSize+14,exSize,exSize,null);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        final Rectangle clip = g2d.getClipBounds();

        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("Above")) paintUnits(g2d);
            if (layer instanceof TileLayer) renderer.paintTileLayer(g2d, (TileLayer) layer);
            //else if (layer instanceof ObjectGroup) renderer.paintObjectGroup(g2d, (ObjectGroup) layer);      
        } 
        paintOverAll(g2d);
    }

}