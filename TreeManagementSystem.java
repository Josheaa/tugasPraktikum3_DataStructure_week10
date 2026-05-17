import java.util.*;

// ============================================================
//   SISTEM MANAJEMEN DATA BERBASIS TREE
//   BST + Expression Tree
//   (Interaktif – semua input dari user via Scanner)
// ============================================================

// ─────────────────────────────────────────
//  1. NODE BST
// ─────────────────────────────────────────
class BSTNode {
    int data;
    BSTNode left, right;
    BSTNode(int data) { this.data = data; }
}

// ─────────────────────────────────────────
//  2. BINARY SEARCH TREE
// ─────────────────────────────────────────
class BST {
    private BSTNode root;

    public void insert(int data) { root = insertRec(root, data); }
    private BSTNode insertRec(BSTNode node, int data) {
        if (node == null) return new BSTNode(data);
        if (data < node.data)       node.left  = insertRec(node.left,  data);
        else if (data > node.data)  node.right = insertRec(node.right, data);
        else System.out.println("  [!] Nilai " + data + " sudah ada dalam BST.");
        return node;
    }

    public boolean search(int data) { return searchRec(root, data); }
    private boolean searchRec(BSTNode node, int data) {
        if (node == null) return false;
        if (data == node.data) return true;
        return data < node.data ? searchRec(node.left, data) : searchRec(node.right, data);
    }

    public void delete(int data) { root = deleteRec(root, data); }
    private BSTNode deleteRec(BSTNode node, int data) {
        if (node == null) { System.out.println("  [!] Nilai " + data + " tidak ditemukan."); return null; }
        if (data < node.data)       node.left  = deleteRec(node.left,  data);
        else if (data > node.data)  node.right = deleteRec(node.right, data);
        else {
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;
            BSTNode succ = node.right;
            while (succ.left != null) succ = succ.left;
            node.data  = succ.data;
            node.right = deleteRec(node.right, succ.data);
        }
        return node;
    }

    public boolean isEmpty() { return root == null; }

    public void inOrder()    { System.out.print("  InOrder    : "); inOrderRec(root);   System.out.println(); }
    public void preOrder()   { System.out.print("  PreOrder   : "); preOrderRec(root);  System.out.println(); }
    public void postOrder()  { System.out.print("  PostOrder  : "); postOrderRec(root); System.out.println(); }
    public void levelOrder() {
        System.out.print("  LevelOrder : ");
        if (root == null) { System.out.println("(kosong)"); return; }
        Queue<BSTNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            BSTNode c = q.poll();
            System.out.print(c.data + " ");
            if (c.left  != null) q.add(c.left);
            if (c.right != null) q.add(c.right);
        }
        System.out.println();
    }
    private void inOrderRec(BSTNode n)   { if (n==null) return; inOrderRec(n.left);  System.out.print(n.data+" "); inOrderRec(n.right); }
    private void preOrderRec(BSTNode n)  { if (n==null) return; System.out.print(n.data+" "); preOrderRec(n.left);  preOrderRec(n.right); }
    private void postOrderRec(BSTNode n) { if (n==null) return; postOrderRec(n.left); postOrderRec(n.right); System.out.print(n.data+" "); }

    public void printTree() {
        if (root == null) { System.out.println("  (tree kosong)"); return; }
        System.out.println("  Struktur BST:");
        printTree(root, "  ", true);
    }
    private void printTree(BSTNode n, String prefix, boolean isLeft) {
        if (n == null) return;
        System.out.println(prefix + (isLeft ? "├── " : "└── ") + n.data);
        printTree(n.left,  prefix + (isLeft ? "│   " : "    "), true);
        printTree(n.right, prefix + (isLeft ? "│   " : "    "), false);
    }
}

// ─────────────────────────────────────────
//  3. NODE EXPRESSION TREE
// ─────────────────────────────────────────
class ExprNode {
    String value;
    ExprNode left, right;
    ExprNode(String v) { this.value = v; }
}

// ─────────────────────────────────────────
//  4. EXPRESSION TREE
// ─────────────────────────────────────────
class ExpressionTree {
    private ExprNode root;

    public void buildFromInfix(String expression) {
        root = buildFromPostfix(infixToPostfix(tokenize(expression)));
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) num.append(expr.charAt(i++));
                tokens.add(num.toString());
            } else { tokens.add(String.valueOf(c)); i++; }
        }
        return tokens;
    }

    private List<String> infixToPostfix(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        for (String t : tokens) {
            if (isNum(t)) { out.add(t); }
            else if (t.equals("(")) { ops.push(t); }
            else if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                if (!ops.isEmpty()) ops.pop();
            } else if (isOp(t)) {
                while (!ops.isEmpty() && isOp(ops.peek()) && prec(ops.peek()) >= prec(t)) out.add(ops.pop());
                ops.push(t);
            }
        }
        while (!ops.isEmpty()) out.add(ops.pop());
        return out;
    }

    private ExprNode buildFromPostfix(List<String> postfix) {
        Deque<ExprNode> stack = new ArrayDeque<>();
        for (String t : postfix) {
            ExprNode n = new ExprNode(t);
            if (isOp(t)) { n.right = stack.pop(); n.left = stack.pop(); }
            stack.push(n);
        }
        return stack.isEmpty() ? null : stack.pop();
    }

    public double evaluate() { return evalRec(root); }
    private double evalRec(ExprNode n) {
        if (n == null) throw new RuntimeException("Node null!");
        if (!isOp(n.value)) return Double.parseDouble(n.value);
        double l = evalRec(n.left), r = evalRec(n.right);
        return switch (n.value) {
            case "+" -> l + r;
            case "-" -> l - r;
            case "*" -> l * r;
            case "/" -> { if (r == 0) throw new ArithmeticException("Pembagian dengan nol!"); yield l / r; }
            default  -> throw new RuntimeException("Operator tidak dikenal: " + n.value);
        };
    }

    // ── ASCII Visual Tree ──
    public void printVisual() {
        System.out.println("  Ekspresi dalam bentuk tree:");
        if (root == null) { System.out.println("  (kosong)"); return; }

        // BFS kumpulkan per level
        List<List<ExprNode>> levels = new ArrayList<>();
        Queue<ExprNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int sz = queue.size();
            List<ExprNode> level = new ArrayList<>();
            boolean hasChild = false;
            for (int i = 0; i < sz; i++) {
                ExprNode curr = queue.poll();
                level.add(curr);
                if (curr != null) {
                    queue.add(curr.left);
                    queue.add(curr.right);
                    if (curr.left != null || curr.right != null) hasChild = true;
                } else { queue.add(null); queue.add(null); }
            }
            levels.add(level);
            if (!hasChild) break;
        }

        int depth = levels.size();
        int maxW  = (int) Math.pow(2, depth - 1) * 6;

        for (int d = 0; d < depth; d++) {
            List<ExprNode> row = levels.get(d);
            int space = maxW / row.size();

            // Konektor
            if (d > 0) {
                StringBuilder conn = new StringBuilder("  ");
                List<ExprNode> prev = levels.get(d - 1);
                for (ExprNode parent : prev) {
                    int half = space;
                    if (parent != null) {
                        for (int s = 0; s < half / 2 - 1; s++) conn.append(" ");
                        conn.append("/");
                        for (int s = 0; s < half - 2; s++) conn.append(" ");
                        conn.append("\\");
                        for (int s = 0; s < half / 2 - 1; s++) conn.append(" ");
                    } else { for (int s = 0; s < half * 2; s++) conn.append(" "); }
                }
                System.out.println(conn);
            }

            // Node
            StringBuilder line = new StringBuilder("  ");
            for (ExprNode node : row) {
                String label = (node == null) ? " " : (isOp(node.value) ? "(" + node.value + ")" : node.value);
                int pad = space - label.length();
                int padL = pad / 2, padR = pad - padL;
                for (int s = 0; s < padL; s++) line.append(" ");
                line.append(label);
                for (int s = 0; s < padR; s++) line.append(" ");
            }
            System.out.println(line);
        }
    }

    public void inOrder()   { System.out.print("  InOrder   : "); ioRec(root);  System.out.println(); }
    public void preOrder()  { System.out.print("  PreOrder  : "); prRec(root);  System.out.println(); }
    public void postOrder() { System.out.print("  PostOrder : "); poRec(root);  System.out.println(); }
    private void ioRec(ExprNode n) { if(n==null)return; if(n.left!=null)System.out.print("("); ioRec(n.left); System.out.print(n.value); ioRec(n.right); if(n.right!=null)System.out.print(")"); }
    private void prRec(ExprNode n) { if(n==null)return; System.out.print(n.value+" "); prRec(n.left); prRec(n.right); }
    private void poRec(ExprNode n) { if(n==null)return; poRec(n.left); poRec(n.right); System.out.print(n.value+" "); }

    public boolean isBuilt() { return root != null; }
    private boolean isNum(String s) { try { Double.parseDouble(s); return true; } catch (NumberFormatException e) { return false; } }
    private boolean isOp(String s)  { return "+".equals(s)||"-".equals(s)||"*".equals(s)||"/".equals(s); }
    private int prec(String op)     { return ("*".equals(op)||"/".equals(op)) ? 2 : 1; }
}

// ─────────────────────────────────────────
//  5. MAIN – Menu Interaktif
// ─────────────────────────────────────────
public class TreeManagementSystem {

    static final Scanner sc = new Scanner(System.in);

    static void line()  { System.out.println("─".repeat(52)); }
    static void dline() { System.out.println("═".repeat(52)); }

    /** Baca integer dari user dengan validasi loop */
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Masukkan angka bulat yang valid."); }
        }
    }

    // ══════════════════════════════════════
    //  MENU BST
    // ══════════════════════════════════════
    static void menuBST() {
        BST bst = new BST();
        while (true) {
            dline();
            System.out.println("  MENU BINARY SEARCH TREE (BST)");
            line();
            System.out.println("  1. Insert elemen");
            System.out.println("  2. Delete elemen");
            System.out.println("  3. Search elemen");
            System.out.println("  4. Traversal");
            System.out.println("  5. Tampilkan struktur tree");
            System.out.println("  0. Kembali ke menu utama");
            line();
            int pilih = readInt("  Pilihan: ");

            switch (pilih) {
                case 1 -> {
                    int val = readInt("  Masukkan nilai yang akan diinsert: ");
                    bst.insert(val);
                    System.out.println("  Insert(" + val + ") selesai.");
                    bst.printTree();
                }
                case 2 -> {
                    if (bst.isEmpty()) { System.out.println("  [!] Tree kosong."); break; }
                    int val = readInt("  Masukkan nilai yang akan dihapus: ");
                    bst.delete(val);
                    bst.printTree();
                }
                case 3 -> {
                    int val = readInt("  Masukkan nilai yang dicari: ");
                    System.out.println("  Search(" + val + ") → " +
                            (bst.search(val) ? "✓ DITEMUKAN" : "✗ TIDAK DITEMUKAN"));
                }
                case 4 -> {
                    if (bst.isEmpty()) { System.out.println("  [!] Tree kosong."); break; }
                    System.out.println("  Pilih traversal:");
                    System.out.println("    1. InOrder    2. PreOrder");
                    System.out.println("    3. PostOrder  4. LevelOrder  5. Semua");
                    int t = readInt("  Pilihan traversal: ");
                    if (t==1||t==5) bst.inOrder();
                    if (t==2||t==5) bst.preOrder();
                    if (t==3||t==5) bst.postOrder();
                    if (t==4||t==5) bst.levelOrder();
                }
                case 5 -> bst.printTree();
                case 0 -> { return; }
                default -> System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ══════════════════════════════════════
    //  MENU EXPRESSION TREE
    // ══════════════════════════════════════
    static void menuExprTree() {
        ExpressionTree et = new ExpressionTree();
        String lastExpr = "";

        while (true) {
            dline();
            System.out.println("  MENU EXPRESSION TREE");
            line();
            System.out.println("  1. Masukkan ekspresi matematika");
            System.out.println("  2. Evaluasi ekspresi");
            System.out.println("  3. Tampilkan tree visual");
            System.out.println("  4. Traversal expression tree");
            System.out.println("  0. Kembali ke menu utama");
            line();
            int pilih = readInt("  Pilihan: ");

            switch (pilih) {
                case 1 -> {
                    System.out.print("  Masukkan ekspresi matematika: ");
                    lastExpr = sc.nextLine().trim();
                    try {
                        et.buildFromInfix(lastExpr);
                        System.out.println("  Expression tree berhasil dibuat.");
                        et.printVisual();
                        System.out.printf("  Hasil Evaluasi: %.6g%n", et.evaluate());
                    } catch (Exception e) {
                        System.out.println("  [!] Error: " + e.getMessage());
                        lastExpr = "";
                    }
                }
                case 2 -> {
                    if (!et.isBuilt()) { System.out.println("  [!] Masukkan ekspresi dulu (opsi 1)."); break; }
                    try {
                        double hasil = et.evaluate();
                        System.out.printf("  Hasil Evaluasi \"%s\"%n  = %.6g%n", lastExpr, hasil);
                        if (hasil == (long) hasil) System.out.println("  (bilangan bulat: " + (long)hasil + ")");
                    } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
                }
                case 3 -> {
                    if (!et.isBuilt()) { System.out.println("  [!] Masukkan ekspresi dulu (opsi 1)."); break; }
                    et.printVisual();
                }
                case 4 -> {
                    if (!et.isBuilt()) { System.out.println("  [!] Masukkan ekspresi dulu (opsi 1)."); break; }
                    et.inOrder(); et.preOrder(); et.postOrder();
                }
                case 0 -> { return; }
                default -> System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ══════════════════════════════════════
    //  MAIN MENU
    // ══════════════════════════════════════
    public static void main(String[] args) {
        System.out.println();
        dline();
        System.out.println("   SISTEM MANAJEMEN DATA BERBASIS TREE");
        System.out.println("   BST · Expression Tree");
        dline();

        while (true) {
            System.out.println();
            System.out.println("  MENU UTAMA");
            line();
            System.out.println("  1. Binary Search Tree (BST)");
            System.out.println("  2. Expression Tree");
            System.out.println("  0. Keluar");
            line();
            int pilih = readInt("  Pilihan: ");

            switch (pilih) {
                case 1 -> menuBST();
                case 2 -> menuExprTree();
                case 0 -> {
                    System.out.println();
                    dline();
                    System.out.println("  Program selesai. Terima kasih!");
                    dline();
                    sc.close();
                    return;
                }
                default -> System.out.println("  [!] Pilihan tidak valid. Coba lagi.");
            }
        }
    }
}