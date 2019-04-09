package pikachusrevenge.gui.dialog;

import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.model.Database;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.SaveData;

/**
 * A dicsőségtábált tartalmazó dialógusablak.
 * @author Csaba Foltin
 */
public final class HighscoreDialog extends GameDialog {
        
    private JTable table;
    
    public HighscoreDialog(MainWindow frame){
        super(frame, "Highscores");   
    }

    @Override
    protected void fillMainPanel() {
        try {
            table = new JTable(new HighscoreTableModel());
            
            table.setFillsViewportHeight(true);
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
            sorter.setSortKeys(sortKeys);
            table.setRowSorter(sorter);

            table.setRowHeight(ROW_HEIGHT);
            table.getColumnModel().getColumn(0).setPreferredWidth(130);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            table.setRowSelectionAllowed(false);
            table.getTableHeader().setEnabled(false);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            mainPanel.add(scroll);
        } catch (Database.NoResultException ex) {
            errorMessage = "There is no data in the database!";
        } catch (SQLException ex) {
            errorMessage = "No Database Connection!";
        }
        
    }

    @Override
    protected void fillBottomPanel() {
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        okButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) { 
                HighscoreDialog.this.setVisible(false);
            } 
        });    
        bottomPanel.add(okButton);
    }

    /**
     * A dicsőségtábla absztrakt modelje.
     */
    public class HighscoreTableModel extends AbstractTableModel {

        private final ArrayList<SaveData> data;
        private final String[] colName = new String[]{"Name", "Max Level", "Pokémon", "Score"}; 
        
        public HighscoreTableModel() throws Database.NoResultException, SQLException {
            this.data = Database.loadAllSaveData();
        }
        
        @Override
        public int getRowCount() {
            return Math.min(data.size(),10);
        }

        @Override
        public int getColumnCount() {
            return colName.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SaveData s = data.get(rowIndex);
            
            switch (columnIndex) {
                case 0 : return (s.difficulty == Model.Difficulty.HARDCORE) ? s.name + " @90s" : s.name; 
                case 1 : return s.maxLevel; 
                case 2 : return String.format("%d%%",(int)(((double)s.foundPokemon/(double)s.maxPokemon)*100));
                case 3 : return s.score; 
                default: return null;
            }
        }
        
        @Override
        public String getColumnName(int i) { 
            return colName[i]; 
        }
        
        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
}
