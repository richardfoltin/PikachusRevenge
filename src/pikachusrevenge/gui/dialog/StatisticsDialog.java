package pikachusrevenge.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.gui.StatsPanel;
import static pikachusrevenge.gui.StatsPanel.TIMEFORMAT;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.unit.Pokemon;

public final class StatisticsDialog extends GameDialog {
        
    private JTable table;
    
    public StatisticsDialog(MainWindow frame, String title){
        super(frame, title);   
    }

    @Override
    protected void fillMainPanel() {
        HashMap<TilePosition,Pokemon> pokemons = MainWindow.getInstance().getModel().getAllPokemonsWithPosition();
        ArrayList<Level> levels = MainWindow.getInstance().getModel().getLevels();
        table = new JTable(new StatisticsTableModel(pokemons, levels));
        table.getColumnModel().getColumn(1).setCellRenderer(pokemonRenderer());
        table.setFillsViewportHeight(false);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);
        
        table.setRowHeight(ROW_HEIGHT);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setEnabled(false);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scroll);
    }

    @Override
    protected void fillBottomPanel() {
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        okButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) { 
                StatisticsDialog.this.setVisible(false);
            } 
        });    
        bottomPanel.add(okButton);
    }
    
    private TableCellRenderer pokemonRenderer() {
        return new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                panel.setBackground(Color.white);
                panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                ArrayList<?> pokemons = (ArrayList<?>)value;
                for(Object o : pokemons){
                    Pokemon p = (Pokemon)o;
                    JLabel label = StatsPanel.getBallLabel();
                    if (p.isFound()) p.revealLabel(label);
                    label.setPreferredSize(new Dimension(ROW_HEIGHT,ROW_HEIGHT));
                    panel.add(label);
                }
                return panel;
            }
        };
    }

    public class StatisticsTableModel extends AbstractTableModel {

        private final HashMap<TilePosition,Pokemon> pokemons;
        private final ArrayList<Level> levels;
        private final String[] colName = new String[]{"Level", "Pokémon", "Time"}; 
        
        public StatisticsTableModel(HashMap<TilePosition,Pokemon> pokemons, ArrayList<Level> levels) {
            this.pokemons = pokemons;
            this.levels = levels;
        }
        
        @Override
        public int getRowCount() {
            return levels.size();
        }

        @Override
        public int getColumnCount() {
            return colName.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Level level = levels.get(rowIndex);
            int levelId = level.getId();
            switch (columnIndex) {
                case 0 : return levelId;
                case 1 : return pokemons.entrySet().stream().filter(e -> e.getKey().getLevel() == levelId).map(e -> e.getValue()).collect(Collectors.toList());
                case 2 : return TIMEFORMAT.format(new Date(level.getTime() * 1000));
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
