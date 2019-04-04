package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static pikachusrevenge.gui.MapView.ZOOM;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.resources.Resource;

public class Pokemon extends Unit {
     
    private static final int FOLLOW_DISTANCE = 45;
    private static final int MAX_DISTANCE = 330;
    
    private int id;
    private boolean found;
    private JLabel label;
    private TilePosition tpos;
    private int distance;

    public Pokemon(Model model, TilePosition tpos, int id, boolean found) {
        this(model, tpos, id);
        this.found = found;
    }
    
    public Pokemon(Model model, TilePosition tpos, int id) {
        super(model);
        
        this.id = (id == 0) ? newRandomId() : id;
        this.name = POKEMON_NAME[this.id-1];
        this.found = false;
        this.tpos = tpos;
        this.speed = model.getPlayer().getSpeed();
        this.direction = Direction.STOP;
        setIdImg();
    }
    
    public void found() {
        this.found = true;
        revealLabel();
        putToPosition(TilePosition.tileCenter(tpos));
        startLooping();
    }
    
    public void revealLabel(){
        if (label != null) {
            BufferedImage image = null;
            try {image = Resource.loadBufferedImage(String.format("pokemons\\icon%03d.png",this.id));} 
            catch (IOException e) {System.err.println("Can't load file");} 
            image = Resource.getSprite(image, 0, 0);
            image = Resource.getScaledImage(image, 30, 30);

            label.setIcon(new ImageIcon(image));
        }
    }
    
    private void setIdImg() {
        setImg(String.format("pokemons\\%03d.png",this.id));
    }

    @Override
    protected void loadNextPosition(){
        Position playerPosition = model.getPlayer().getPosition();
        double playerDistance = playerPosition.distanceFrom(pos);
        if (playerDistance > MAX_DISTANCE) {
            nextDirection = Direction.getDirection(pos, playerPosition);
            startWalking();
            super.loadNextPosition();
        } else if (playerDistance > distance && model.canMoveTo(pos,Direction.getDirection(pos, playerPosition),speed)) {
            nextDirection = Direction.getDirection(pos, playerPosition);
            startWalking();
            super.loadNextPosition();
        } else if (playerDistance > distance && model.canMoveTo(pos,Direction.getSecondDirection(pos, playerPosition),speed)) {
            nextDirection = Direction.getSecondDirection(pos, playerPosition);
            startWalking();
            super.loadNextPosition();
        } else {
            stopWalking();
        }  
    }
    
    private int newRandomId() {
        int maxPokemonCount = POKEMON_NAME.length;
        int randomId = new Random().nextInt(maxPokemonCount-1) + 1;
        int i = 0;
        if (model.getAllIds().size() < maxPokemonCount-1) {
            while (i < maxPokemonCount-1) {
                i++;
                if (model.getAllIds().contains(randomId) || randomId == 25) {
                    randomId = randomId % maxPokemonCount + 1;
                } else {
                    break;
                }
            }
        }
        return randomId;
    }
    
    @Override
    public void startLooping() {
        int i = 1;
        for (Pokemon p : model.getMapPokemons()) {
            if (p.isFound()) {
                p.setDistance((int)((double)FOLLOW_DISTANCE * ZOOM * (double)i));
                i++;
            }
        }
        super.startLooping();
    }

    public int getId() {return id;}
    public TilePosition getTilePosition() {return tpos;}
    public boolean isFound() {return found;} 
    public void setNotFound() {this.found = false;}
    public void setLabel(JLabel label) {this.label = label;}
    public void setDistance(int distance) {this.distance = distance;}
    
    public static final String[] POKEMON_NAME = {"Bulbasaur",
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
            
