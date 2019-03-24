package pikachusrevenge.unit;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.resources.Resource;
import static pikachusrevenge.unit.Unit.C_BOX_HEIGHT;
import static pikachusrevenge.unit.Unit.C_BOX_WIDTH;

public class Pokemon extends Unit {
    
    private int id;
    private boolean found;
    private JLabel label;
    private TilePosition tpos;
    private int distance;
    
    private static final int FOLLOW_DISTANCE = 45;
    private static final int MAX_DISTANCE = 280;
    
    public Pokemon(Model model, TilePosition tpos, int id) {
        super(model);
        
        this.id = (id == 0) ? newRandomId() : id;
        this.found = false;
        this.tpos = tpos;
        this.speed = model.getPlayer().getSpeed();
        this.direction = Direction.STOP;
        setIdImg();
    }
    
    public void found() {
        this.found = true;
        for (Pokemon p : model.getMapPokemons()) {
            if (p.found) this.distance += FOLLOW_DISTANCE;
        }
        revealLabel();
        restartFromStratingPoint();
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
    
    public void putNextToPlayer() {
        pos = model.getPlayer().getPosition();
        nextPosition = pos;
        nextDirection = Direction.STOP;
    }
    
    private void setIdImg() {
        setImg(String.format("pokemons\\%03d.png",this.id));
    }

    @Override
    protected void loadNextPosition(){
        Position playerPosition = model.getPlayer().getPosition();
        double distance = playerPosition.distanceFrom(pos);
        if (distance > MAX_DISTANCE) {
            nextDirection = Direction.getDirection(pos, playerPosition);   
        } else if (distance > this.distance) {  
            nextDirection = Direction.getDirection(pos, playerPosition);          
            Position targetPosition = new Position(pos.x + nextDirection.x * speed, pos.y + nextDirection.y * speed);
            Rectangle targetRectangle = new Rectangle(0, 0, C_BOX_WIDTH, C_BOX_HEIGHT);
            moveNextCollisionBoxTo(targetRectangle,targetPosition);
            if (!model.canMoveTo(targetRectangle)) nextDirection = Direction.STOP;
        } else {
            nextDirection = Direction.STOP;
        }
        super.loadNextPosition();        
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
    public void restartFromStratingPoint() {
        Position playerPosition = model.getPlayer().getPosition();
        setStartingPostion(playerPosition.x, playerPosition.y);
        super.restartFromStratingPoint();  
        startMoving();
    }
    
    public int getId() {return id;}
    public TilePosition getTilePosition() {return tpos;}
    public boolean isFound() {return found;} 
    public void setLabel(JLabel label) {this.label = label;}
    
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
            
