package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public class Pokemon extends Unit {
    
    private int id;
    private final int tileX;
    private final int tileY;
    private boolean found;
    private JLabel label;
    
    private static final int FOLLOW_DISTANCE = 50;
    
    public Pokemon(int tileX, int tileY, Model model, JLabel label) {
        this(tileX,tileY,model);
        this.label = label;
        this.id = new Random().nextInt(150) + 1;
        setIdImg();
    }
    
    public Pokemon(int tileX, int tileY, Model model, int id) {
        this(tileX,tileY,model);
        this.id = id;
    }
    
    private Pokemon(int tileX, int tileY, Model model) {
        super(model);
        
        this.found = false;
        this.speed = 8.0;
        this.tileX = tileX;
        this.tileY = tileY;
        
        setStartingPostion(Model.tileCenterFromTileCoord(tileX), Model.tileCenterFromTileCoord(tileY));
        this.direction = Direction.STOP;
    }
    
    public void found() {
        this.nextDirection = Direction.STOP;
        this.found = true;
        revealLabel();
        startMoving();
    }
    
    public boolean isDrawn() {
        return found;
    }
    
    private void revealLabel(){
        BufferedImage image = null;
        try {image = Resource.loadBufferedImage(String.format("pokemons\\icon%03d.png",this.id));} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getSprite(image, 0, 0);
        image = Resource.getScaledImage(image, 30, 30);
        
        label.setIcon(new ImageIcon(image));
    }
    
    private void setIdImg() {
        setImg(String.format("pokemons\\%03d.png",this.id));
    }

    @Override
    protected void loadNextPosition(){
        Position playerPosition = model.getPlayer().getPosition();
        double distance = playerPosition.distanceFrom(pos);
        if (distance > FOLLOW_DISTANCE) {
            nextDirection = Direction.getDirection(pos, playerPosition);
        } else {
            nextDirection = Direction.STOP;
        }
        super.loadNextPosition();        
    }

    public int getId() {return id;}
    public int getTileX() {return tileX;}
    public int getTileY() {return tileY;}
    
    public static final String[] pokemonName = {"Bulbasaur",
                                                "Ivysaur",
                                                "Venusaur",
                                                "Charmander",
                                                "Charmeleon",
                                                "Charizard",
                                                "Squirtle",
                                                "Wartortle",
                                                "Blastoise",
                                                "Caterpie",
                                                "Metapod",
                                                "Butterfree",
                                                "Weedle",
                                                "Kakuna",
                                                "Beedrill",
                                                "Pidgey",
                                                "Pidgeotto",
                                                "Pidgeot",
                                                "Rattata",
                                                "Raticate",
                                                "Spearow",
                                                "Fearow",
                                                "Ekans",
                                                "Arbok",
                                                "Pikachu",
                                                "Raichu",
                                                "Sandshrew",
                                                "Sandslash",
                                                "Nidoran♀",
                                                "Nidorina",
                                                "Nidoqueen",
                                                "Nidoran♂",
                                                "Nidorino",
                                                "Nidoking",
                                                "Clefairy",
                                                "Clefable",
                                                "Vulpix",
                                                "Ninetales",
                                                "Jigglypuff",
                                                "Wigglytuff",
                                                "Zubat",
                                                "Golbat",
                                                "Oddish",
                                                "Gloom",
                                                "Vileplume",
                                                "Paras",
                                                "Parasect",
                                                "Venonat",
                                                "Venomoth",
                                                "Diglett",
                                                "Dugtrio",
                                                "Meowth",
                                                "Persian",
                                                "Psyduck",
                                                "Golduck",
                                                "Mankey",
                                                "Primeape",
                                                "Growlithe",
                                                "Arcanine",
                                                "Poliwag",
                                                "Poliwhirl",
                                                "Poliwrath",
                                                "Abra",
                                                "Kadabra",
                                                "Alakazam",
                                                "Machop",
                                                "Machoke",
                                                "Machamp",
                                                "Bellsprout",
                                                "Weepinbell",
                                                "Victreebel",
                                                "Tentacool",
                                                "Tentacruel",
                                                "Geodude",
                                                "Graveler",
                                                "Golem",
                                                "Ponyta",
                                                "Rapidash",
                                                "Slowpoke",
                                                "Slowbro",
                                                "Magnemite",
                                                "Magneton",
                                                "Farfetch’d",
                                                "Doduo",
                                                "Dodrio",
                                                "Seel",
                                                "Dewgong",
                                                "Grimer",
                                                "Muk",
                                                "Shellder",
                                                "Cloyster",
                                                "Gastly",
                                                "Haunter",
                                                "Gengar",
                                                "Onix",
                                                "Drowzee",
                                                "Hypno",
                                                "Krabby",
                                                "Kingler",
                                                "Voltorb",
                                                "Electrode",
                                                "Exeggcute",
                                                "Exeggutor",
                                                "Cubone",
                                                "Marowak",
                                                "Hitmonlee",
                                                "Hitmonchan",
                                                "Lickitung",
                                                "Koffing",
                                                "Weezing",
                                                "Rhyhorn",
                                                "Rhydon",
                                                "Chansey",
                                                "Tangela",
                                                "Kangaskhan",
                                                "Horsea",
                                                "Seadra",
                                                "Goldeen",
                                                "Seaking",
                                                "Staryu",
                                                "Starmie",
                                                "Mr. Mime",
                                                "Scyther",
                                                "Jynx",
                                                "Electabuzz",
                                                "Magmar",
                                                "Pinsir",
                                                "Tauros",
                                                "Magikarp",
                                                "Gyarados",
                                                "Lapras",
                                                "Ditto",
                                                "Eevee",
                                                "Vaporeon",
                                                "Jolteon",
                                                "Flareon",
                                                "Porygon",
                                                "Omanyte",
                                                "Omastar",
                                                "Kabuto",
                                                "Kabutops",
                                                "Aerodactyl",
                                                "Snorlax",
                                                "Articuno",
                                                "Zapdos",
                                                "Moltres",
                                                "Dratini",
                                                "Dragonair",
                                                "Dragonite",
                                                "Mewtwo",
                                                "Mew"};
}
            
