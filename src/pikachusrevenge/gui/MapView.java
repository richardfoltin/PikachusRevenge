package pikachusrevenge.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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

public class MapView extends JPanel {
    
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

    private void paintUnits(Graphics2D g){     
        for (PokeBall ball : model.getThrownBalls()){
            Position pos = ball.getPosition();
            int size = (int) ((double)BALLSIZE*ZOOM);
            g.drawImage(ball.getImg(), (int)pos.x - size/2, (int)pos.y - size/2, size, size,null);
        }
        
        for (NPC npc : model.getNpcs()){
            Position pos = npc.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            g.drawImage(npc.getImg(),(int)pos.x - size/2, (int)pos.y - size/2, size , size,null);
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
    
    
    private void paintBubbles(Graphics2D g){  
        for (NPC npc : model.getNpcs()){
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
        //final Graphics2D g2d = (Graphics2D) g.create();
        Graphics2D g2d = (Graphics2D)g;
        final Rectangle clip = g2d.getClipBounds();

        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("Above")) paintUnits(g2d);
            if (layer instanceof TileLayer) renderer.paintTileLayer(g2d, (TileLayer) layer);
            //else if (layer instanceof ObjectGroup) renderer.paintObjectGroup(g2d, (ObjectGroup) layer);      
        } 
        paintBubbles(g2d);
    }

}