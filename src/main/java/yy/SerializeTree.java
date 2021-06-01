package yy;

/**
 * 序列化和反序列化二叉树
 */
public class SerializeTree {
    /**
     * 序列化二叉树
     * 递归遍历二叉树的节点，空节点使用#代替，节点之间使用逗号隔开，返回字符串
     */
    String Serialize(TreeNode root) {
        return root.val + "," + Serialize(root.left) + "," + Serialize(root.right);
    }

    /**
     * 反序列化二叉树
     * 设置序号index，将字符串根据逗号分割为数组，根据index的值来设置树节点的val，如果节点的值为#，则返回空的树节点。
     */
    private int index = -1;
    TreeNode Deserialize(String str) {
        String[] s = str.split(",");//将序列化之后的序列用，分隔符转化为数组
        index++;//索引每次加一
        int len = s.length;
        if (index > len) {
            return null;
        }
        TreeNode treeNode = null;
        if (!s[index].equals("#")) {//不是叶子节点 继续走 是叶子节点出递归
            treeNode = new TreeNode(Integer.parseInt(s[index]));
            treeNode.left = Deserialize(str);
            treeNode.right = Deserialize(str);
        }
        return treeNode;
    }

    public static void main(String[] args) {
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(2);
        TreeNode treeNode3 = new TreeNode(3);
        TreeNode treeNode4 = new TreeNode(4);
        TreeNode treeNode5 = new TreeNode(5);
        TreeNode treeNode6 = new TreeNode(6);

        treeNode1.left = treeNode2;
        treeNode1.right = treeNode3;

        treeNode2.left = treeNode4;
        treeNode3.left = treeNode5;
        treeNode3.right = treeNode6;

        SerializeTree serializeTree = new SerializeTree();

        String str = serializeTree.Serialize(treeNode1);
        TreeNode treeNode = serializeTree.Deserialize(str);
    }

    public static class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;

        }

    }
}
