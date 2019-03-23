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
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.Player;
import static pikachusrevenge.unit.Unit.UNITSIZE;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

public class MapView extends JPanel {
    
    private final Map map;
    private final Model model;
    private final MapRenderer renderer;

    public static final int GRIDSIZE = 16;
    
    public MapView(Map map, Model model) {
        this.map = map;
        this.model = model;
        this.renderer = new OrthogonalRenderer(map);

        setPreferredSize(renderer.getMapSize());
        setOpaque(true);
    }

    private void paintUnitsBottom(Graphics2D g){
        Player player = model.getPlayer();
        g.drawImage(player.getBottomSprite(),player.getCornerX(),player.getCornerY()+CUT,UNITSIZE,UNITSIZE-CUT,null);
        
        for (PokeBall ball : model.getThrownBalls()){
            g.drawImage(ball.getImg(),ball.getCornerX(),ball.getCornerY(),PokeBall.BALLSIZE,PokeBall.BALLSIZE,null);
        }
        
        for (NPC npc : model.getNpcs()){
            g.drawImage(npc.getBottomSprite(),npc.getCornerX(),npc.getCornerY()+CUT,UNITSIZE,UNITSIZE-CUT,null);
        }
        
        for (Pokemon p : model.getPokemons()){
            if (p.isDrawn()) g.drawImage(p.getBottomSprite(),p.getCornerX(),p.getCornerY()+CUT,UNITSIZE,UNITSIZE-CUT,null);
        }
    }

    private void paintUnitsTop(Graphics2D g){
        Player player = model.getPlayer();
        g.drawImage(player.getTopSprite(),player.getCornerX(),player.getCornerY(),UNITSIZE,CUT,null);        

        for (NPC npc : model.getNpcs()){
            g.drawImage(npc.getTopSprite(),npc.getCornerX(),npc.getCornerY(),UNITSIZE,CUT,null);
        }
        
        for (Pokemon p : model.getPokemons()){
            if (p.isDrawn()) g.drawImage(p.getTopSprite(),p.getCornerX(),p.getCornerY(),UNITSIZE,CUT,null);
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
            if (layer.getName().equals("Above")) paintUnitsBottom(g2d);   
            if (layer instanceof TileLayer) renderer.paintTileLayer(g2d, (TileLayer) layer);
            //else if (layer instanceof ObjectGroup) renderer.paintObjectGroup(g2d, (ObjectGroup) layer);      
        }
        paintUnitsTop(g2d);  
    }

}