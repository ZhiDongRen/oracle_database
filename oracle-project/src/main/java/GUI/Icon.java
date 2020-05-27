
package GUI;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * 自定义图像显示类
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
     * 构造函数
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
     * 设置额外的类变量ic的构造方法
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
     * 重置当前图标
     * @param i icon
     */
    public void setNewIcon(ImageIcon i){
        ic = i;
    }
    
    
    /**
     * 处理图标
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
     * @param f 设定
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
     * 返回索引图标
     * @return index icon
     */
    public int getIndex() {
        return this.index;
    }
    
    /**
     * 设置将图像加载到数据库的路径
     * @param p 篮子
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
     * 返回当前图标
     * @return 当前图标
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
     * 图标处于活动状态时继续
     * @param a 激活
     */
    public void setActive(boolean a) {
        this.active = a;
    }
}