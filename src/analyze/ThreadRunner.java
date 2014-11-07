/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import javax.swing.tree.DefaultTreeModel;

class ThreadRunner extends Thread {

    private Thread t;
    String sourceFolderName;

    javax.swing.JProgressBar progressInfo;
    javax.swing.JTextArea textInfo;
    DefaultTreeModel model;

    ThreadRunner() {
        if (t == null) {
            t = new Thread(this, "analyze");

        }
    }

    void set(String sourceFolderName,  javax.swing.JProgressBar progressInfo, javax.swing.JTextArea textInfo, DefaultTreeModel model) {
        this.sourceFolderName = sourceFolderName;

        this.progressInfo = progressInfo;
        this.textInfo = textInfo;
        this.model = model;
    }

    public void run() {
        Analyzer a = new Analyzer();
        a.analyze(sourceFolderName, progressInfo, textInfo, model);
    }

    public void restart() {
        if (t.isAlive()) {

            t.stop();
        }

        t = new Thread(this, "analyze");

        t.start();
    }

}
