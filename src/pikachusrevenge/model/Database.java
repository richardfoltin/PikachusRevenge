package pikachusrevenge.model;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.gui.MenuBar;
import pikachusrevenge.model.Model.Difficulty;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.Pokemon;

/**
 * Az adatbáziskezeléshez kapcsolatos statikus metódusokat tartalmazó osztály.
 * @author Csaba Foltin
 */
public class Database {
    private static MysqlConnectionPoolDataSource connPool;
    private static boolean tableCreated = false;
    public static final String DB = "pikachusrevenge";
    public static final String USER = "tanulo";
    public static final String PASSWORD = "asd123";
    
    private Database(){}
    
    public static class NoResultException extends Exception{};
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        MainWindow window = MainWindow.getInstance();
        if (connPool == null){
            Class.forName("com.mysql.jdbc.Driver"); // Driver betöltése
            connPool = new MysqlConnectionPoolDataSource();
            connPool.setServerName("localhost");
            connPool.setPort(3306);
            connPool.setUser(USER);
            connPool.setPassword(PASSWORD);
        }
        Connection conn = connPool.getPooledConnection().getConnection();
        if (!tableCreated) {
            try {
                runBatchFile("createDatabase.sql", conn);
                tableCreated = true;
            } catch (Exception e){
                window.showDbError("Cannot create database");
                System.err.println("Cannot create database\n" + e);
            }
            putSaveIntoDb("save/csaba_hc.pikasave", "Csaba", 1);
        }
        return conn;
    }
    
    /**
     * Lefuttat egy ;-vel elválasztot SQL utasításokat tartalmazó .sql file-t
     * @param fileName a file neve és elérési útja
     * @param conn a kapcsolat
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException SQLException
     */
    public static void runBatchFile(String fileName, Connection conn) throws ClassNotFoundException, SQLException {
        Statement stmt = conn.createStatement();
        StringBuilder contentBuilder = new StringBuilder();
        URL url = Database.class.getResource(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(url.getPath()))){
            String line;
            while ((line = br.readLine()) != null){
                contentBuilder.append(line);
                if (contentBuilder.length() > 0 && contentBuilder.charAt(contentBuilder.length()-1) == ';') {
                    stmt.addBatch(contentBuilder.toString());
                    contentBuilder = new StringBuilder();
                } else {
                    contentBuilder.append("\n");
                }
            }
        }catch (IOException e){
            System.err.println("Cannot read .sql file to create database.");
            throw new SQLException();
        }
        stmt.executeBatch();
    }
    
    /**
     * Betölti a dialógusablakok megjelenítéséhez szükséges adatbázisban tárolt
     * adatokat
     * @return az {@link SaveData} adatokat tartalmazó lista
     * @throws pikachusrevenge.model.Database.NoResultException nincs adat az adatbázisban
     * @throws SQLException SQLException
     */
    public static ArrayList<SaveData> loadAllSaveData() throws NoResultException, SQLException {
        ArrayList<SaveData> data = new ArrayList<>();
        
        try (Statement stmt = getConnection().createStatement()){
            String query =  "SELECT \n" +
                            "	 p.id as 'id',\n" +
                            "    p.name as 'name',\n" +
                            "    p.life as 'life',\n" +
                            "    p.actualLevel as 'actualLevel',\n" +
                            "    p.maxLevel as 'maxLevel',\n" +
                            "    p.score as 'score',\n" +
                            "    p.updated as 'updated',\n" +
                            "    p.difficulty as 'difficulty',\n" +
                            "    count(case pok.found when 1 then 1 else NULL end) as 'foundPokemon',\n" +
                            "    count(pok.id) as 'maxPokemon'\n" +
                            "FROM pikachusrevenge.player p\n" +
                            "LEFT JOIN pikachusrevenge.pokemon pok ON p.id = pok.player_id\n" +
                            "GROUP BY\n" +
                            "	 p.id, p.name, p.life, p.actualLevel, p.maxLevel, p.score";
            ResultSet rs = stmt.executeQuery(query);
            if (!rs.isBeforeFirst()) throw new NoResultException();
            while (rs.next()){
                SaveData s = new SaveData();
                s.id = rs.getInt("id");
                s.name = rs.getString("name");
                s.life = rs.getInt("life");
                s.actualLevel = rs.getInt("actualLevel");
                s.maxLevel = rs.getInt("maxLevel");
                s.score = rs.getInt("score");
                s.foundPokemon = rs.getInt("foundPokemon");
                s.maxPokemon = rs.getInt("maxPokemon");
                s.updated = rs.getTimestamp("updated");
                s.difficulty = Difficulty.fromId(rs.getInt("difficulty"));
                data.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Cannot load saved games data!\n" + e);
            throw new SQLException(e);
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot load saved games data!\n" + e);
            throw new SQLException(e);
        }
        
        return data;
    }
    
    /**
     * Betölti a kiválasztott id-jű mentést az adatbázisból
     * @param id a kiválasztott adat id-je
     * @param difficulty a kiválasztott nehézségi fok
     * @return true, ha sikeres a betöltés
     */
    public static boolean load(int id, Difficulty difficulty) {
        if (id == 0) return false;
        Model model = new Model(id, difficulty);
        
        try (Connection conn = getConnection()) {
        
            //player
            Player player = model.getPlayer();
            Statement stmt = conn.createStatement();
            String query = String.format("SELECT life,name,actualLevel,maxLevel,x,y FROM %s.player WHERE id = %d",DB,id);
            ResultSet rs = stmt.executeQuery(query);
            int actualLevel = 0;
            while (rs.next()){
                player.setLives(rs.getInt("life"));
                player.setName(rs.getString("name"));
                actualLevel = rs.getInt("actualLevel");
                player.increaseAvailableLevels(rs.getInt("maxLevel"));
                player.putToPosition(new Position(rs.getInt("x"),rs.getInt("y")));
            }
            
            //pokémon
            HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemonsWithPosition();
            stmt = conn.createStatement();
            query = String.format("SELECT id,level_id,x,y,found FROM %s.pokemon WHERE player_id = %d",DB,id);
            rs = stmt.executeQuery(query);
            while (rs.next()){
                TilePosition tpos = new TilePosition(rs.getInt("x"), rs.getInt("y"), rs.getInt("level_id"));
                boolean found = (rs.getInt("found") == 1);
                int pokemonId = rs.getInt("id");
                Pokemon p = new Pokemon(model,tpos,pokemonId,found);
                pokemons.put(tpos,p);
            }
            
            //level
            stmt = conn.createStatement();
            query = String.format("SELECT id,time FROM %s.level WHERE player_id = %d",DB,id);
            rs = stmt.executeQuery(query);
            while (rs.next()){
                int levelId = rs.getInt("id");
                int time = rs.getInt("time");
                model.buildLevelIfNotExists(levelId,time);
            }
            
            model.setActualLevel(actualLevel);
            MainWindow.getInstance().loadActualLevelWithNewModel(model, false);
            return true;
        } catch (Exception e) {
            MainWindow.getInstance().showDbError("Cannot load saved game!");
            System.err.println("Cannot load saved game!\n" + e);
            return false;
        }
    }
    
    /**
     * Elmenti a megadott {@link Model} által leírt játék minden eddig megjelenített
     * pályáját az adatbázisba. Amennyiben nincs megadva mentési id, akkor bekéri 
     * a játékos neevét, és új mentést hoz létre, egyébként felülírja az előzőt.
     * Ha nem tud menteni hibaüzenetet dob fel.
     * @param id a mentés id-je
     * @param model a játék modelje
     * @return true, ha sikeres a mentés
     */ 
    public static boolean save(int id, Model model) {
        try {
            String name = null;
            if (id == 0) {
                name = MainWindow.getInstance().getSaveName();
                if (name == null || name.equals("")) return false;
                model.getPlayer().setName(name);
            }
            saveSilently(id, name, model);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            MainWindow.getInstance().showDbError("Cannot save game to database!");
            System.err.println("Cannot save to database!");
            return false; 
        } 
    }
    
    /**
     * Elmenti a megadott {@link Model} által leírt játék minden eddig megjelenített
     * pályáját az adatbázisba. Amennyiben nincs megadva mentési id, akkor bekéri 
     * a játékos neevét, és új mentést hoz létre, egyébként felülírja az előzőt.
     * @param id a mentés id-je
     * @param name a játékos neve. Ha null, akkor nem lesz átírva
     * @param model a játék modelje
     * @throws java.sql.SQLException SQL hiba
     * @throws java.lang.ClassNotFoundException SQL hiba
     */
    public static void saveSilently(int id, String name, Model model) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        String stmt = null;
        PreparedStatement pstmt = null;
        
        //player
        Player player = model.getPlayer();
        if (id == 0) {
            stmt = String.format("INSERT INTO %s.player (life,x,y,actualLevel,maxLevel,score,difficulty,updated,name)\n" +
                    "VALUES (?,?,?,?,?,?,?,now(),?)",DB);
            if (name == null) throw new SQLException();
            pstmt = conn.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(8, name);
        } else {
            stmt = String.format("UPDATE %s.player SET life=?, x=?, y=?, actualLevel=?, maxLevel=?, score=?, difficulty=?, updated=now()\n" +
                    "WHERE id = %d",DB,id);
            pstmt = conn.prepareStatement(stmt);
        }
        
        pstmt.setInt(1, player.getLives());
        pstmt.setInt(2, (int)player.getPosition().x);
        pstmt.setInt(3, (int)player.getPosition().y);
        pstmt.setInt(4, model.getActualLevelId());
        pstmt.setInt(5, model.getLevels().size());
        pstmt.setInt(6, model.getScore());
        pstmt.setInt(7, model.getDifficulty().id);
        pstmt.addBatch();
        pstmt.executeBatch();
        
        if (id == 0) {
            conn.commit();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) id = generatedKeys.getInt(1);
            else throw new SQLException();
        }
        
        //pokémon
        stmt = String.format("REPLACE INTO %s.pokemon (player_id,id,level_id,name,found,x,y,updated)\n" +
                "VALUES (?,?,?,?,?,?,?,now())",DB);
        pstmt = conn.prepareStatement(stmt);
        for (HashMap.Entry<TilePosition,Pokemon> p : model.getAllPokemonsWithPosition().entrySet()) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, p.getValue().getId());
            pstmt.setInt(3, p.getKey().getLevel());
            pstmt.setString(4, p.getValue().getName());
            pstmt.setInt(5, (p.getValue().isFound()) ? 1 : 0);
            pstmt.setInt(6, p.getKey().getX());
            pstmt.setInt(7, p.getKey().getY());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        
        //level
        stmt = String.format("REPLACE INTO %s.level (player_id,id,time,updated)\n" +
                "VALUES (?,?,?,now())",DB);
        pstmt = conn.prepareStatement(stmt);
        for (Level level : model.getLevels()){
            pstmt.setInt(1, id);
            pstmt.setInt(2, level.getId());
            pstmt.setInt(3, level.getTime());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        conn.commit();
        System.out.println("Save successful!");
        
        model.setDbId(id);
    }
    
    /**
     * Egy file-ban elmentett játékot az adatbázisba rak, ha a megkapott id-n
     * még nem szerepel semmi.
     * @param fileName az elmentett játék fileneve
     * @param name a játékos neve
     */
    private static void putSaveIntoDb(String fileName, String name, int id) {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT id FROM %s.player WHERE id = %d", DB,id));
            if (!rs.isBeforeFirst()) throw new NoResultException();
        } catch (NoResultException e) {
            try {
                File file = new File(System.getProperty("user.dir") + "/" + fileName);
                Model model = MenuBar.load(file);
                saveSilently(0, name, model);
            } catch (FileNotFoundException | MenuBar.IllegalFileException ex) {
                System.err.println("Cannot put save game into db! Wrong save game file!");
            } catch (ClassNotFoundException | SQLException ex) {
                System.err.println("Cannot put save game into db!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Cannot put save game into db!");
        }
    }
}
