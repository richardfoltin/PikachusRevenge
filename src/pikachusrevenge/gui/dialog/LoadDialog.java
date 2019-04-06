package pikachusrevenge.gui.dialog;

import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import pikachusrevenge.model.SaveData;

public final class LoadDialog extends GameDialog {
        
    private JTable table;
    private int selectedId;
    
    public LoadDialog(MainWindow frame){
        super(frame, "Select Saved Game...");   
    }

    @Override
    protected void fillMainPanel() {
        try {
            table = new JTable(new LoadTableModel());
            
            table.setFillsViewportHeight(true);
            table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
            sorter.setSortKeys(sortKeys);
            table.setRowSorter(sorter);
            
            table.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                        loadAction().actionPerformed(null);
                }
            });
            
            table.setRowHeight(ROW_HEIGHT);
            table.getColumnModel().getColumn(0).setPreferredWidth(140);
            table.getColumnModel().getColumn(1).setPreferredWidth(140);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            
            JScrollPane scroll = new JScrollPane(table);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            mainPanel.add(scroll);
        } catch (Database.NoResultException ex) {
            errorMessage = "There is no data in the database!";
        } catch (SQLException ex) {
            errorMessage = "No Database Connection!";
        }
        
    }

    @Override
    protected void fillBottomPanel() {
        JButton okButton = new JButton("Load");
        okButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        okButton.addActionListener(loadAction());    
        bottomPanel.add(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);
        cancelButton.addActionListener((ActionEvent e) -> {
            LoadDialog.this.setVisible(false); 
        });    
        bottomPanel.add(cancelButton);
    }

    @Override
    public void showDialog() {
        this.selectedId = 0;
        super.showDialog();
    }
    
    private final ActionListener loadAction() {
        return (ActionEvent e) -> {
            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
            selectedId = (Integer)table.getModel().getValueAt(modelRow,0);
            LoadDialog.this.setVisible(false); 
        };
    }

    public int getSelectedId() {return selectedId;}
    
    public class LoadTableModel extends AbstractTableModel  {

        private final ArrayList<SaveData> data;
        private final String[] colName = new String[]{"ID", "Name", "Date", "Level", "Pokémon", "Score"}; 
        
        public LoadTableModel() throws Database.NoResultException, SQLException {
            this.data = Database.loadAllSaveData();
        }
        
        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return colName.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SaveData s = data.get(rowIndex);
            
            switch (columnIndex) {
                case 0 : return s.id;
                case 1 : return s.name; 
                case 2 : return new SimpleDateFormat("yyyy.MM.dd hh:mm").format(s.updated);
                case 3 : return s.maxLevel; 
                case 4 : return String.format("%d%%",(int)(((double)s.foundPokemon/(double)s.maxPokemon)*100));
                case 5 : return s.score; 
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
