package pikachusrevenge.model;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.Pokemon;

public class Database {
    private static MysqlConnectionPoolDataSource connPool;
    private static boolean tableCreated = false;
    public static final String DB = "pikachusrevenge";
    public static final String USER = "tanulo";
    public static final String PASSWORD = "asd123";
    
    private Database(){}
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        MainWindow window = MainWindow.getInstance();
        if (connPool == null){
            Class.forName("com.mysql.jdbc.Driver"); // Driver betöltése
            connPool = new MysqlConnectionPoolDataSource();
            connPool.setServerName("localhost");
            connPool.setPort(3306);
            connPool.setDatabaseName(DB);
            connPool.setUser(USER);
            connPool.setPassword(PASSWORD);
        }
        
        Connection conn = connPool.getPooledConnection().getConnection();
        
        if (!tableCreated) {
            try (Statement stmt = conn.createStatement()){
                StringBuilder contentBuilder = new StringBuilder();
                URL url = Database.class.getResource("createDatabase.sql");
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
                    throw new Exception();
                }
                stmt.executeBatch();
                tableCreated = true;
            } catch (Exception e){
                window.showDbError("Cannot create database");
                System.err.println("Cannot create database\n" + e);
            }
        }
        return conn;
    }
    
    public static boolean loadSelection() {
        int id = 0;
        
        return load(id);
    }
    
    public static boolean load(int id) {
        Model model = new Model(id);
        String query;
        int actualLevel = 0;
        Position start = null;
        
        //player
        Player player = model.getPlayer();
        try (Statement stmt = getConnection().createStatement()){
            query = String.format("SELECT life,actualLevel,maxLevel,x,y FROM %s.player WHERE id = %d",DB,id);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                player.setLives(rs.getInt("life"));
                actualLevel = rs.getInt("actualLevel");
                player.increaseAvailableLevels(rs.getInt("maxLevel"));
                start = new Position(rs.getInt("x"),rs.getInt("y"));
            }
        } catch (Exception e) {
            MainWindow.getInstance().showDbError("Cannot load saved game!");
            System.err.println("Cannot load saved game!\n" + e);
            return false;
        }
        
        //pokémon
        HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemons();
        try (Statement stmt = getConnection().createStatement()){
            query = String.format("SELECT pokemon_id,level_id,x,y,found FROM %s.pokemon WHERE player_id = %d",DB,id);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                TilePosition tpos = new TilePosition(rs.getInt("x"), rs.getInt("y"), rs.getInt("level_id"));
                boolean found = (rs.getInt("found") == 1);
                int pokemonId = rs.getInt("pokemon_id");
                Pokemon p = new Pokemon(model,tpos,pokemonId,found);
                pokemons.put(tpos,p);
            }
        } catch (Exception e) {
            MainWindow.getInstance().showDbError("Cannot load saved game!");
            System.err.println("Cannot load saved game!\n" + e);
            return false;
        }
        
        //level
        try (Statement stmt = getConnection().createStatement()){
            query = String.format("SELECT level_id,time FROM %s.level WHERE player_id = %d",DB,id);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                int levelId = rs.getInt("level_id");
                int time = rs.getInt("time");
                model.buildLevelIfNotExists(levelId,time);
            }
        } catch (Exception e) {
            MainWindow.getInstance().showDbError("Cannot load saved game!");
            System.err.println("Cannot load saved game!\n" + e);
            return false;
        }
        
        MainWindow.getInstance().loadLevelWithNewModel(model, actualLevel, start);
        return true;
    }
      
    public static boolean save(int id, Model model) {
        String query;
        String name = null;
        if (id == 0) {
            name = MainWindow.getInstance().getSaveName();
            if (name == null || name.equals("")) return false;
            try (Statement stmt = getConnection().createStatement()){
                int maxId = 0;
                query = String.format("SELECT max(id) as 'id' FROM %s.player",DB);
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    maxId = rs.getInt("id");
                }
                id = maxId + 1;
            } catch (Exception e) {
                MainWindow.getInstance().showDbError("Database error!");
                System.err.println("Cannot make new ID!\n" + e);
                return false;
            }
        } else {
            try (Statement stmt = getConnection().createStatement()){
                query = String.format("SELECT name FROM %s.player WHERE id = %d",DB,id);
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    name = rs.getString("name");
                }
                if (name == null || name.equals("")) return false;
            } catch (Exception e) {
                MainWindow.getInstance().showDbError("Database error!");
                System.err.println("Cannot get player name!\n" + e);
                return false;
            }
        }
        
        Player player = model.getPlayer();
        HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemons();
        ArrayList<Level> levels = model.getLevels();
        int actualLevel = model.getActualLevelId();
        int score = model.getScore();    
        StringBuilder queryBuilder;
        String comma;
        
        //player
        query = String.format("REPLACE INTO %s.player (id,name,life,x,y,actualLevel,maxLevel,score)\n" + 
                                     "VALUES (%d,'%s',%d,%d,%d,%d,%d,%d)", 
                                     DB,
                                     id,
                                     name,
                                     player.getLives(),
                                     (int)player.getPosition().x,
                                     (int)player.getPosition().y,
                                     actualLevel,
                                     levels.size(),
                                     score);
        if (!sqlQuery(query,"Cannot save to player table!")) return false;   
        
        //pokémon
        queryBuilder = new StringBuilder();
        comma = "";
        queryBuilder.append(String.format("REPLACE INTO %s.pokemon (player_id,pokemon_id,level_id,name,found,x,y)\nVALUES\n",DB));
        for (HashMap.Entry<TilePosition,Pokemon> p : pokemons.entrySet()) {
            queryBuilder.append(String.format("%s(%d,%d,%d,'%s',%d,%d,%d)\n",
                                              comma,
                                              id,
                                              p.getValue().getId(),
                                              p.getKey().getLevel(),
                                              p.getValue().getName(),
                                              (p.getValue().isFound()) ? 1 : 0,
                                              p.getKey().getX(),
                                              p.getKey().getY()));
            comma = ",";
        }
        if (!sqlQuery(queryBuilder.toString(),"Cannot save to pokemon table!")) return false;   

        //levels
        queryBuilder = new StringBuilder();
        comma = "";
        queryBuilder.append(String.format("REPLACE INTO %s.level (player_id,level_id,time)\nVALUES\n",DB));
        for (Level level : levels){
            queryBuilder.append(String.format("%s(%d,%d,%d)\n",
                                              comma,
                                              id,
                                              level.getId(),
                                              level.getTime()));
            comma = ",";
        }
        if (!sqlQuery(queryBuilder.toString(),"Cannot save to level table!")) return false;
        
        model.setDbId(id);
        return true;
    }
    
    public static boolean sqlQuery(String query, String error) {
        MainWindow window = MainWindow.getInstance();
        try (Statement stmt = getConnection().createStatement()){
            int i = stmt.executeUpdate(query);
            System.out.println(query);
            return true;
        } catch (Exception e){
            window.showDbError(error);
            System.err.println(error + "\n" + e + "\n" + query);
            return false;
        }    
    }

}
