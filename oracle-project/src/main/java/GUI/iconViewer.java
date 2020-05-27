/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
//import Model.SpatialModel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import Model.ImageModel;
import Model.titleModel;
/**
 *
 * @author doria
 */
public class iconViewer extends javax.swing.JPanel {

    /**
     * Creates new form iconViwer
     */
    public iconViewer() {
        initComponents();
        myInit();
        
    }
  private void myInit() {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        title_comtainer.setLayout(layout);
        ModelImage = new ImageModel();
        picture = new Icon();
        updateCombo();
        title_combobox.addActionListener(new ActionListener() {
            @Override
     public void actionPerformed(ActionEvent ae) {
                comboBoxAction(ae);
            }
        });
 }
    private Map<Integer, Icon> updateUserImages(int usrId){
        Map<Integer, Icon> result = null;
        try {
            result =  ModelImage.getImagesOftitle(usrId);
        } catch (SQLException ex) {
            Logger.getLogger(iconViewer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }
         public void updateCombo(){
        layout_databaseIDtoComboxId = new HashMap<>();
        titleModel ModelTitle = new titleModel();

        try {
            int i = 0;
            Map<Integer, String> list = ModelTitle.getList();

            String[] items = new String[list.size() + 1];
            items[i++] = "";
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                items[i] = entry.getValue();
                layout_databaseIDtoComboxId.put(entry.getKey(), i);
                i++;
            }
            comboBoxItems = items;
        } catch (SQLException e) {
            comboBoxItems = new String[]{"loading error.."};
        }
        title_combobox.setModel(new DefaultComboBoxModel(comboBoxItems));
        title_combobox.setSelectedIndex(0);
        picture.setVisible(false);
        picture.setIcon(null);
    }
         private void getNextIcon() {
        if(picUpdated != null) {
            if(list!=null && !list.isEmpty() && currentIndex < list.size()-1) {
                currentIndex += 1;
                Entry<Integer, Icon> novyItem = list.get(currentIndex);
                i = novyItem.getValue().getIcon();
                Delete.setEnabled(true);
                Roation.setEnabled(true);
                picture.setIndex(novyItem.getKey());
                setNewIcon(i, false);
                ActIndex_label.setText("Index: "+(currentIndex+1)+"/"+list.size());
            }
        }
    }
         private void getPreviousIcon() {
        if(picUpdated != null) {
            if(list!=null && !list.isEmpty() && currentIndex > 0) {
                currentIndex -= 1;
                Entry<Integer,Icon> novyItem = list.get(currentIndex);
                i = novyItem.getValue().getIcon();
                Delete.setEnabled(true);
                Roation.setEnabled(true);
                picture.setIndex(novyItem.getKey());
                setNewIcon(i, false);
                ActIndex_label.setText("Index: "+(currentIndex+1)+"/"+list.size());
            } 
        }
    }  
   /**
     * 
     * @param i
     * @param notFound
     */
         public void setNewIcon(ImageIcon i, boolean notFound){
        if(i != null){
            if(notFound) {
                picture.setText("No IMAGE");
                picture.setFocus(false);
                ActIndex_label.setText("Index: 0/0");
            } else {
                picture.setText("");
                picture.setFocus(true);
            }
            picture.setVisible(true);
            picture.setIcon(i);
            //picture.setIndex(lastUserId);
            title_comtainer.add(picture);
            title_comtainer.revalidate();
        }
    }  
  /**
     * 
     * @param ae 
     */
     public void comboBoxAction(ActionEvent ae){
        JComboBox cb = (JComboBox) ae.getSource();
        String comboBoxitem = (String) cb.getSelectedItem();
        list = null;
        statubar.setVisible(false);
        if(!comboBoxitem.equals("")){
            String substring = comboBoxitem.substring(0, comboBoxitem.indexOf(" "));
            lastUserId = Integer.parseInt(substring);
            System.out.println(lastUserId);
            boolean notFound = false;
            picUpdated = updateUserImages(lastUserId);
            if(picUpdated.isEmpty()) {
                System.out.println("NULL");
                String path = "/Image/ban.png";
                i = new ImageIcon(getClass().getResource(path));
                Delete.setEnabled(false);
                Roation.setEnabled(false);
                notFound = true;
            } else {
                
                list = new ArrayList<>(picUpdated.entrySet());
                currentIndex = 0;
                Entry<Integer,Icon> entry = list.get(currentIndex);
                i = entry.getValue().getIcon();
                picture.setIndex(entry.getKey());
                Delete.setEnabled(true);
                Roation.setEnabled(true);
                ActIndex_label.setText("Index: "+(currentIndex+1)+"/"+(list.size()));
            }
            setNewIcon(i,notFound);
        } else {
            picture.setVisible(false);
            picture.setIcon(null);
            picture.setText("");
            ActIndex_label.setText("Index: 0/0");
        }
    }
  
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Roation = new javax.swing.JButton();
        Delete = new javax.swing.JButton();
        title_combobox = new javax.swing.JComboBox<>();
        refreshLayoutList_button = new javax.swing.JButton();
        similar_button1 = new javax.swing.JButton();
        forward = new javax.swing.JButton();
        backward = new javax.swing.JButton();
        ActIndex_label = new javax.swing.JLabel();
        Pic_Container_parrent = new javax.swing.JPanel();
        statubar = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        title_comtainer = new javax.swing.JPanel();

        Roation.setText("Roation");
        Roation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoationActionPerformed(evt);
            }
        });

        Delete.setText("Delete");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        title_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        title_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                title_comboboxActionPerformed(evt);
            }
        });

        refreshLayoutList_button.setText("refreshLayoutList");
        refreshLayoutList_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshLayoutList_buttonActionPerformed(evt);
            }
        });

        similar_button1.setText("similar/œ‡À∆");

        forward.setText("<");
        forward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardActionPerformed(evt);
            }
        });

        backward.setText(">");
        backward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardActionPerformed(evt);
            }
        });

        ActIndex_label.setText("Index:0/0");

        jLabel3.setText("You must first select an image (by left-clicking on the image)");

        javax.swing.GroupLayout statubarLayout = new javax.swing.GroupLayout(statubar);
        statubar.setLayout(statubarLayout);
        statubarLayout.setHorizontalGroup(
            statubarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statubarLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                .addContainerGap())
        );
        statubarLayout.setVerticalGroup(
            statubarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statubarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );

        javax.swing.GroupLayout title_comtainerLayout = new javax.swing.GroupLayout(title_comtainer);
        title_comtainer.setLayout(title_comtainerLayout);
        title_comtainerLayout.setHorizontalGroup(
            title_comtainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1359, Short.MAX_VALUE)
        );
        title_comtainerLayout.setVerticalGroup(
            title_comtainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(title_comtainer);

        javax.swing.GroupLayout Pic_Container_parrentLayout = new javax.swing.GroupLayout(Pic_Container_parrent);
        Pic_Container_parrent.setLayout(Pic_Container_parrentLayout);
        Pic_Container_parrentLayout.setHorizontalGroup(
            Pic_Container_parrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Pic_Container_parrentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statubar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Pic_Container_parrentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Pic_Container_parrentLayout.setVerticalGroup(
            Pic_Container_parrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Pic_Container_parrentLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statubar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Roation)
                        .addGap(14, 14, 14)
                        .addComponent(Delete)
                        .addGap(18, 18, 18)
                        .addComponent(title_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refreshLayoutList_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(similar_button1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(forward)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backward)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ActIndex_label)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(Pic_Container_parrent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Roation)
                    .addComponent(Delete)
                    .addComponent(title_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshLayoutList_button)
                    .addComponent(similar_button1)
                    .addComponent(forward)
                    .addComponent(backward)
                    .addComponent(ActIndex_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Pic_Container_parrent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RoationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoationActionPerformed
        if(picture.isActive()){
            try {
                statubar.setVisible(false);
                ModelImage.rotateImage(picture.getIndex());
                ImageIcon ic = new ImageIcon(ModelImage.getImage(picture.getIndex()));
                picture.setNewIcon(ic);
                setNewIcon(ic, false);
                Entry<Integer, Icon> en = new AbstractMap.SimpleEntry<Integer, Icon>(picture.getIndex(), picture);
                picUpdated = updateUserImages(lastUserId);
                list = new ArrayList<>(picUpdated.entrySet());
                list.set(currentIndex, en);
            } catch (SQLException ex) {
                Logger.getLogger(iconViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            statubar.setVisible(true);
            statubar.setBackground(Color.red);
        }
    }//GEN-LAST:event_RoationActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        if(picture.isActive()){
            statubar.setVisible(false);
            picture.setVisible(false);
            picture.setIcon(null);
            picture.setBorder(null);
            try {
                ModelImage.delete(picture.getIndex());
                System.out.println("I want to delete with the index: "+picture.getIndex());
                Icon remove = picUpdated.remove(picture.getIndex());
                list = new ArrayList<>(picUpdated.entrySet());
                list.remove(currentIndex);
            } catch (SQLException ex) {
                Logger.getLogger(iconViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(list != null && !list.isEmpty() && currentIndex < list.size()-1){
                getNextIcon();
            } else if(list != null && !list.isEmpty() && currentIndex > 0){
                getPreviousIcon();
            } else {
                String path = "/Image/ban.png";
                ImageIcon ic = new ImageIcon(getClass().getResource(path));
                Delete.setEnabled(false);
                Roation.setEnabled(false);
                setNewIcon(ic, true);
            }
        } else {
            statubar.setVisible(true);
            statubar.setBackground(Color.red);
        }
    }//GEN-LAST:event_DeleteActionPerformed

    private void title_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_title_comboboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_title_comboboxActionPerformed

    private void refreshLayoutList_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshLayoutList_buttonActionPerformed

        updateCombo();
        picture.setVisible(false);
        picture.setIcon(null);        // TODO add your handling code here:
    }//GEN-LAST:event_refreshLayoutList_buttonActionPerformed

    private void forwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardActionPerformed
        statubar.setVisible(false);
        getPreviousIcon(); // TODO add your handling code here:
    }//GEN-LAST:event_forwardActionPerformed

    private void backwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backwardActionPerformed
        statubar.setVisible(false);
        getNextIcon();  // TODO add your handling code here:
    }//GEN-LAST:event_backwardActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ActIndex_label;
    private javax.swing.JButton Delete;
    private javax.swing.JPanel Pic_Container_parrent;
    private javax.swing.JButton Roation;
    private javax.swing.JButton backward;
    private javax.swing.JButton forward;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshLayoutList_button;
    private javax.swing.JButton similar_button1;
    private javax.swing.JPanel statubar;
    private javax.swing.JComboBox<String> title_combobox;
    private javax.swing.JPanel title_comtainer;
    // End of variables declaration//GEN-END:variables
    private ImageModel ModelImage;
    private Icon picture;
    private Map<Integer, Integer> layout_databaseIDtoComboxId;
    private String[] comboBoxItems;
    private ImageIcon i;
    private int lastUserId;
    private Map<Integer, Icon> picUpdated;
    private Integer currentIndex;
    private List<Map.Entry<Integer, Icon>> list;
        }
