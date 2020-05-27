
package GUI;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * �Զ���ͼ����ʾ��
 * @author Doria
 */
public class Icon extends JLabel{

    private boolean active = false;
    private int index;
    private String path = null;
    private boolean focusable = true;
    private ImageIcon ic;
    private Double score = 0.0;
    
    /**
     * ���캯��
     */
    public Icon(){
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IconMouseClicked(evt);
            }
        });
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setText("");
        //setSize(60, 60);
    }
    
    /**
     * ���ö���������ic�Ĺ��췽��
     * @param i ikona
     */
    public Icon(ImageIcon i){
       addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IconMouseClicked(evt);
            }
        });
        setText("");
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        //setIcon(i);
        ic = i;
    }
    
    /**
     * ���õ�ǰͼ��
     * @param i icon
     */
    public void setNewIcon(ImageIcon i){
        ic = i;
    }
    
    
    /**
     * ����ͼ��
     * @param evt event
     */
    public void IconMouseClicked(java.awt.event.MouseEvent evt){
        System.out.println("clik my Icon");
        if(focusable) {
            if(!active){
                setBorder(BorderFactory.createLineBorder(Color.green,5));
                active = true;
            } else {
                active = false;
                setBorder(null);
            }
        }
    }
    
    /**
     * 
     * @return focusable
     */
    public boolean canFocus() {
        return focusable;
    }
    
    /**
     * @param f �趨
     */
    public void setFocus(boolean f) {
        focusable = f;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isActive(){
        return active;
    }
    
    /**
     * 
     * @param i index
     */
    public void setIndex(int i) {
        this.index = i;
    }
    
    /**
     * ��������ͼ��
     * @return index icon
     */
    public int getIndex() {
        return this.index;
    }
    
    /**
     * ���ý�ͼ����ص����ݿ��·��
     * @param p ����
     */
    public void setPath(String p){
        this.path = p;
    }
    
    /**
     * Vraci cestu pro nacteni do db
     * @return cesta
     */
    public String getPath(){
        return this.path;
    }
    
    /**
     * ���ص�ǰͼ��
     * @return ��ǰͼ��
     */
    
    public ImageIcon getIcon(){
        return this.ic;
    }
    
    /**
     * Nastavi skore pro porovnani obrazku
     * @param s skore
     */
    public void setScore(Double s){
        this.score = s;
    }
    
    /**
     * Vrati skore
     * @return skore
     */
    public Double getScore(){
        return this.score;
    }
    
    /**
     * Vrati skore jako string
     * @return skore
     */
    public String getScoreAsString(){
        return String.valueOf(this.score);
    }
    
    /**
     * ͼ�괦�ڻ״̬ʱ����
     * @param a ����
     */
    public void setActive(boolean a) {
        this.active = a;
    }
}