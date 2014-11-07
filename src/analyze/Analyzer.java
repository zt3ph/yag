/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import utils.FUtils;

/**
 *
 * @author steph
 */
public class Analyzer {

    Map<String, Map<String, Map<String, List<Tuple<Integer, Integer>>>>> totInf;

    void analyze(String data, final String filename) {

        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

        parser.setSource(data.toCharArray());

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        cu.accept(new ASTVisitor() {
            Set names = new HashSet();

            public boolean visit(MethodInvocation node) {

                String functionName = node.getName().toString().toLowerCase();

                if (functionName.contains("query")
                        || functionName.contains("exec")
                        || functionName.contains("write")
                        || functionName.contains("open")
                        || functionName.contains("read")
                        || functionName.contains("send")
                        || functionName.contains("xml")
                        || functionName.contains("xpath")
                        || functionName.contains("shell")
                        || functionName.contains("file")
                        || functionName.contains("stream")
                        || functionName.contains("reciev")) {

                    if (totInf.get("FUNC") == null) {
                        totInf.put("FUNC", new TreeMap<String, Map<String, List<Tuple<Integer, Integer>>>>());
                    }
                    if (totInf.get("FUNC").get(node.getName().toString()) == null) {
                        totInf.get("FUNC").put(node.getName().toString(), new TreeMap<String, List<Tuple<Integer, Integer>>>());
                    }
                    if (totInf.get("FUNC").get(node.getName().toString()).get(filename) == null) {
                        totInf.get("FUNC").get(node.getName().toString()).put(filename, new ArrayList());
                    }
                    totInf.get("FUNC").get(node.getName().toString()).get(filename).add(new Tuple(cu.getLineNumber(node.getStartPosition()), cu.getColumnNumber(node.getStartPosition())));

                }

                return true;
            }

        });
    }

    public void analyze(String sourceFolderName, javax.swing.JProgressBar progressInfo, javax.swing.JTextArea textInfo, DefaultTreeModel model) {

        totInf = new TreeMap<String, Map<String, Map<String, List<Tuple<Integer, Integer>>>>>();
        int i;
        List<String> data = new ArrayList<>();
        final File folder = new File(sourceFolderName);
        data.addAll(FUtils.listFilesForFolder(folder));

        String content = "";
        double step = (double) 100 / (double) data.size();
        double value = 0;
        progressInfo.setValue(0);
        for (String tmp : data) {
            value += step;

            progressInfo.setValue((int) Math.round(value));
            textInfo.setText(textInfo.getText() + "\n" + tmp);

            if (!FUtils.isJavaFile(tmp)) {
                continue;
            }
            try {

                content = FUtils.readFile(tmp, Charset.defaultCharset());
                analyze(content, tmp);
            } catch (Exception ex) {
                continue;
            }

        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload(root);
        DefaultMutableTreeNode Type;
        DefaultMutableTreeNode Expr;
        DefaultMutableTreeNode File;
        DefaultMutableTreeNode Tmp;

        for (Map.Entry<String, Map<String, Map<String, List<Tuple<Integer, Integer>>>>> entry : totInf.entrySet()) {

            Type = new DefaultMutableTreeNode(entry.getKey());
            root.add(Type);

            for (Map.Entry<String, Map<String, List<Tuple<Integer, Integer>>>> entry2 : entry.getValue().entrySet()) {
                Expr = new DefaultMutableTreeNode(entry2.getKey());
                Type.add(Expr);
                for (Map.Entry<String, List<Tuple<Integer, Integer>>> entry3 : entry2.getValue().entrySet()) {
                    File = new DefaultMutableTreeNode(entry3.getKey());
                    Expr.add(File);
                    for (Tuple<Integer, Integer> tmp : entry3.getValue()) {
                        Tmp = new DefaultMutableTreeNode(tmp.left + ":" + tmp.right);
                        File.add(Tmp);
                    }
                }

            }

        }
        model.reload(root);

        progressInfo.setValue(0);
    }

}
