
package pikachusrevenge.model;

import java.util.Date;
import java.util.Objects;
import pikachusrevenge.model.Model.Difficulty;

public final class SaveData {
    public int id;
    public String name;
    public int life;
    public int actualLevel;
    public int maxLevel;
    public int foundPokemon;
    public int maxPokemon;
    public int score;
    public Difficulty difficulty;
    public Date updated;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.id;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + this.life;
        hash = 43 * hash + this.actualLevel;
        hash = 43 * hash + this.maxLevel;
        hash = 43 * hash + this.foundPokemon;
        hash = 43 * hash + this.maxPokemon;
        hash = 43 * hash + this.score;
        hash = 43 * hash + Objects.hashCode(this.difficulty);
        hash = 43 * hash + Objects.hashCode(this.updated);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final SaveData other = (SaveData) obj;
        if (this.id != other.id) return false;
        if (this.life != other.life) return false;
        if (this.actualLevel != other.actualLevel) return false;
        if (this.maxLevel != other.maxLevel) return false;
        if (this.foundPokemon != other.foundPokemon) return false;
        if (this.maxPokemon != other.maxPokemon) return false;
        if (this.score != other.score) return false;
        if (!Objects.equals(this.difficulty, other.difficulty)) return false;
        if (!Objects.equals(this.name, other.name)) return false;
        if (!Objects.equals(this.updated, other.updated)) return false;
        return true;
    }
    
}
