/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import GUI.MainWindow;

   
/**
 *
 * @author doria
 */

public class Application{

    /**
     *
     */
    public Application() {
    }
    
    /**
     * 主应用程序循环。
     * @param args 命令行参数
     */

    
     public static void main(String[] args) {
     
        MainWindow mainWin= new MainWindow();
       mainWin.setLocationRelativeTo(null);
        mainWin.setVisible(true);
        
    }
}
