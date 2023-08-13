package swing;

import javax.swing.*;

/**
 * @author wuyongkang
 * @date 2023年03月06日 14:49
 */
public class SwingTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog jDialog = new JDialog();
                jDialog.setVisible(true);
                jDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (jDialog.isVisible()){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });cdvdv
                thread.start();
            }
        });

    }
}