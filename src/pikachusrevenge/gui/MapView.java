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
import static pikachusrevenge.gui.Frame.CUT;
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

    private void paintUnitsBottom(Graphics2D g){     
        for (PokeBall ball : model.getThrownBalls()){
            Position pos = ball.getPosition();
            int size = (int) ((double)BALLSIZE*ZOOM);
            g.drawImage(ball.getImg(), (int)pos.x - size/2, (int)pos.y - size/2, size, size,null);
        }
        
        for (NPC npc : model.getNpcs()){
            Position pos = npc.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            int cut = (int) ((double)CUT*ZOOM);
            g.drawImage(npc.getBottomSprite(),(int)pos.x - size/2, (int)pos.y - size/2 + cut, size , size-cut,null);
        }
        
        for (Pokemon p : model.getMapPokemons()){
            Position pos = p.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            int cut = (int) ((double)CUT*ZOOM);
            g.drawImage(p.getBottomSprite(),(int)pos.x - size/2, (int)pos.y - size/2 + cut, size , size-cut,null);
        }
        
        Player player = model.getPlayer();
        Position pos = player.getPosition();
        int size = (int) ((double)SPRITE_SIZE*ZOOM);
        int cut = (int) ((double)CUT*ZOOM);
        g.drawImage(player.getBottomSprite(),(int)pos.x - size/2, (int)pos.y - size/2 + cut, size , size-cut,null);
    }

    private void paintUnitsTop(Graphics2D g){     
        for (NPC npc : model.getNpcs()){
            Position pos = npc.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            int cut = (int) ((double)CUT*ZOOM);
            int exSize = (int) ((double)EXCLAMATION_SIZE*ZOOM);
            g.drawImage(npc.getTopSprite(),(int)pos.x - size/2, (int)pos.y - size/2, size, cut, null);
            if (npc.seesPlayer()) g.drawImage(npc.getExclamation(),(int)pos.x - size/2 + 17,(int)pos.y - size/2-exSize+14,exSize,exSize,null);
        }
        
        for (Pokemon p : model.getMapPokemons()){
            Position pos = p.getPosition();
            int size = (int) ((double)SPRITE_SIZE*ZOOM);
            int cut = (int) ((double)CUT*ZOOM);
            g.drawImage(p.getTopSprite(),(int)pos.x - size/2, (int)pos.y - size/2, size, cut, null);
        }
        
        Player player = model.getPlayer();
        Position pos = player.getPosition();
        int size = (int) ((double)SPRITE_SIZE*ZOOM);
        int cut = (int) ((double)CUT*ZOOM);
        g.drawImage(player.getTopSprite(),(int)pos.x - size/2, (int)pos.y - size/2, size, cut, null);  
    } 
    
    @Override
    public void paintComponent(Graphics g) {
        //final Graphics2D g2d = (Graphics2D) g.create();
        Graphics2D g2d = (Graphics2D)g;
        final Rectangle clip = g2d.getClipBounds();

        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("Above")) paintUnitsBottom(g2d);   
            if (layer instanceof TileLayer) renderer.paintTileLayer(g2d, (TileLayer) layer);
            //else if (layer instanceof ObjectGroup) renderer.paintObjectGroup(g2d, (ObjectGroup) layer);      
        }
        paintUnitsTop(g2d);  
    }

}